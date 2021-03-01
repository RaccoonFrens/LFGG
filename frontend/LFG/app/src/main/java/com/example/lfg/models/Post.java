package com.example.lfg.models;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    String id;
    String logoName;
    String game;
    User author;
    String user;
    String body;
    String tag;
    int size;
    List<Comment> replies;
    Map<String, String> timestamp;
    long time;
    long timer;
    long timeEnd;

    public Post(){}

    public Post(String game, int size, Map<String, String> timestamp, String user, long timer){
        this.game = game;
        this.size = size;
        this.timestamp = timestamp;
        this.logoName = game + ".png";
        this.user = user;
        this.timer = timer;
    }



    public Post(String game, int size, String logoName, long timeEnd){
        this.game = game;
        this.size = size;
        this.logoName = logoName;
        this.timeEnd = timeEnd;
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

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", logoName='" + logoName + '\'' +
                ", game='" + game + '\'' +
                ", author=" + author +
                ", user='" + user + '\'' +
                ", body='" + body + '\'' +
                ", tag='" + tag + '\'' +
                ", size=" + size +
                ", replies=" + replies +
                ", timestamp=" + timestamp +
                ", time=" + time +
                ", timer=" + timer +
                '}';
    }
}
