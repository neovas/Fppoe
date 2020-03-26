package tgriffith.itas.fppoe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * This class symbolizes an item array entry from the get-character info api.
 * Stores every field from the item array which which be used in the CharacterInfo.java activity.
 *
 * */
public class Item {
    public String imageUrl;
    public String name;
    public String typeLine;
    public JSONArray implicitMods;
    public JSONArray explicitMods;
    public String inventoryId;

    public Item(String imageUrl, String name, String typeLine, JSONArray implicitMods, JSONArray explicitMods, String inventoryId) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.typeLine = typeLine;
        this.implicitMods = implicitMods;
        this.explicitMods = explicitMods;
        this.inventoryId = inventoryId;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getTypeLine() {
        return typeLine;
    }

    public JSONArray getImplicitMods() {
        return implicitMods;
    }

    public JSONArray getExplicitMods() {
        return explicitMods;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeLine(String typeLine) {
        this.typeLine = typeLine;
    }

    public void setImplicitMods(JSONArray implicitMods) {
        this.implicitMods = implicitMods;
    }

    public void setExplicitMods(JSONArray explicitMods) {
        this.explicitMods = explicitMods;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }
}
