package com.project.damoa2020.ui.main.groupTab;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupInfo implements Serializable {

    private String title;
    private String date;
    private String time;
    private int PON;
    private String detail_info;
    private String imageString;
    private HashMap<String, Boolean> participants;
    private String groupID;
    private Double latitude;
    private Double longitude;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public GroupInfo(){}

    public GroupInfo(String title, String detail_info, String date,String time, int PON, HashMap<String, Boolean> participants , String imageString){
        this.title = title;
        this.detail_info = detail_info;
        this.date = date;
        this.time = time;
        this.PON = PON;
        this.participants = participants;
        this.imageString = imageString;
        this.latitude = null;
        this.longitude = null;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPON() {
        return PON;
    }

    public void setPON(int PON) {
        this.PON = PON;
    }

    public String getDetail_info() {
        return detail_info;
    }

    public void setDetail_info(String detail_info) {
        this.detail_info = detail_info;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public HashMap<String, Boolean> getParticipants() {
        if(participants == null){
            participants = new HashMap<String, Boolean>();
        }
        return participants;
    }

    public void setParticipants(HashMap<String, Boolean> participants) {
        this.participants = participants;
    }

    public void addParticipant(String email){
        if(this.participants == null){
            this.participants = new HashMap<>();
        }
        this.participants.put(email, false);
    }

    public void deleteParticipant(String email){
        if(participants != null){
            participants.remove(email);
        }
    }
}
