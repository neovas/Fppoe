package tgriffith.itas.fppoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * This activity starts when a ladder entry is tapped. It gets the character and account name from
 * the mainactivity. It then queries the api for character specific info such as gear and gems.
 */
public class CharacterInfo extends AppCompatActivity {

    // The charname and account selected from the ladder
    String charName;
    String acctName;

    // Storing each item in this array.
    ArrayList<Item> itemArray = new ArrayList<Item>();
    ArrayList<Item> sortedList;

    private RequestQueue queue;
    // returns all item info for a specific character
    private String charInfoUrl;

    LinearLayout llWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_info);
        Intent flagIntent = getIntent();

        queue = Volley.newRequestQueue(this);

        // Grabbing the values from our mainactivity's selected entry on ladder
        charName = flagIntent.getStringExtra("characterName");
        acctName = flagIntent.getStringExtra("accountName");
        String charLevel = flagIntent.getStringExtra("characterLevel");
        String charClass = flagIntent.getStringExtra("characterClass");

        TextView charAccTv = findViewById(R.id.charAccTv);
        TextView classLevelTv = findViewById(R.id.classLevelTv);

        llWrapper = findViewById(R.id.llItemsContainer);

        charAccTv.setText(charName + " - " + acctName);
        classLevelTv.setText(charLevel + " - " + charClass);


        Log.i("charInfo", "Character Name: " + charName + " | Account Name: " + acctName + " |Level: " + charLevel + " |Class: " + charClass);

        request();

    }

    public void request() {

        charInfoUrl = "https://www.pathofexile.com/character-window/get-items?character=" + charName + "&accountName=" + acctName;
        Log.i("charInfo", charInfoUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, charInfoUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        parseCharacterInfo(response);
                        itemArraySort();
                        populateCharacterInfo();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("charInfo", "Error Code: " + error.networkResponse.statusCode);
                        Log.i("charInfo", "error :" + error.toString());
                        /**
                         * Check for error 503 network response. This is the error thrown when
                         * game/website is down. Toast the user telling them servers cannot be reached.
                         * */
                        if (error.networkResponse.statusCode == 503) {
                            Toast.makeText(getApplicationContext(), "Game servers cannot be reached currently. Try again later.", Toast.LENGTH_LONG).show();
                        }
                        /**
                         * 403 response generally is due to the account being marked as private.
                         * Close the activity
                         * */
                        if (error.networkResponse.statusCode == 403) {
                            Toast.makeText(getApplicationContext(), "Cannot access account. Marked as private.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });

        queue.add(jsonObjectRequest);
    }

    public void populateCharacterInfo() {

        for (int i = 0; i < sortedList.size(); i++) {
            /**
             * Create the layouts and views for an item entry
             * */
            // The LL which contains the icon and item info
            LinearLayout parent = new LinearLayout(getApplicationContext());
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.HORIZONTAL);
            parent.setPadding(0, 0, 0, 10);

            // Add our parent ll to our grandpappy ll.
            llWrapper.addView(parent);

            // Stores the image icon
            ImageView itemIconIv = new ImageView(getApplicationContext());
            // The LL which holds the item information
            LinearLayout childLl = new LinearLayout(getApplicationContext());
            childLl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            childLl.setOrientation(LinearLayout.VERTICAL);

            // Add the child elements to our parent LL
            parent.addView(itemIconIv);
            parent.addView(childLl);

            //The textviews for storing item info
            TextView iName = new TextView(getApplicationContext());

            Item item = sortedList.get(i);

            // image icon url
            String itemIcon = item.imageUrl;
            // the json has backslashes that break url, this removes them.
            itemIcon = itemIcon.replace("\\", "");
            //Log.i("charInfo", itemIcon);

            // Display the item name and item type
            iName.setText(item.name + " " + item.getTypeLine());
            // Add the name to the layout
            childLl.addView(iName);


            // Set dimensions of our image in dp
            int widthHeightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            itemIconIv.getLayoutParams().height = widthHeightDp;
            itemIconIv.getLayoutParams().width = widthHeightDp;
            // Loads image into imageview by url
            Picasso.with(getApplicationContext()).load(itemIcon).into(itemIconIv);

            /*
             * Add Enchant Mods
             * */
            JSONArray enchantMods = new JSONArray();
            if (item.getEnchantMods().length() > 0) {
                //The implicitMods
                enchantMods = item.getEnchantMods();
                //Log.i("charInfo", implicitMods.toString());

                //Loop through the implicitMods
                for (int j = 0; j < enchantMods.length(); j++) {
                    TextView iTest = new TextView(getApplicationContext());
                    try {
                        iTest.setText(enchantMods.get(j).toString());
                    } catch (JSONException e) {
                        Log.d("json", "Error: " + e);
                    }
                    childLl.addView(iTest);
                }
            }

            /*
             * IMPLICIT MODS: Add each mod if any to the layout
             * */
            JSONArray implicitMods = new JSONArray();
            if (item.getImplicitMods().length() > 0) {
                //The implicitMods
                implicitMods = item.getImplicitMods();
                Log.i("implicitMods", "Implicit: " + implicitMods.toString());


                //Loop through the implicitMods
                for (int j = 0; j < implicitMods.length(); j++) {

                    TextView iTest = new TextView(getApplicationContext());
                    try {
                        iTest.setText(implicitMods.get(j).toString());
                    } catch (JSONException e) {
                        Log.d("json", "Error: " + e);
                    }
                    childLl.addView(iTest);
                }
            }

            /*
             * EXPLICIT MODS: Add each mod if any at all to the layout.
             * */
            JSONArray explicitMods = new JSONArray();
            if (item.getExplicitMods().length() > 0) {
                explicitMods = item.getExplicitMods();
                Log.i("implicitMods", "Explicit: " + explicitMods.toString());

                //Loop through the explicitMods
                for (int j = 0; j < explicitMods.length(); j++) {

                    TextView iTest = new TextView(getApplicationContext());
                    try {
                        iTest.setText(explicitMods.get(j).toString());
                    } catch (JSONException e) {
                        Log.d("json", "Error: " + e);
                    }
                    childLl.addView(iTest);

                }
            }

            /*
             * Add crafted Mods
             * */
            JSONArray craftMods = new JSONArray();
            if (item.getCraftedMods().length() > 0) {
                //The implicitMods
                craftMods = item.getCraftedMods();
                //Log.i("charInfo", implicitMods.toString());


                //Loop through the implicitMods
                for (int j = 0; j < craftMods.length(); j++) {
                    TextView iTest = new TextView(getApplicationContext());
                    try {
                        iTest.setText(craftMods.get(j).toString());
                    } catch (JSONException e) {
                        Log.d("json", "Error: " + e);
                    }
                    childLl.addView(iTest);
                }
            }
        }
    }

    /*
     * Breaks down the characterInfo JSON into Item objects which are then put into an arraylist.
     * */
    public void parseCharacterInfo(JSONObject response) {
        try {
            //Access the items array of the charInfo request
            JSONArray entriesArray = response.getJSONArray("items");

            // If entriesArray is empty that means no items equipped.
            // End function and exit activity
            if (entriesArray.length() == 0) {
                Toast.makeText(getApplicationContext(), "No equipped items to show on " + charName, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // initializing the values we will be using.
            String itemName = "";
            String itemType = "";
            // the equipment slot
            String inventoryId = "";
            String itemIcon = "";

            // Loop through every item in the json
            for (int itemCounter = 0; itemCounter <= entriesArray.length(); itemCounter++) {
                // the individual item objects
                JSONObject itemInfo = entriesArray.getJSONObject(itemCounter);

                itemName = itemInfo.getString("name");
                Log.i("charInfo", "Item Name: " + itemName);
                itemType = itemInfo.getString("typeLine");
                inventoryId = itemInfo.getString("inventoryId");

                // image icon url
                itemIcon = itemInfo.getString("icon");
                // the json has backslashes that break url, this removes them.
                itemIcon = itemIcon.replace("\\", "");

                /*
                 * IMPLICIT MODS: Add each mod if any to the layout
                 * */
                JSONArray implicitMods = new JSONArray();
                if (itemInfo.has("implicitMods")) {
                    //The implicitMods
                    implicitMods = itemInfo.getJSONArray("implicitMods");

                }

                /*
                 * EXPLICIT MODS: Add each mod if any at all to the layout.
                 * */
                JSONArray explicitMods = new JSONArray();
                if (itemInfo.has("explicitMods")) {
                    explicitMods = itemInfo.getJSONArray("explicitMods");
                    Log.i("charInfo", explicitMods.toString());

                }

                /**
                 *  ENCHANTMENTS: Check all for enchants
                 * */
                JSONArray enchantMods = new JSONArray();
                if (itemInfo.has("enchantMods")) {
                    enchantMods = itemInfo.getJSONArray("enchantMods");
                    Log.i("charInfo", enchantMods.toString());
                }

                /**
                 *  Crafted Mods: Check all for crafted mods
                 * */
                JSONArray craftMods = new JSONArray();
                if (itemInfo.has("craftedMods")) {
                    craftMods = itemInfo.getJSONArray("craftedMods");
                    Log.i("charInfo", craftMods.toString());
                }

                /**
                 * Find socketed jewels and/or gems
                 * */
                JSONArray socketedItems = new JSONArray();
                if (itemInfo.has("socketedItems")) {
                    socketedItems = itemInfo.getJSONArray("socketedItems");


                    // check there are actual socketedItems and not an empty array.
                    if (socketedItems.length() > 0) {

                        String socketedItemName = "";
                        String socketedItemType = "";
                        String socketedItemInventoryId = "";
                        String socketedItemIcon = "";
                        // loop through all socketed items
                        for (int x = 0; x < socketedItems.length(); x++) {
                            //individual socketed item
                            JSONObject socketedItemInfo = socketedItems.getJSONObject(x);

                            // Checking for abyss jewels which will be handled like normal items
                            if (socketedItemInfo.has("abyssJewel")) {


                                socketedItemName = socketedItemInfo.getString("name");
                                socketedItemType = socketedItemInfo.getString("typeLine");
                                socketedItemInventoryId = "abyssJewel";
                                // image icon url
                                socketedItemIcon = socketedItemInfo.getString("icon");
                                // the json has backslashes that break url, this removes them.
                                socketedItemIcon = socketedItemIcon.replace("\\", "");

                                /*
                                 * IMPLICIT MODS: Add each mod if any to the layout
                                 * */
                                JSONArray socketedImplicitMods = new JSONArray();
                                if (itemInfo.has("implicitMods")) {
                                    //The implicitMods
                                    socketedImplicitMods = itemInfo.getJSONArray("implicitMods");
                                }

                                /*
                                 * EXPLICIT MODS: Add each mod if any at all to the layout.
                                 * */
                                JSONArray socketedExplicitMods = new JSONArray();
                                if (itemInfo.has("explicitMods")) {
                                    socketedExplicitMods = itemInfo.getJSONArray("explicitMods");
                                }

                                /**
                                 *  ENCHANTMENTS: Check all for enchants
                                 * */
                                JSONArray socketedEnchantMods = new JSONArray();
                                if (itemInfo.has("enchantMods")) {
                                    socketedEnchantMods = itemInfo.getJSONArray("enchantMods");
                                    Log.i("charInfo", enchantMods.toString());
                                }

                                /**
                                 *  Crafted Mods: Check all for crafted mods
                                 * */
                                JSONArray socketedCraftMods = new JSONArray();
                                if (itemInfo.has("craftedMods")) {
                                    socketedCraftMods = itemInfo.getJSONArray("craftedMods");
                                    Log.i("charInfo", craftMods.toString());
                                }

                                Item socketedIndividualItem = new Item(socketedItemIcon, socketedItemName, socketedItemType, socketedImplicitMods, socketedExplicitMods, socketedItemInventoryId, socketedEnchantMods, socketedCraftMods);
                                itemArray.add(socketedIndividualItem);
                            }
                            Log.i("socketItem", socketedItemInfo.toString());
                        }

                    }
                }

                // Add the item to our itemArray so we can later sort the order of them by values
                Item individualItem = new Item(itemIcon, itemName, itemType, implicitMods, explicitMods, inventoryId, enchantMods, craftMods);
                itemArray.add(individualItem);


            }
        } catch (JSONException e) {
            Log.d("charInfo", "Error: " + e);

        }

    }

    // Requests the passive tree information for the purpose of
    public void requestPassives() {

        charInfoUrl = "https://www.pathofexile.com/character-window/get-passive-skills?character=" + charName + "&accountName=" + acctName;
        Log.i("charInfo", charInfoUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, charInfoUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //parsePassiveInfo(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("charInfo", "Error Code: " + error.networkResponse.statusCode);
                        Log.i("charInfo", "error :" + error.toString());
                        /**
                         * Check for error 503 network response. This is the error thrown when
                         * game/website is down. Toast the user telling them servers cannot be reached.
                         * */
                        if (error.networkResponse.statusCode == 503) {
                            Toast.makeText(getApplicationContext(), "Game servers cannot be reached currently. Try again later.", Toast.LENGTH_LONG).show();
                        }
                        /**
                         * 403 response generally is due to the account being marked as private.
                         * Close the activity
                         * */
                        if (error.networkResponse.statusCode == 403) {
                            Toast.makeText(getApplicationContext(), "Cannot access account. Marked as private.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });

        queue.add(jsonObjectRequest);
    }

    /*
     * Super inefficiently sorts items into order of Weapon, Offhand, Helm, etc.
     * Simply just looping through the array of items and if it finds the type of item we want to
     * have it adds it to our new arraylist. Then does another loop looking for another item.
     *
     * It works.
     * */
    public void itemArraySort() {

        sortedList = new ArrayList<Item>();

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Weapon")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Offhand")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Helm")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("BodyArmour")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Gloves")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Boots")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Belt")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Amulet")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Ring")) {
                sortedList.add(item);

            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Ring2")) {
                sortedList.add(item);

            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Flask")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("abyssJewel")) {
                sortedList.add(item);
            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Weapon2")) {
                sortedList.add(item);

            }
        }

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = itemArray.get(i);

            if (item.getInventoryId().equals("Offhand2")) {
                sortedList.add(item);

            }
        }

        // Logging the list of sorted items
        for (int i = 0; i < sortedList.size(); i++) {
            Log.i("sort", "Sorted: " + sortedList.get(i).getInventoryId() + " " + i);
        }
    }

}


