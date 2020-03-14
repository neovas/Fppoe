package tgriffith.itas.fppoe;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * TODO:
 * 2. Use listView functions to detect if at bottom of screen, load more. Or
 * find alternative way to load more results. Left and right swipes?
 * 3. Search for specific character.
 */


public class MainActivity extends AppCompatActivity {
    // stores the league selected from the spinner
    private String selectedLeague = "Standard";

    private String ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?limit=200";

    // Buttons for requesting results, adding textViews, and clearing LL.
    private Button btnRequest;
    private Button btnClear;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    ListView listView;

    // Stores the name, info and rank values which
    // are stored in the listView rows.
    ArrayList<String> nameArray = new ArrayList<String>();
    ArrayList<String> infoArray = new ArrayList<String>();
    ArrayList<String> rankArray = new ArrayList<String>();
    ArrayList<String> onlineStatusArray = new ArrayList<>();
    ArrayList<String> deathStatusArray = new ArrayList<>();

    // The customAdapter for our listview
    CustomListAdapter clAdapter;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        // Find the listView layout
        listView = findViewById(R.id.listViewID);
        // Create our custom adapter and provide it the arrays which will hold our ladder info
        clAdapter = new CustomListAdapter(this, nameArray, infoArray, rankArray, onlineStatusArray, deathStatusArray);
        //bind the two
        listView.setAdapter(clAdapter);

        // Spinner element
        Spinner spinner = findViewById(R.id.leagueSpinner);

        // Currently running leagues
        String[] leagueChoices = new String[]{
                "Standard", "Hardcore", "SSF Standard", "SSF Hardcore",
                "Delirium", "Hardcore Delirium", "SSF Delirium", "SSF Delirium HC"
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

                /**
                 * Clear the listView and then reset the arrays so
                 * on league change only those entries are stored.
                 * */
                clAdapter.clear();
                nameArray.clear();
                infoArray.clear();
                rankArray.clear();
                deathStatusArray.clear();
                onlineStatusArray.clear();

                //grab the selected item from the spinner
                String league = parent.getItemAtPosition(position).toString();

                // urlencode the league so spaces do not break the url
                try {
                    selectedLeague = URLEncoder.encode(league, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("log", "UnsupportedEncodingException");
                }

                // assign values to ladderUrl again so it updates
                ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?limit=200";
                request();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });
    }

    /**
     * Requests ladder information from api.pathofexile.com
     * Then calls the tablePopulate function to display the information.
     *
     * */
    public void request() {

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

                // Add to listView Row's information line the level, class and account name.
                infoArray.add(levelVal + "  " + classVal + " - " + accNameVal);
                rankArray.add(rankVal);
                nameArray.add(nameVal);
                deathStatusArray.add(deadVal);
                onlineStatusArray.add(onlineVal);
                Log.i("ladder", deadVal);

                clAdapter.notifyDataSetChanged();

            }
            //Log.i("ladder", "Rank is: " + rank);
        } catch (JSONException e) {
            Log.d("ladder", "Error: " + e);
        }
    }
}
