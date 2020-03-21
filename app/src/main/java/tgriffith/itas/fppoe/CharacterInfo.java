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

/**
 * This activity starts when a ladder entry is tapped. It gets the character and account name from
 * the mainactivity. It then queries the api for character specific info such as gear and gems.
 */
public class CharacterInfo extends AppCompatActivity {

    // The charname and account selected from the ladder
    String charName;
    String acctName;

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

                        populateCharacterInfo(response);

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

    public void populateCharacterInfo(JSONObject response) {
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

                /**
                 * Create the layouts and views for an item entry
                 * */
                // The LL which contains the icon and item info
                LinearLayout parent = new LinearLayout(getApplicationContext());
                parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.HORIZONTAL);

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
                TextView iIMods = new TextView(getApplicationContext());
                TextView iEMods = new TextView(getApplicationContext());


                // image icon url
                itemIcon = itemInfo.getString("icon");
                // the json has backslashes that break url, this removes them.
                itemIcon = itemIcon.replace("\\", "");
                //Log.i("charInfo", itemIcon);

                // Display the item name and item type
                iName.setText(itemName + " " + itemType);
                // Add the name to the layout
                childLl.addView(iName);

                // Set dimensions of our image in dp
                int widthHeightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                itemIconIv.getLayoutParams().height = widthHeightDp;
                itemIconIv.getLayoutParams().width = widthHeightDp;
                // Loads image into imageview by url
                Picasso.with(getApplicationContext()).load(itemIcon).into(itemIconIv);

                /*
                 * IMPLICIT MODS: Add each mod if any to the layout
                 * */
                if (itemInfo.has("implicitMods")) {
                    //The implicitMods
                    JSONArray implicitMods = itemInfo.getJSONArray("implicitMods");
                    //Log.i("charInfo", implicitMods.toString());


                    //Loop through the implicitMods
                    for (int j = 0; j < implicitMods.length(); j++) {
                        // Set the text on first loop. Following loops append to it.
                        if (j == 0) {
                            iIMods.setText(implicitMods.get(j).toString());
                        } else {
                            iIMods.append('\n' + implicitMods.get(j).toString());
                        }
                    }
                    // add the implicit mods to the layout
                    childLl.addView(iIMods);
                }

                /*
                 * EXPLICIT MODS: Add each mod if any at all to the layout.
                 * */
                if (itemInfo.has("explicitMods")) {
                    JSONArray explicitMods = itemInfo.getJSONArray("explicitMods");
                    Log.i("charInfo", explicitMods.toString());


                    //Loop through the explicitMods
                    for (int i = 0; i < explicitMods.length(); i++) {
                        // Set the text on first loop. Following loops append to it.
                        if (i == 0) {
                            iEMods.setText(explicitMods.get(i).toString() + '\n');
                        } else {
                            iEMods.append(explicitMods.get(i).toString() + '\n');
                        }

                    }
                    // add the explicit mods to the layout
                    childLl.addView(iEMods);
                }

                // SOCKETED ITEMS PARSING
                //JSONArray socketedItems = itemInfo.getJSONArray("socketedItems");
                // The individual gem
                //JSONObject socketedGem = socketedItems.getJSONObject(0);
                //String gemName = socketedGem.getString("typeLine");

                // set the textView
                //TextView itemSocket = findViewById(R.id.itemSocket);
                //itemSocket.setText(gemName);

                // set the image
                //ImageView itemSocketIv = findViewById(R.id.socketImage);
                // image icon url
                //String socketIcon = socketedGem.getString("icon");
                // the json has backslashes that break url, this removes them.
                //socketIcon = socketIcon.replace("\\", "");
                //Log.i("charInfo", socketIcon);
                // Loads image into imageview by url
                //Picasso.with(getApplicationContext()).load(socketIcon).into(itemSocketIv);

                //Log.i("charInfo", socketedGem.toString());

                //Log.i("charInfo", "Item Name: " + itemName);
            }
        } catch (JSONException e) {
            Log.d("charInfo", "Error: " + e);

        }

    }

}
