package ru.renelogist.chat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FriendlyMessage implements Serializable {

    public String id;
    public String text;
    public String name;
    public String photoUrl;
    public String imageUrl;
    public String dateTime;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl, String imageUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(System.currentTimeMillis()));
    }

    public FriendlyMessage(String id, String text, String name, String photoUrl, String imageUrl, String dateTime) {
        this.id = id;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(System.currentTimeMillis()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
