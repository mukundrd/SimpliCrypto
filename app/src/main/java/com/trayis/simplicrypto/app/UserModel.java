package com.trayis.simplicrypto.app;

public class UserModel {
    private String createTime;

    private String lastName;

    private String picUrl;

    private String location;

    private String serverAuthCode;

    private String idToken;

    private String username;

    private String emailId;

    private String _id;

    private String name;

    private String accountType;

    private String firstName;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getServerAuthCode() {
        return serverAuthCode;
    }

    public void setServerAuthCode(String serverAuthCode) {
        this.serverAuthCode = serverAuthCode;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "ClassPojo [createTime = " + createTime + ", lastName = " + lastName + ", picUrl = " + picUrl + ", location = " + location + ", serverAuthCode = " + serverAuthCode + ", idToken = " + idToken + ", username = " + username + ", emailId = " + emailId + ", _id = " + _id + ", name = " + name + ", accountType = " + accountType + ", firstName = " + firstName + "]";
    }
}
