package tgriffith.itas.fppoe;

import android.app.Activity;
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
                    iTest.setPadding(10, 0, 15, 0);
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
                    iTest.setPadding(15, 0, 15, 0);
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
                    iTest.setPadding(15, 0, 15, 0);
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
