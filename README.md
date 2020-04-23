# Fppoe - Path of Exile Ladder Tool

[REPOSITORY](https://github.com/neovas/Fppoe)

Fppoe (placeholder name) is an Android app which shows the user the top 200 of the ladder (leaderboard). The user 
is able to switch which ladder they are viewing (e.g. Delirium, HC Delirium, Standard, Hardcore, etc). Each entry from
the leaderboard will show the Rank, character name, account name, class, level and the background behind the rank will
be green if the account is online or red if they are dead (in hardcore leagues only).

From here the user can tap on any of the players listed on the leaderboard in order to display all of their equipped gear and gems. 
If the user has no equipped gear, can't be found (rare but seems to be related to extremely old, inactive characters),
or has their profile set to private the user will be notified and unable to view their equipment. On the ladder view the
user can tap on the search button and search by account name to return any characters associated with that account in 
the currently selected league. 

NOTE: Due to api limitations with searching leagues only characters in the top 15000 of a ladder can be seen.

## Useful Resources 
These are some of the tutorials and resources I utilized for the assignment. For the most part the code which I needed
help with was features such as how to work with listviews and create CustomListAdapters. 

* [Path of Exile Official API Resources](https://www.pathofexile.com/developer/docs/api-resources)
* [Making HTTP Requests with Volley](https://developer.android.com/training/volley/request)
* [Programmatically adding Views to a LinearLayout](https://en.proft.me/2016/08/28/how-programmatically-add-views-linearlayout-androi/)
* [Getting JSONObjects from JSONArrays](https://stackoverflow.com/questions/7634518/getting-jsonobject-from-jsonarray)
* [Populating a Spinner Programmatically](https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically)
* [ListView Tutorial](https://www.vogella.com/tutorials/AndroidListView/article.html)
* [Pull to Refresh](https://stackoverflow.com/questions/22387820/how-to-create-a-pull-to-refresh-into-a-listview)
* [Checking if a JSON key exists](https://stackoverflow.com/questions/17487205/how-to-check-if-a-json-key-exists)
* [Drawing a border on one side of a view](https://stackoverflow.com/questions/9211208/how-to-draw-border-on-just-one-side-of-a-linear-layout)
* [Setting multiple colors in one TextView](https://riptutorial.com/android/example/19946/single-textview-with-two-different-colors)
* [Setting Navigation Bar and Status Bar Colors](https://stackoverflow.com/questions/22192291/how-to-change-the-status-bar-color-in-android)

