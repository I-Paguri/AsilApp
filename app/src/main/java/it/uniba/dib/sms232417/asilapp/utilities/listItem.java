package it.uniba.dib.sms232417.asilapp.utilities;

public class listItem {

    private String title;
    private String description;
    private String uuid;
    private String imageUrl;

    public listItem(String title, String description, String imageUrl, String uuid) {
        this.title = title;
        this.description = description;
        this.uuid = uuid;
        this.imageUrl = imageUrl; // Set the icon resource ID
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


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}