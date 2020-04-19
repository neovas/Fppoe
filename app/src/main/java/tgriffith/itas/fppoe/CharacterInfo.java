package tgriffith.itas.fppoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.exit;

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
    ArrayList<Item> sortedList = new ArrayList<>();
    ArrayList<Item> sortedListCopy = new ArrayList<>();
    ArrayList<Gem> gemList = new ArrayList<>();

    private RequestQueue queue;
    // returns all item info for a specific character
    private String charInfoUrl;

    // Holds values of the list view itself.
    LinearLayout llWrapper;
    ListView lvWrapper;

    // list adapter for equipment
    EquipCustomListAdapter eqclAdapter;
    // adapter for showing our gems
    GemCustomListAdapter gemAdapter;


    // Toggle to true after running parseCharacterInfo.
    boolean characterGearDone = false;

    //Our button
    Button toggleButton;
    // False for on gear screen. True for gem screen.
    boolean toggleButtonFlag = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        toggleButton = findViewById(R.id.toggleButton);

        //llWrapper = findViewById(R.id.llItemsContainer);
        lvWrapper = findViewById(R.id.eqListview);
        lvWrapper.setDivider(null);
        lvWrapper.setDividerHeight(0);

        charAccTv.setText(charName);
        classLevelTv.setText(charLevel + " - " + charClass);


        Log.i("charInfo", "Character Name: " + charName + " | Account Name: " + acctName + " |Level: " + charLevel + " |Class: " + charClass);
        // provide our custom adapter the sorted item information

        eqclAdapter = new EquipCustomListAdapter(this, sortedList);
        gemAdapter = new GemCustomListAdapter(this, gemList);

        // set to our equipment adapter by default. Will toggle this with button.
        lvWrapper.setAdapter(eqclAdapter);
        request();

        // When our button is tapped change from gear screen to gem screen.
        // When tapped again swap back.
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButtonFlag == false) {
                    lvWrapper.setAdapter(gemAdapter);
                    toggleButton.setText("View Gear");
                    toggleButtonFlag = true;
                } else {
                    lvWrapper.setAdapter(eqclAdapter);
                    toggleButton.setText("View Gems");

                    toggleButtonFlag = false;
                }

            }
        });
        //eqclAdapter.notifyDataSetChanged();



    }

    public void request() {

        charInfoUrl = "https://www.pathofexile.com/character-window/get-items?character=" + charName + "&accountName=" + acctName;
        Log.i("charInfo", charInfoUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, charInfoUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        parseCharacterInfo(response);
                        //itemArraySort();
                        //populateCharacterInfo();

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
                        // Resource not found
                        if (error.networkResponse.statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "404. Resource not found.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });

        queue.add(jsonObjectRequest);
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
            for (int itemCounter = 0; itemCounter < entriesArray.length(); itemCounter++) {

                /**
                 * Optional Fields not all items will have. Set after creating the item
                 * */
                String itemQuality = "";
                String evasionRating = "";
                String energyShield = "";
                String armour = "";
                String physicalDamage = "";
                String criticalChance = "";
                String attackSpeed = "";
                String elementalDamage = "";

                // the individual item objects
                JSONObject itemInfo = entriesArray.getJSONObject(itemCounter);

                itemName = itemInfo.getString("name");
                //Log.i("charInfo", "Item Name: " + itemName);
                itemType = itemInfo.getString("typeLine");
                if (itemInfo.has("abyssJewel")) {
                    inventoryId = "abyssJewel";
                    Log.i("jewelz", "AbyssJewel: " + itemName);
                } else {
                    inventoryId = itemInfo.getString("inventoryId");
                }


                // image icon url
                itemIcon = itemInfo.getString("icon");
                // the json has backslashes that break url, this removes them.
                itemIcon = itemIcon.replace("\\", "");

                // Store the list of gems for the item
                ArrayList<Gem> gems = new ArrayList<>();

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
                    //Log.i("charInfo", explicitMods.toString());

                }

                /**
                 *  ENCHANTMENTS: Check all for enchants
                 * */
                JSONArray enchantMods = new JSONArray();
                if (itemInfo.has("enchantMods")) {
                    enchantMods = itemInfo.getJSONArray("enchantMods");
                    //Log.i("charInfo", enchantMods.toString());
                }

                /**
                 *  Crafted Mods: Check all for crafted mods
                 * */
                JSONArray craftMods = new JSONArray();
                if (itemInfo.has("craftedMods")) {
                    craftMods = itemInfo.getJSONArray("craftedMods");
                    //Log.i("charInfo", craftMods.toString());
                }

                // the different rarities
                // frametype 0 = common, 1 = magic, 2 = rare, 3 = unique
                String itemRarity = "Common";
                if (itemInfo.has("frameType")) {
                    int frameType = itemInfo.getInt("frameType");

                    if (frameType == 0) {
                        itemRarity = "Common";
                    } else if (frameType == 1) {
                        itemRarity = "Magic";
                    } else if (frameType == 2) {
                        itemRarity = "Rare";
                    } else if (frameType == 3) {
                        itemRarity = "Unique";
                    }
                }

                /**
                 * Item Properties:
                 * This contains quality, evasion rating, energy shield, armour in armour pieces
                 * In weapons it has Physical Damage, Elemental Damage, crit chance, attacks per second,
                 * weapon range. Also contains item type, aka "One Handed Sword" as opposed to just
                 * the item base.
                 *
                 * Rings/Amulets/Belts do not have this information unless quality has been * As all values here are not always in
                 * every item these will not be set in the constructor and instead set after creation.
                 * */
                if (itemInfo.has("properties")) {
                    JSONArray itemProperties = itemInfo.getJSONArray("properties");
                    // An individual property
                    JSONObject property;

                    // Go through all property objects grabbing the fields we need.
                    for (int z = 0; z < itemProperties.length(); z++) {
                        property = itemProperties.getJSONObject(z);

                        // if we have the name field then check it has the value of quality or wanted field. Multiple name
                        // fields in properties so must distinguish. All in random orders also.
                        if (property.has("name") && property.getString("name").contains("Quality")) {
                            itemQuality = extractLevelOrQuality(property);
                        } else if (property.has("name") && property.getString("name").contains("Evasion Rating")) {
                            evasionRating = extractLevelOrQuality(property);
                        } else if (property.has("name") && property.getString("name").contains("Energy Shield")) {
                            energyShield = extractLevelOrQuality(property);
                        } else if (property.has("name") && property.getString("name").contains("Armour")) {
                            armour = extractLevelOrQuality(property);
                        } else if (property.has("name") && property.getString("name").contains("Physical Damage")) {
                            physicalDamage = extractLevelOrQuality(property);
                        } else if (property.has("name") && property.getString("name").contains("Critical Strike Chance")) {
                            criticalChance = extractLevelOrQuality(property);
                        } else if (property.has("name") && property.getString("name").contains("Attacks per Second")) {
                            attackSpeed = extractLevelOrQuality(property);
                        } else if (property.has("name") && property.getString("name").contains("Elemental Damage")) {
                            elementalDamage = extractLevelOrQuality(property);
                        }
                    }

                    Log.i("JSON", "Name: " + itemName + " PD: " + physicalDamage + " ED: " + elementalDamage + " CC: " + criticalChance + " AS: " + attackSpeed);

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
                                if (socketedItemInfo.has("implicitMods")) {
                                    //The implicitMods
                                    socketedImplicitMods = socketedItemInfo.getJSONArray("implicitMods");
                                }

                                /*
                                 * EXPLICIT MODS: Add each mod if any at all to the layout.
                                 * */
                                JSONArray socketedExplicitMods = new JSONArray();
                                if (socketedItemInfo.has("explicitMods")) {
                                    socketedExplicitMods = socketedItemInfo.getJSONArray("explicitMods");
                                }

                                /**
                                 *  ENCHANTMENTS: Check all for enchants
                                 * */
                                JSONArray socketedEnchantMods = new JSONArray();
                                if (socketedItemInfo.has("enchantMods")) {
                                    socketedEnchantMods = socketedItemInfo.getJSONArray("enchantMods");
                                    //Log.i("charInfo", enchantMods.toString());
                                }

                                /**
                                 *  Crafted Mods: Check all for crafted mods
                                 * */
                                JSONArray socketedCraftMods = new JSONArray();
                                if (socketedItemInfo.has("craftedMods")) {
                                    socketedCraftMods = socketedItemInfo.getJSONArray("craftedMods");
                                    //Log.i("charInfo", craftMods.toString());
                                }

                                // the different rarities
                                // frametype 0 = common, 1 = magic, 2 = rare, 3 = unique
                                String socketedItemRarity = "Common";
                                if (socketedItemInfo.has("frameType")) {
                                    int frameType = socketedItemInfo.getInt("frameType");

                                    if (frameType == 0) {
                                        socketedItemRarity = "Common";
                                    } else if (frameType == 1) {
                                        socketedItemRarity = "Magic";
                                    } else if (frameType == 2) {
                                        socketedItemRarity = "Rare";
                                    } else if (frameType == 3) {
                                        socketedItemRarity = "Unique";
                                    }
                                }

                                // add to the socketed item array
                                Item socketedIndividualItem = new Item(socketedItemIcon, socketedItemName, socketedItemType, socketedImplicitMods, socketedExplicitMods, socketedItemInventoryId, socketedEnchantMods, socketedCraftMods, gems, socketedItemRarity);
                                itemArray.add(socketedIndividualItem);

                                // Determine is the socketed item is a gem
                            } else if (socketedItemInfo.has("support")) {
                                socketedItemIcon = socketedItemInfo.getString("icon");
                                socketedItemType = socketedItemInfo.getString("typeLine");
                                String gemLevel = "";
                                String gemQuality = "0";
                                // Which socket the gem is socketed in
                                String socketNumber = socketedItemInfo.getString("socket");
                                String socketGroupNumber = "";

                                // Peeling the properties field layers of JSON
                                JSONArray gemProperties = socketedItemInfo.getJSONArray("properties");

                                /**
                                 * Loop through the spaghetti JSON to find the name and quality values.
                                 * Then proceed to due way too much stuff with the strings to get our
                                 * values for our gem.
                                 * */
                                for (int z = 0; z < gemProperties.length(); z++) {
                                    JSONObject gemLQProperties = gemProperties.getJSONObject(z);
                                    //Store the name field's value. We need to find the one with level and one with quality
                                    String nameFieldVal = gemLQProperties.getString("name");

                                    // check for the existence of the name field to prevent crashes
                                    // then check that its for the Level
                                    if (gemLQProperties.has("name") && nameFieldVal.equals("Level")) {
                                        gemLevel = extractLevelOrQuality(gemLQProperties);
                                        //Log.i("jsonTest", "Gem Level: " + gemLevel);
                                        /**
                                         * Looking for the quality field. Then doing the breakdown we did
                                         * for the name
                                         * */
                                    } else if (gemLQProperties.has("name") && nameFieldVal.equals("Quality")) {
                                        gemQuality = extractLevelOrQuality(gemLQProperties);
                                        //Log.i("jsonTest", "Gem Quality: " + gemQuality);
                                    }
                                }

                                //Gem links and sockets
                                JSONArray sockets = itemInfo.getJSONArray("sockets");
                                // The current gem's socket number
                                int gemSocketNum = Integer.parseInt(socketedItemInfo.getString("socket"));

                                // Using the current gem's socket number find the group of the item socket
                                JSONObject socketGroup = sockets.getJSONObject(gemSocketNum);
                                socketGroupNumber = socketGroup.getString("group");

                                // Convert group number to integer so we can utilize it in our gem class.
                                int groupNumber = Integer.parseInt(socketGroupNumber);
                                //Log.i("jsonTest", "Item: " + itemType + "Gem: " + socketedItemType + " Group: " + socketGroupNumber);

                                // Create our gem and add to the gem array.
                                Gem individualGem = new Gem(socketedItemType, socketedItemIcon, gemQuality, gemLevel, groupNumber, inventoryId);
                                gems.add(individualGem);
                                gemList.add(individualGem);

                                for (int z = 0; z < gems.size(); z++) {
                                    //Log.i("gemsArray", "Gem: " + gems.get(z).typeLine + " " + gems.get(z).getGroupNum());
                                }

                            }
                            //Log.i("socketItem", socketedItemInfo.toString());
                        }

                    }
                }

                // Add the item to our itemArray so we can later sort the order of them by values
                Item individualItem = new Item(itemIcon, itemName, itemType, implicitMods, explicitMods, inventoryId, enchantMods, craftMods, gems, itemRarity);

                // Set the optional fields. In our Equipcustomlistadapter we will check if they are populated or not
                individualItem.setItemQuality(itemQuality);
                individualItem.setEvasionRating(evasionRating);
                individualItem.setEnergyShield(energyShield);
                individualItem.setArmour(armour);
                individualItem.setPhysicalDamage(physicalDamage);
                individualItem.setCriticalChance(criticalChance);
                individualItem.setAttackSpeed(attackSpeed);
                individualItem.setElementalDamage(elementalDamage);

                itemArray.add(individualItem);


            }

        } catch (JSONException e) {
            Log.d("charInfo", "Error: " + e);

        }
        /**
         * If we haven't run this method the first time then continue onto getting our passive jewel
         * information. If second time then sort our collection of items.
         * */
        if (characterGearDone == false) {
            characterGearDone = true;
            requestPassives();
        } else {
            itemArraySort();
        }


    }

    /**
     * Peel the onion layers of JSON for the level and quality fields. Returns the level or quality.
     */
    public String extractLevelOrQuality(JSONObject gemLQProperties) {
        try {
            //Grabbing the stored level and quality of the gemss
            JSONArray gemLQ = gemLQProperties.getJSONArray("values");
            // Grab the array inside.
            String gemLevelArrayString = gemLQ.get(0).toString();
            // Info stuck inside another array we can't access so remove brackets
            String newString = gemLevelArrayString.substring(1, gemLevelArrayString.length() - 1);
            // Break the field into two fields split at the comma. First
            // entry into our List will be the level
            List<String> brokenDownString = Arrays.asList(newString.split(","));
            // remove quotations
            String result = brokenDownString.get(0).replace("\"", "");
            return result;
        } catch (JSONException e) {
            Log.i("json", "Error, ExtractLevelOrQuality: " + e);
        }

        return null;

    }

    // Requests the passive tree information for grabbing socketed in skill tree jewels
    // then call our characterInfo parsing method.
    public void requestPassives() {

        charInfoUrl = "https://www.pathofexile.com/character-window/get-passive-skills?character=" + charName + "&accountName=" + acctName;
        Log.i("charInfo", charInfoUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, charInfoUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        parseCharacterInfo(response);

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

        //sortedList = new ArrayList<Item>();

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

            if (item.getInventoryId().equals("PassiveJewels")) {
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
        // fill our copy with the results.
        sortedListCopy.addAll(sortedList);

        // update the listview
        eqclAdapter.notifyDataSetChanged();
        Log.i("adapter", Integer.toString(eqclAdapter.getCount()));

        //populateGems();
    }

}


