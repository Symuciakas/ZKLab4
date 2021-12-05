package com.example.projectchibi;

public class User {
    String givenName, familyName, email, id;

    public User(){ }
    public User(String givenName, String familyName, String email, String id) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.email = email;
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
