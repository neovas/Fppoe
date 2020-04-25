package tgriffith.itas.fppoe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // stores the league selected from the spinner
    private String selectedLeague = "Standard";

    // Offset for ladder searches.
    int ladderOffset = 0;

    // the lowest spot visible on ladder. 15k at moment of programming.
    int ladderMaxSize = 15000;

    // Storing our toast message so we can clear it earlier if needed.
    Toast toastMessage;

    private String ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?limit=200&offset=" + ladderOffset;

    // Buttons for requesting results, adding textViews, and clearing LL.
    private Button btnRequest;
    private Button btnClear;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    // Our beautiful listview
    ListView listView;
    SearchView searchView;
    // swipe to refresh
    // https://www.bignerdranch.com/blog/implementing-swipe-to-refresh-an-android-material-design-ui-pattern/
    SwipeRefreshLayout mSwipeRefreshLayout;

    // The searched accountName. This allows us to pass the accountName variable into
    // tablePopulate when doing a search. This is due to lack of info in the account search json.
    String searchedAccountName;

    // Flag representing if the user is searching for an account or doing regular ladder search
    Boolean isAccountSearch = false;

    // check for successful characterInformation api request
    Boolean gotCharInfo = false;


    // Stores the name, info and rank values which
    // are stored in the listView rows.
    ArrayList<String> nameArray = new ArrayList<String>();
    ArrayList<String> infoArray = new ArrayList<String>();
    ArrayList<String> rankArray = new ArrayList<String>();
    ArrayList<String> onlineStatusArray = new ArrayList<>();
    ArrayList<String> deathStatusArray = new ArrayList<>();

    //Used for passing info on into the characterInfo activity
    ArrayList<String> accountNameArray = new ArrayList<>();
    ArrayList<String> characterLevelArray = new ArrayList<>();
    ArrayList<String> characterClassArray = new ArrayList<>();

    // The customAdapter for our listview
    CustomListAdapter clAdapter;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        // Find the listView layout
        listView = findViewById(R.id.listViewID);
        // find the searchView
        searchView = findViewById(R.id.accountSearchID);
        // find swipe to refresh
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);

        // Create our custom adapter and provide it the arrays which will hold our ladder info
        clAdapter = new CustomListAdapter(this, nameArray, infoArray, rankArray, onlineStatusArray, deathStatusArray);
        //bind the two
        listView.setAdapter(clAdapter);


        /**
         * When swiping to refresh clear variables and refresh the ladder.
         * */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                accountNameArray.clear();
                characterClassArray.clear();
                characterLevelArray.clear();
                clAdapter.notifyDataSetChanged();

                // Check whether we are refreshing a standard league result or an account search
                if (isAccountSearch == false) {
                    // assign values to ladderUrl again so it updates
                    ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?limit=200&offset=" + ladderOffset;

                } else {
                    ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?accountName=" + searchedAccountName + "&limit=200";

                }
                request();
                // clears the refreshing icon after refreshing.
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // Spinner element
        Spinner spinner = findViewById(R.id.leagueSpinner);

        // Currently running leagues
        String[] leagueChoices = new String[]{
                "Delirium", "Hardcore Delirium", "SSF Delirium", "SSF Delirium HC",
                "Standard", "Hardcore", "SSF Standard", "SSF Hardcore"
        };
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.ladder_spinner_entry, leagueChoices);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.ladder_spinner_entry);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //((TextView) parent.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                //((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dkPText));
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
                accountNameArray.clear();
                characterClassArray.clear();
                characterLevelArray.clear();
                clAdapter.notifyDataSetChanged();

                //grab the selected item from the spinner
                String league = parent.getItemAtPosition(position).toString();

                // urlencode the league so spaces do not break the url
                try {
                    selectedLeague = URLEncoder.encode(league, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("log", "UnsupportedEncodingException");
                }

                // assign values to ladderUrl again so it updates
                ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?limit=200&offset=" + ladderOffset;
                request();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });

        // https://stackoverflow.com/questions/31749210/searchview-seticonifiedfalse-automatically-calls-the-focus-for-the-searchview
        // Fixes issue where searchView is focused on automatically.
        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();

        // searchVIew listener.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // When text is submitted in searchView query api for accountName characters.
            @Override
            public boolean onQueryTextSubmit(String query) {
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
                accountNameArray.clear();
                characterClassArray.clear();
                characterLevelArray.clear();
                clAdapter.notifyDataSetChanged();

                // store account name in our global variable for use in tablePopulate
                searchedAccountName = query;

                // set accountSearch flag to true
                isAccountSearch = true;

                ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?accountName=" + searchedAccountName + "&limit=200";
                Log.i("ladder", "Searching for: " + query);
                request();

                return false;
            }

            /**
             * Everytime text input changes this runs. If the query is empty then default to normal
             * search of the league.
             */
            @Override
            public boolean onQueryTextChange(String newText) {

                if (TextUtils.isEmpty(newText)) {
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
                    accountNameArray.clear();
                    characterClassArray.clear();
                    characterLevelArray.clear();
                    clAdapter.notifyDataSetChanged();

                    // not searching for an account name so reset account search flag to false
                    isAccountSearch = false;

                    // assign values to ladderUrl again so it updates
                    ladderUrl = "https://api.pathofexile.com/ladders/" + selectedLeague + "?limit=200&offset=" + ladderOffset;
                    request();
                }
                return false;
            }
        });



        // On click of item start CharacterInfo activity.
        // Pass the accountName and characterName so we can call api to get detailed info on the
        // character. Gems, gear, etc.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // if searchView has been focused clear it
                searchView.clearFocus();
                requestCharInfo(position);

            }
        });

    }

    /**
     * Requests the equipped gear of the character provided a character name and account name.
     */
    public void requestCharInfo(int position) {
        int row = position;
        final String listRowName = nameArray.get(position);
        final String listRowAccount = accountNameArray.get(position);
        final String listRowLevel = characterLevelArray.get(position);
        final String listRowClass = characterClassArray.get(position);

        String charInfoUrl = "https://www.pathofexile.com/character-window/get-items?character=" + listRowName + "&accountName=" + listRowAccount;
        Log.i("charInfo", charInfoUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, charInfoUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // create our intent and pass the char name and acct names to our activity.
                        Intent characterIntent = new Intent(getApplicationContext(), CharacterInfo.class);
                        characterIntent.putExtra("characterName", listRowName);
                        characterIntent.putExtra("accountName", listRowAccount);
                        characterIntent.putExtra("characterLevel", listRowLevel);
                        characterIntent.putExtra("characterClass", listRowClass);
                        characterIntent.putExtra("characterInformation", response.toString());

                        try {
                            JSONArray itemEntry = response.getJSONArray("items");

                            if (itemEntry.length() == 0) {
                                // if user is tapping new entries clear previous toast that may be there.
                                if (toastMessage != null) {
                                    toastMessage.cancel();
                                }
                                Toast.makeText(getApplicationContext(), "No equipped items to show on " + listRowName, Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(characterIntent);
                            }

                        } catch (JSONException e) {
                            Log.i("JSONE", e.toString());
                        }


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
                            // if user is tapping new entries clear previous toast that may be there.
                            if (toastMessage != null) {
                                toastMessage.cancel();
                            }
                            Toast.makeText(getApplicationContext(), "Game servers cannot be reached currently. Try again later.", Toast.LENGTH_SHORT).show();
                        }
                        /**
                         * 403 response generally is due to the account being marked as private.
                         * Close the activity
                         * */
                        if (error.networkResponse.statusCode == 403) {
                            // if user is tapping new entries clear previous toast that may be there.
                            if (toastMessage != null) {
                                toastMessage.cancel();
                            }
                            Toast.makeText(getApplicationContext(), "Cannot access account. Marked as private.", Toast.LENGTH_SHORT).show();
                        }
                        // Resource not found
                        if (error.networkResponse.statusCode == 404) {
                            // if user is tapping new entries clear previous toast that may be there.
                            if (toastMessage != null) {
                                toastMessage.cancel();
                            }
                            Toast.makeText(getApplicationContext(), "404. Resource not found.", Toast.LENGTH_SHORT).show();
                        }
                        gotCharInfo = false;
                    }
                });

        queue.add(jsonObjectRequest);
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
                        Log.i("ladder", "Request" + response);
                        clAdapter.clear();
                        nameArray.clear();
                        infoArray.clear();
                        rankArray.clear();
                        deathStatusArray.clear();
                        onlineStatusArray.clear();
                        accountNameArray.clear();
                        characterClassArray.clear();
                        characterLevelArray.clear();
                        tablePopulate(response);
                        clAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ladder", "error :" + error.toString());
                        /**
                         * Check for error 503 network response. This is the error thrown when
                         * game/website is down. Toast the user telling them servers cannot be reached.
                         * */
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 503) {
                            Toast.makeText(getApplicationContext(), "Game servers cannot be reached currently. Try again later.", Toast.LENGTH_LONG).show();
                        } else if (networkResponse != null && networkResponse.statusCode == 429) {
                            Toast.makeText(getApplicationContext(), "Too many searches in a short time. Please try again in minute.", Toast.LENGTH_LONG).show();
                        }
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
                Log.i("ladder", "CharAccData: " + charAccData);
                //Grab rank info
                String rankVal = charAccData.getString("rank");
                Log.i("ladder", "RankVal: " + rankVal);

                //Online status
                String onlineVal = charAccData.getString("online");
                // Death status
                String deadVal = charAccData.getString("dead");

                // Contains only the character portion of data.
                JSONObject characters = charAccData.getJSONObject("character");

                //Grab Name of Character on ladder
                String nameVal = characters.getString("name");
                Log.i("ladder", "Name: " + nameVal);

                // Grab level of character
                String levelVal = characters.getString("level");
                // Grab class
                String classVal = characters.getString("class");

                // exp
                String expVal = characters.getString("experience");

                // The account information paired with a character
                JSONObject account;
                String accNameVal;

                /**
                 *  Checking that our query contains an account object.
                 *  This is used primarily for accountSearch
                 * */
                if (charAccData.has("account")) {
                    account = charAccData.getJSONObject("account");
                    Log.i("ladder", "Account value: " + account);
                    // Account name
                    accNameVal = account.getString("name");
                } else {
                    // Couldn't find account object so using the query value from the searchView
                    accNameVal = searchedAccountName;
                }

                // Add to listView Row's information line the level, class and account name.
                infoArray.add(levelVal + "  " + classVal + " - " + accNameVal);
                rankArray.add(rankVal);
                nameArray.add(nameVal);
                deathStatusArray.add(deadVal);
                onlineStatusArray.add(onlineVal);
                // This array will store account names for use in creating a new activity.
                accountNameArray.add(accNameVal);
                characterClassArray.add(classVal);
                characterLevelArray.add(levelVal);

                Log.i("ladder", accNameVal + " " + nameVal + " " + rankVal);

                //clAdapter.notifyDataSetChanged();

            }
            //Log.i("ladder", "Rank is: " + rank);
        } catch (JSONException e) {
            Log.d("ladder", "Error: " + e);
        }
    }

}
