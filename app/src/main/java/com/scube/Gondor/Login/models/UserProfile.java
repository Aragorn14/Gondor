package com.scube.Gondor.Login.models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by srikanthsridhara on 7/25/15.
 */
public class UserProfile {

    private String userName = "";
    private String firstName = "";
    private String lastName = "";
    private String gender   = "";
    private String dateOfBirth   = "";
    private String location   = "";
    private String phone   = "";
    private String imageUrl   = "";
    private Integer qbUserId   = -1;
    private String user_type = "";

    public UserProfile(JSONObject userProfile, Context thisContext) {
        try {
            this.userName = (userProfile.has("username") && !userProfile.isNull("username")) ? userProfile.getString("username") : "";
            this.firstName = (userProfile.has("firstname") && !userProfile.isNull("firstname")) ? userProfile.getString("firstname") : "";
            this.lastName = (userProfile.has("lastname") && !userProfile.isNull("lastname")) ? userProfile.getString("lastname") : "";
            this.gender = (userProfile.has("gender") && !userProfile.isNull("gender")) ? userProfile.getString("gender") : "";
            this.dateOfBirth = (userProfile.has("dob") && !userProfile.isNull("dob")) ? userProfile.getString("dob") : "";
            this.location = (userProfile.has("location") && !userProfile.isNull("location")) ? userProfile.getString("location") : "";
            this.phone = (userProfile.has("phone") && !userProfile.isNull("phone")) ? userProfile.getString("phone") : "";
            this.imageUrl = (userProfile.has("imageurl") && !userProfile.isNull("imageurl")) ? userProfile.getString("imageurl") : "";
            this.qbUserId = (userProfile.has("qb_user_id") && !userProfile.isNull("qb_user_id")) ? userProfile.getInt("qb_user_id") : -1;
            this.user_type = (userProfile.has("user_type") && !userProfile.isNull("user_type")) ? userProfile.getString("user_type") : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getQbUserId() {
        return qbUserId;
    }

    public void setQbUserId(Integer qbUserId) {
        this.qbUserId = qbUserId;
    }

    public String getUserType() {
        return user_type;
    }

    public void setUserType(String user_type) {
        this.user_type = user_type;
    }
}
