package com.example.lfg.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class User {
    String id;
    String username;
    String email;
    String bio;
    List<Post> posts;
    String league_id;
    String sum_name;
    String league_region;

    public User(){
        bio = "";
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }

    public User(String id, String username, String email){
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getLeague_id() {
        return league_id;
    }

    public void setLeague_id(String league_id) {
        this.league_id = league_id;
    }

    public String getSum_name() {
        return sum_name;
    }

    public void setSum_name(String sum_name) {
        this.sum_name = sum_name;
    }

    public String getLeague_region() {
        return league_region;
    }

    public void setLeague_region(String league_region) {
        this.league_region = league_region;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
