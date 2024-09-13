package com.the.fastorder;

public class ModelShop {

    private String uid, email, name,  phone,   timesTemp, accountType,  profileImage;

    public ModelShop() {
    }

    public ModelShop(String uid, String email, String name,  String phone, String timesTemp, String accountType,  String profileImage) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.timesTemp = timesTemp;
        this.accountType = accountType;

        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getTimesTemp() {
        return timesTemp;
    }

    public void setTimesTemp(String timesTemp) {
        this.timesTemp = timesTemp;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
