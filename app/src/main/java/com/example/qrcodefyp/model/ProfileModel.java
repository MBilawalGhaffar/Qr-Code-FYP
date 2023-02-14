package com.example.qrcodefyp.model;

public class ProfileModel {
    private String age;
    private String gender;
    private String imageUrl;

    public ProfileModel(String age, String gender, String imageUrl) {
        this.age = age;
        this.gender = gender;
        this.imageUrl = imageUrl;
    }

    public ProfileModel() {
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
