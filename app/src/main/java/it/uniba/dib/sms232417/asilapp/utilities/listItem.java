package it.uniba.dib.sms232417.asilapp.utilities;

public class listItem {

    private String title;
    private String description;
    private int iconResId; // New field
    private String uuid;

    public listItem(String title, String description, int iconResId, String uuid) {
        this.title = title;
        this.description = description;
        this.uuid = uuid;
        this.iconResId = iconResId; // Set the icon resource ID
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titolo) {
        this.title = titolo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}