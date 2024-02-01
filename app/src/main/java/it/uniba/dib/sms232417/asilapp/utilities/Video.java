package it.uniba.dib.sms232417.asilapp.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class Video {
    private String title;
    private String link;
    private String channel;
    private Date publishedDate;
    private int views;
    private String length;
    private String description;
    private JSONArray extensions;
    private String thumbnail;

    public Video(String title, String link, String channel, Date publishedDate, int views, String length, String description, JSONArray extensions, String thumbnail) {
        this.title = title;
        this.link = link;
        this.channel = channel;
        this.publishedDate = publishedDate;
        this.views = views;
        this.length = length;
        this.description = description;
        this.extensions = extensions;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getChannel() {
        return channel;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public int getViews() {
        return views;
    }

    public String getLength() {
        return length;
    }

    public String getDescription() {
        return description;
    }

    public JSONArray getExtensions() {
        return extensions;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}