package com.example.lfg.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.StorageReference;

@IgnoreExtraProperties
public class Comment {
    String id;
    User author;
    String userId;
    String username;
    String body;
    String photoUrl;
    StorageReference photoReference;

    public Comment(){}

    public Comment(String id, String userId, String username, String body){
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.body = body;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public StorageReference getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(StorageReference photoReference) {
        this.photoReference = photoReference;
    }
}
