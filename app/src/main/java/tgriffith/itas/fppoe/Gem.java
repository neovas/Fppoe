package tgriffith.itas.fppoe;

public class Gem {

    public String typeLine;
    public String icon;
    public String quality;
    public String level;
    public int groupNum;
    public String equipmentType;

    public Gem(String typeLine, String icon, String quality, String level, int groupNum, String equipmentType) {
        this.typeLine = typeLine;
        this.icon = icon;
        this.quality = quality;
        this.level = level;
        this.groupNum = groupNum;
        this.equipmentType = equipmentType;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public String getTypeLine() {
        return typeLine;
    }

    public void setTypeLine(String typeLine) {
        this.typeLine = typeLine;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }
}
