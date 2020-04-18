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
    public String rarity;

    /**
     * Optional Fields
     */
    String itemQuality = "";
    String evasionRating = "";
    String energyShield = "";
    String armour = "";
    String physicalDamage = "";
    String criticalChance = "";
    String attackSpeed = "";
    String elementalDamage = "";

    public Item(String imageUrl, String name, String typeLine, JSONArray implicitMods, JSONArray explicitMods, String inventoryId, JSONArray enchantMods, JSONArray craftedMods, ArrayList<Gem> gems, String rarity) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.typeLine = typeLine;
        this.implicitMods = implicitMods;
        this.explicitMods = explicitMods;
        this.inventoryId = inventoryId;
        this.enchantMods = enchantMods;
        this.craftedMods = craftedMods;
        this.gems = gems;
        this.rarity = rarity;
    }

    public String getItemQuality() {
        return itemQuality;
    }

    public void setItemQuality(String itemQuality) {
        this.itemQuality = itemQuality;
    }

    public String getEvasionRating() {
        return evasionRating;
    }

    public void setEvasionRating(String evasionRating) {
        this.evasionRating = evasionRating;
    }

    public String getEnergyShield() {
        return energyShield;
    }

    public void setEnergyShield(String energyShield) {
        this.energyShield = energyShield;
    }

    public String getArmour() {
        return armour;
    }

    public void setArmour(String armour) {
        this.armour = armour;
    }

    public String getPhysicalDamage() {
        return physicalDamage;
    }

    public void setPhysicalDamage(String physicalDamage) {
        this.physicalDamage = physicalDamage;
    }

    public String getCriticalChance() {
        return criticalChance;
    }

    public void setCriticalChance(String criticalChance) {
        this.criticalChance = criticalChance;
    }

    public String getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(String attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public String getElementalDamage() {
        return elementalDamage;
    }

    public void setElementalDamage(String elementalDamage) {
        this.elementalDamage = elementalDamage;
    }

    public ArrayList<Gem> getGems() {
        return gems;
    }

    public void setGems(ArrayList<Gem> gems) {
        this.gems = gems;
    }

    public String getRarity() {
        return rarity;
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
