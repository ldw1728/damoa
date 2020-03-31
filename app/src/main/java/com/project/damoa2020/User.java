package com.project.damoa2020;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String Email;
    private String name;
    private String UID;
    private LatLng latLng;
    private ArrayList<String> friends;
    private ArrayList<String> groups;

    public ArrayList<String> getGroups() {
        if(groups == null)
            groups = new ArrayList<>();
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public User(){

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser(FirebaseUser user){
        setEmail(user.getEmail());
        setUID(user.getUid());
    }

    public ArrayList<String> getFriends() {
        if(friends == null){
            friends = new ArrayList<>();
        }
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void addFriend(String email){
        if(friends == null){
            friends = new ArrayList<>();
        }
        this.friends.add(email);
    }

    public void removeFriend(String email){
        if(this.friends != null)
            this.friends.remove(email);
    }

    public void addGroup(String gid){
        if(groups == null){
            groups = new ArrayList<>();
        }
        groups.add(gid);
    }

    public void removeGroup(String gid){
        if(groups != null){
            groups.remove(gid);
        }
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
