package it.uniba.dib.sms232417.asilapp.utilities;

public class listItem {

    private String title;
    private String description;

    public listItem(String title, String description) {
        this.title = title;
        this.description = description;
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

}