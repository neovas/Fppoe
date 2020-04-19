package tgriffith.itas.fppoe;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.text.Html;
import android.util.Log;
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

public class GemCustomListAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;

    //to store the position on the ladder
    private final ArrayList<Gem> gemArrayList;


    public GemCustomListAdapter(Activity context, ArrayList<Gem> gemArrayList) {
        // always provide the data in this super statement otherwise you'll waste 40min.
        super(context, R.layout.gemrow, gemArrayList);

        this.context = context;
        this.gemArrayList = gemArrayList;
        Log.i("adapter", "Constructor for adapter");
        //Log.i("adapter", itemArray.get(0).getName());
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.gemrow, null, true);

        ImageView gemImageView = rowView.findViewById(R.id.gemImageView);
        TextView gemTextView = rowView.findViewById(R.id.gemNameView);
        LinearLayout gemWrapperLL = rowView.findViewById(R.id.gemrowWrapper);
        TextView gemEquipSlot = rowView.findViewById(R.id.equipslotview);
        //ImageView gemLink = rowView.findViewById(R.id.gemLink);

        // Get the individual item we will get gems from.
        Gem gem = gemArrayList.get(position);
        int paddingTop = 0;
        int paddingBot = 0;

        // First entry or first new item give spacing to the top. Show equipment slot.
        if (position == 0 || gemArrayList.get(position - 1).getEquipmentType() != gem.getEquipmentType()) {
            paddingTop = 15;
            gemEquipSlot.setText(gem.getEquipmentType());
            //gemLink.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.linkstart));
        } else {
            // Not the first entry, remove the item equipment name from view
            gemEquipSlot.setVisibility(View.GONE);
        }

        //next gem's group
        // make sure we aren't getting a null reference
        if (position + 1 < gemArrayList.size()) {

            Gem nextGem = gemArrayList.get(position + 1);
            // if the next gem's group matches ours and it was the same equipment type then marked as linked
            if (((gem.getGroupNum() == nextGem.getGroupNum())) && (gem.getEquipmentType() == nextGem.getEquipmentType())) {
                //gemImageView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemLink));
                gemImageView.setBackgroundResource(R.drawable.linkborder);
            } else {
                paddingBot = 15;
            }
        }

        // previous gem. Ensure we aren't getting a null reference
        if (position - 1 >= 0) {

            Gem prevGem = gemArrayList.get(position - 1);
            // If the previous gem's group matches ours and it was the same equipment type then mark as linked
            if ((gem.getGroupNum() == prevGem.getGroupNum()) && (gem.getEquipmentType() == prevGem.getEquipmentType())) {
                //gemImageView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemLink));
                gemImageView.setBackgroundResource(R.drawable.linkborder);
            }

        }

        // set the padding for the row
        gemWrapperLL.setPadding(10, paddingTop, 10, paddingBot);

        // image icon url
        String itemIcon = gemArrayList.get(position).getIcon();
        // the json has backslashes that break url, this removes them.
        itemIcon = itemIcon.replace("\\", "");
        // Loads image into imageview by url
        Picasso.with(context).load(itemIcon).into(gemImageView);

        // Remove extra text sometimes attached to max level gems. Do the same with quality.
        String gemLevel = gem.getLevel();
        gemLevel = gemLevel.replaceAll("[^\\d.]", "");

        String gemQuality = gem.getQuality();
        gemQuality = gemQuality.replaceAll("[^\\d.]", "");

        gemTextView.setText(gem.getTypeLine() + " " + gemLevel + "/" + gemQuality);


        return rowView;
    }
}
