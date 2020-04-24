package tgriffith.itas.fppoe;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Paint;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class EquipCustomListAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;

    //to store the position on the ladder
    private final ArrayList<Item> itemArray;


    public EquipCustomListAdapter(Activity context, ArrayList<Item> itemArray) {
        // always provide the data in this super statement otherwise you'll waste 40min.
        super(context, R.layout.equipment_row, itemArray);

        this.context = context;
        this.itemArray = itemArray;
        Log.i("adapter", "Constructor for adapter");
        //Log.i("adapter", itemArray.get(0).getName());
    }

    public View getView(int position, View view, ViewGroup parent) {
        Log.i("adapter", "Adapater Name: " + itemArray.get(position).getName());
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.equipment_row, null, true);

        //this code gets references to objects in the listview_row.xml file
        LinearLayout itemInfoll = rowView.findViewById(R.id.eqLlInfo);
        ImageView eqImageView = rowView.findViewById(R.id.eqImageView);
        TextView eqItemName = rowView.findViewById(R.id.eqItemName);

        // An individual item
        Item item = itemArray.get(position);

        // image icon url
        String itemIcon = itemArray.get(position).imageUrl;
        // the json has backslashes that break url, this removes them.
        itemIcon = itemIcon.replace("\\", "");
        // Loads image into imageview by url
        Picasso.with(context).load(itemIcon).into(eqImageView);

        // add name and typeline of item
        eqItemName.setText(item.getName() + " " + item.getTypeLine());
        eqItemName.setPadding(10, 0, 15, 0);

        // set the color of the item name to be the same as the rarity colors in game
        if (item.getRarity() == "Common") {
            eqItemName.setTextColor(ContextCompat.getColor(context, R.color.rarityCommon));
        } else if (item.getRarity() == "Magic") {
            eqItemName.setTextColor(ContextCompat.getColor(context, R.color.rarityMagic));
        } else if (item.getRarity() == "Rare") {
            eqItemName.setTextColor(ContextCompat.getColor(context, R.color.rarityRare));
        } else if (item.getRarity() == "Unique") {
            eqItemName.setTextColor(ContextCompat.getColor(context, R.color.rarityUnique));
        }

        // Adding quality. Check it has a non default value.
        if (!item.getItemQuality().equals("")) {
            TextView iTest = new TextView(context);
            // Setting two different colors for one textview. Easiest found way was using html.
            // so we copied the hexcode from our colors.xml. They are itemBaseStats and itemTextBlue
            // we are using
            String field = "<font color='#7f7f7f'>Quality: </font>";
            String value = "<font color='#8787fe'>" + item.getItemQuality() + "</font>";

            iTest.setText(Html.fromHtml(field + value));
            iTest.setPadding(10, 0, 15, 0);
            itemInfoll.addView(iTest);
        }

        //Evasion Rating
        if (!item.getEvasionRating().equals("")) {
            TextView iTest = new TextView(context);
            // Setting two different colors for one textview. Easiest found way was using html.
            // so we copied the hexcode from our colors.xml. They are itemBaseStats and itemTextBlue
            // we are using
            String field = "<font color='#7f7f7f'>Evasion Rating: </font>";
            String value = "<font color='#8787fe'>" + item.getEvasionRating() + "</font>";

            iTest.setText(Html.fromHtml(field + value));
            iTest.setPadding(10, 0, 15, 0);
            itemInfoll.addView(iTest);
        }

        // Energy Shield
        if (!item.getEnergyShield().equals("")) {
            TextView iTest = new TextView(context);
            // Setting two different colors for one textview. Easiest found way was using html.
            // so we copied the hexcode from our colors.xml. They are itemBaseStats and itemTextBlue
            // we are using
            String field = "<font color='#7f7f7f'>Energy Shield: </font>";
            String value = "<font color='#8787fe'>" + item.getEnergyShield() + "</font>";

            iTest.setText(Html.fromHtml(field + value));
            iTest.setPadding(10, 0, 15, 0);
            itemInfoll.addView(iTest);
        }

        // Armour
        if (!item.getArmour().equals("")) {
            TextView iTest = new TextView(context);
            // Setting two different colors for one textview. Easiest found way was using html.
            // so we copied the hexcode from our colors.xml. They are itemBaseStats and itemTextBlue
            // we are using
            String field = "<font color='#7f7f7f'>Armour: </font>";
            String value = "<font color='#8787fe'>" + item.getArmour() + "</font>";

            iTest.setText(Html.fromHtml(field + value));
            iTest.setPadding(10, 0, 15, 0);
            itemInfoll.addView(iTest);
        }

        // Physical Damage
        if (!item.getPhysicalDamage().equals("")) {
            TextView iTest = new TextView(context);
            // Setting two different colors for one textview. Easiest found way was using html.
            // so we copied the hexcode from our colors.xml. They are itemBaseStats and itemTextBlue
            // we are using
            String field = "<font color='#7f7f7f'>Physical Damage: </font>";
            String value = "<font color='#8787fe'>" + item.getPhysicalDamage() + "</font>";

            iTest.setText(Html.fromHtml(field + value));
            iTest.setPadding(10, 0, 15, 0);
            itemInfoll.addView(iTest);
        }

        // Elemental Damage
        if (!item.getElementalDamage().equals("")) {
            TextView iTest = new TextView(context);
            // Setting two different colors for one textview. Easiest found way was using html.
            // so we copied the hexcode from our colors.xml. They are itemBaseStats and itemTextBlue
            // we are using
            String field = "<font color='#7f7f7f'>Elemental Damage: </font>";
            String value = "<font color='#8787fe'>" + item.getElementalDamage() + "</font>";

            iTest.setText(Html.fromHtml(field + value));
            iTest.setPadding(10, 0, 15, 0);
            itemInfoll.addView(iTest);
        }

        // Critical Strike Chance
        if (!item.getCriticalChance().equals("")) {
            TextView iTest = new TextView(context);
            // Setting two different colors for one textview. Easiest found way was using html.
            // so we copied the hexcode from our colors.xml. They are itemBaseStats and itemTextBlue
            // we are using
            String field = "<font color='#7f7f7f'>Critical Strike Chance: </font>";
            String value = "<font color='#8787fe'>" + item.getCriticalChance() + "</font>";

            iTest.setText(Html.fromHtml(field + value));
            iTest.setPadding(10, 0, 15, 0);
            itemInfoll.addView(iTest);
        }

        // Attack Speed
        if (!item.getAttackSpeed().equals("")) {
            TextView iTest = new TextView(context);
            // Setting two different colors for one textview. Easiest found way was using html.
            // so we copied the hexcode from our colors.xml. They are itemBaseStats and itemTextBlue
            // we are using
            String field = "<font color='#7f7f7f'>Attack Speed: </font>";
            String value = "<font color='#8787fe'>" + item.getAttackSpeed() + "</font>";

            iTest.setText(Html.fromHtml(field + value));
            iTest.setPadding(10, 0, 15, 0);
            itemInfoll.addView(iTest);
        }


        /**
         * Add enchant info
         * */
        JSONArray enchantMods = new JSONArray();
        if (item.getEnchantMods().length() > 0) {
            //The implicitMods
            enchantMods = item.getEnchantMods();
            //Log.i("charInfo", implicitMods.toString());

            //Loop through the implicitMods
            for (int j = 0; j < enchantMods.length(); j++) {
                TextView iTest = new TextView(context);
                try {
                    iTest.setText(enchantMods.get(j).toString());
                    iTest.setPadding(10, 10, 15, 0);
                    iTest.setTextColor(ContextCompat.getColor(context, R.color.enchantBlue));
                } catch (JSONException e) {
                    Log.d("json", "Error: " + e);
                }
                itemInfoll.addView(iTest);
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

                TextView iTest = new TextView(context);
                try {
                    iTest.setText(implicitMods.get(j).toString());
                    iTest.setPadding(10, 0, 15, 0);
                    iTest.setTextColor(ContextCompat.getColor(context, R.color.itemTextBlue));
                } catch (JSONException e) {
                    Log.d("json", "Error: " + e);
                }

                itemInfoll.addView(iTest);

                // If this is the last implicit add more padding to the bottom
                if (j == implicitMods.length() - 1) {
                    iTest.setPadding(10, 0, 15, 10);

                }

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

                TextView iTest = new TextView(context);
                try {
                    iTest.setText(explicitMods.get(j).toString());
                    iTest.setPadding(10, 0, 15, 0);
                    iTest.setTextColor(ContextCompat.getColor(context, R.color.itemTextBlue));
                } catch (JSONException e) {
                    Log.d("json", "Error: " + e);
                }
                itemInfoll.addView(iTest);

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
                TextView iTest = new TextView(context);
                try {
                    iTest.setText(craftMods.get(j).toString());
                    iTest.setPadding(10, 0, 15, 0);
                    iTest.setTextColor(ContextCompat.getColor(context, R.color.enchantBlue));
                } catch (JSONException e) {
                    Log.d("json", "Error: " + e);
                }
                itemInfoll.addView(iTest);
            }
        }


        return rowView;

    }
}
