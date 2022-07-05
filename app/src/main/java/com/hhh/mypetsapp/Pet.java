package com.hhh.mypetsapp;

import android.net.Uri;

import java.util.Date;

public class Pet {
    private String name;
    private String species;
    private String breed;
    private String sex;
    private String birthday;
    private String hair;
    private Uri photoUri;

    public Pet(){}

    public Pet(String name, String species, String breed, String sex, String birthday, String hair, Uri photoUri) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.sex = sex;
        this.birthday = birthday;
        this.hair = hair;
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHair() {
        return hair;
    }

    public void setHair(String hair) {
        this.hair = hair;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }
    public void setPhotoUri(Uri photoUri){
        this.photoUri = photoUri;
    }
}
