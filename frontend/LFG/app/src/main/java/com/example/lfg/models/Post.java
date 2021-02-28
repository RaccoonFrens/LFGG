package com.example.lfg.models;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Post {
    String id;
    String logoName;
    String game;
    User author;
    String body;
    String tag;
    Date createdAt;
    Date duration;
    int size;
    List<Comment> replies;

public Post(){}

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public String getGame() { return game; }

    public void setGame(String game) { this.game = game; }

    public String getLogoName() { return logoName; }

    public void setLogoName(String logoName) { this.logoName = logoName; }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", logoName='" + logoName + '\'' +
                ", game='" + game + '\'' +
                ", author=" + author +
                ", body='" + body + '\'' +
                ", tag='" + tag + '\'' +
                ", createdAt=" + createdAt +
                ", duration=" + duration +
                ", size=" + size +
                ", replies=" + replies +
                '}';
    }
}
