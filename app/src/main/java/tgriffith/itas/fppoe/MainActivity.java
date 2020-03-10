package tgriffith.itas.fppoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

    private String ladderUrl = "https://api.pathofexile.com/ladders/Metamorph?limit=50";
    private String leagueUrl = "https://api.pathofexile.com/leagues?type=main";

    /**
     * TODO: Add league info from populate spinner into the spinner.
     * TODO: Search ladder depending on league selected. Select size of leaderboard to show.
     *
     * */
    // store the leagues for spinner
    //List<String> leagueSpinner = new ArrayList<String>();

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

        // tablelayout for ladder
        tl = (TableLayout) findViewById(R.id.TLWrapper);

        // on click of request button request from url
        btnRequest = (Button) findViewById(R.id.requestButton);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        // clear the table layout
        btnClear = (Button) findViewById(R.id.clearButton);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTl();
            }
        });

        // populate spinner for leagues
        populateSpinner();
    }

    public void request() {

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
                rankTv.setPadding(5,5,5,5);

                // Character name textview
                TextView nameTv = new TextView(this);
                nameTv.setText(nameVal);
                nameTv.setPadding(5,5,5,5);

                // Character level textview
                TextView levelTv = new TextView(this);
                levelTv.setText(levelVal);
                levelTv.setPadding(5,5,5,5);

                // Character level textview
                TextView classTv = new TextView(this);
                classTv.setText(classVal);
                classTv.setPadding(5,5,5,5);

                TextView accNameTv = new TextView(this);
                accNameTv.setText(accNameVal);
                accNameTv.setPadding(5,5,5,5);





                // add textviews to layout
                row.addView(rankTv);
                row.addView(nameTv);
                row.addView(levelTv);
                row.addView(classTv);
                row.addView(accNameTv);

                // Add the row
                tl.addView(row, i);
            }


            //Log.i("ladder", "Rank is: " + rank);
        } catch (JSONException e) {
            Log.d("ladder", "Error: " + e);
        }
    }

    /**
     *  Use api to get list of active leagues, then add to spinner as options
     * */
    public void populateSpinner(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, leagueUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.i("ladder", "Request" + response);
                        try {
                            //grab one league's information
                            for (int i =0; i < response.length(); i++) {
                                JSONObject leaguesJ = response.getJSONObject(i);
                                String leagueId = leaguesJ.getString("id");

                                Log.i("ladder", "League Test: " + leagueId);
                            }

                        } catch (JSONException e){

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("ladder", "error :" + error.toString());
                    }
                });

        queue.add(jsonArrayRequest);
    }
}
