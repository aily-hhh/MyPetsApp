package com.hhh.mypetsapp;

import android.net.Uri;

public class User {
    private String email;
    private String userName;
    private String phone;
    private String password;
    private String photoUri;

    public User() {}

    public User(String email, String userName, String phone, String password, String photoUri){
        this.email = email;
        this.userName = userName;
        this.phone = phone;
        this.password = password;
        this.photoUri = photoUri;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getPhotoUri() {
        return photoUri;
    }
    public void setPhotoUri(String photoUri){
        this.photoUri = photoUri;
    }
}
