package tgriffith.itas.fppoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // stores the league selected from the spinner
    private String selectedLeague = "Standard";

    private String ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?limit=200";

    // Buttons for requesting results, adding textViews, and clearing LL.
    private Button btnRequest;
    private Button btnClear;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    // The ll which wraps ladder contents
    public TableLayout tl;

    // counter for testing purposes
    private int counter = 0;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        //request();

        // tablelayout for ladder
        tl = findViewById(R.id.TLWrapper);

        // Spinner element
        Spinner spinner = findViewById(R.id.leagueSpinner);

        String[] leagueChoices = new String[]{
                "Standard", "Hardcore", "SSF Standard", "SSF Hardcore",
                "Delirium", "Delirium Hardcore", "SSF Delirium", "SSF Delirium HC"
        };
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, leagueChoices);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String league = parent.getItemAtPosition(position).toString();
                selectedLeague = league;
                ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?limit=200";
                clearTl();
                request();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });


    }


    public void request() {
        if (tl.getChildCount() < 1) {
            clearTl();
        }
        Log.i("ladder", ladderUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, ladderUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.i("ladder", "Request" + response);
                        tablePopulate(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("ladder", "error :" + error.toString());
                    }
                });

                queue.add(jsonObjectRequest);
    }

    /**
     * Clear all rows from the ladder table.
     * */
    public void clearTl(){
        tl.removeAllViews();
        counter = 0;
    }

    /**
     * Takes the ladder JSON Object, parses it and dynamically creates tablerows.
     * */
    public void tablePopulate(JSONObject ladder){

        try {
            //Access the Entries array of the ladder request
            JSONArray entriesArray = ladder.getJSONArray("entries");

            for (int i = 0; i < entriesArray.length(); i++){
                //Grab an entry of character + account from entries
                JSONObject charAccData = entriesArray.getJSONObject(i);

                //Grab rank info
                String rankVal = charAccData.getString("rank");
                //Online status
                String onlineVal = charAccData.getString("online");
                // Death status
                String deadVal = charAccData.getString("dead");

                // Contains only the character portion of data.
                JSONObject characters = charAccData.getJSONObject("character");

                //Grab Name of Character on ladder
                String nameVal = characters.getString("name");
                // Grab level of character
                String levelVal = characters.getString("level");
                // Grab class
                String classVal = characters.getString("class");
                // exp
                String expVal = characters.getString("experience");

                // The account information paired with a character
                JSONObject account = charAccData.getJSONObject("account");

                // Account name
                String accNameVal = account.getString("name");

                // Create a row for the tablelayout
                TableRow row = new TableRow(this);
                // Set the layout parameters and apply to the row
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                // The textview storing rank on ladder
                TextView rankTv = new TextView(this);
                rankTv.setText(rankVal);
                rankTv.setTextSize(16);
                rankTv.setPadding(5,5,5,5);
                if (onlineVal == "true") {
                    rankTv.setBackgroundResource(android.R.color.holo_green_light);
                } else if (deadVal == "true") {
                    rankTv.setBackgroundResource(android.R.color.holo_red_light);
                }

                // Character name textview
                TextView nameTv = new TextView(this);
                nameTv.setText(nameVal);
                nameTv.setTextSize(16);
                nameTv.setPadding(5,5,5,5);

                // Character level textview
                TextView levelTv = new TextView(this);
                levelTv.setText(levelVal);
                levelTv.setTextSize(16);
                levelTv.setPadding(5,5,5,5);

                // Character level textview
                TextView classTv = new TextView(this);
                classTv.setText(classVal);
                classTv.setTextSize(16);
                classTv.setPadding(5,5,5,5);

                /*TextView accNameTv = new TextView(this);
                accNameTv.setText(accNameVal);
                accNameTv.setTextSize(15);
                accNameTv.setPadding(5,5,5,5);*/





                // add textviews to layout
                row.addView(rankTv);
                row.addView(nameTv);
                row.addView(levelTv);
                row.addView(classTv);
                //row.addView(accNameTv);

                // Add the row
                tl.addView(row, i);
            }


            //Log.i("ladder", "Rank is: " + rank);
        } catch (JSONException e) {
            Log.d("ladder", "Error: " + e);
        }
    }

}
