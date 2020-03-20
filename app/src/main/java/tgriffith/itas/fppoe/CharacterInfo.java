package tgriffith.itas.fppoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * This activity starts when a ladder entry is tapped. It gets the character and account name from
 * the mainactivity. It then queries the api for character specific info such as gear and gems.
 */
public class CharacterInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_info);
        Intent flagIntent = getIntent();

        // Grabbing the values from our mainactivity's selected entry on ladder
        String charName = flagIntent.getStringExtra("characterName");
        String acctName = flagIntent.getStringExtra("accountName");
        String charLevel = flagIntent.getStringExtra("characterLevel");
        String charClass = flagIntent.getStringExtra("characterClass");
        
        Log.i("charInfo", "Character Name: " + charName + " | Account Name: " + acctName + " |Level: " + charLevel + " |Class: " + charClass);

    }
}
