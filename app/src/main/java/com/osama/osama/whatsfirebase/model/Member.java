package com.osama.osama.whatsfirebase.model;

public class Member
{
    private String uid,name,job_title,email;

    public Member(String uid, String name, String job_title, String email) {
        this.uid = uid;
        this.name = name;
        this.job_title = job_title;
        this.email = email;
    }

    public Member()
    {}
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
