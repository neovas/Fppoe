package tgriffith.itas.fppoe;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * https://appsandbiscuits.com/listview-tutorial-android-12-ccef4ead27cc
 */
public class CustomListAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;

    //to store the position on the ladder
    private final ArrayList<String> rankArray;

    //to store character name
    private final ArrayList<String> nameArray;

    //to store class, level, account name, etc
    private final ArrayList<String> infoArray;

    // store online status
    private final ArrayList<String> onlineArray;

    // store death status
    private final ArrayList<String> deathArray;


    public CustomListAdapter(Activity context, ArrayList<String> nameArrayParam, ArrayList<String> infoArrayParam, ArrayList<String> rankArrayParam,
                             ArrayList<String> onlineArrayParam, ArrayList<String> deathArrayParam) {

        super(context, R.layout.listview_row, rankArrayParam);

        this.context = context;
        this.rankArray = rankArrayParam;
        this.nameArray = nameArrayParam;
        this.infoArray = infoArrayParam;
        this.onlineArray = onlineArrayParam;
        this.deathArray = deathArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_row, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = rowView.findViewById(R.id.nameTextViewID);
        TextView infoTextField = rowView.findViewById(R.id.infoTextViewID);
        TextView rankTextField = rowView.findViewById(R.id.rankTextViewID);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(nameArray.get(position));
        infoTextField.setText(infoArray.get(position));
        rankTextField.setText(rankArray.get(position));

        // Color code dead characters. Death status done second so you can see dead characters
        // of online players.
        if (onlineArray.get(position).equals("true")) {
            rankTextField.setBackgroundColor((Color.GREEN));
        }
        if (deathArray.get(position).equals("true")) {
            rankTextField.setBackgroundColor(Color.RED);
        }

        return rowView;

    }
}
