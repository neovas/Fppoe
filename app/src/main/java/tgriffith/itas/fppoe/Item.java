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
    public JSONArray enchantMods;
    public JSONArray craftedMods;
    public ArrayList<Gem> gems;

    public Item(String imageUrl, String name, String typeLine, JSONArray implicitMods, JSONArray explicitMods, String inventoryId, JSONArray enchantMods, JSONArray craftedMods, ArrayList<Gem> gems) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.typeLine = typeLine;
        this.implicitMods = implicitMods;
        this.explicitMods = explicitMods;
        this.inventoryId = inventoryId;
        this.enchantMods = enchantMods;
        this.craftedMods = craftedMods;
        this.gems = gems;
    }

    public ArrayList<Gem> getGems() {
        return gems;
    }

    public void setGems(ArrayList<Gem> gems) {
        this.gems = gems;
    }

    public JSONArray getCraftedMods() {
        return craftedMods;
    }

    public JSONArray getEnchantMods() {
        return enchantMods;
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
