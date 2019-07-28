package com.osama.osama.whatsfirebase.model;

public class TaskContent
{

    private String date,uid,content,task_name,status,deadline,time,expected;
    private boolean isFromDatabase;

    public TaskContent(String date, String uid, String content,
                       String task_name, String status, String deadline, String time, String expected) {
        this.date = date;
        this.uid = uid;
        this.content = content;
        this.task_name = task_name;
        this.status = status;
        this.deadline = deadline;
        this.time = time;
        this.expected = expected;
    }

    public TaskContent() {
    }

    public TaskContent(String date, String task_name)
    {
        this.date = date;
        this.task_name = task_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public boolean isFromDatabase() {
        return isFromDatabase;
    }

    public void setFromDatabase(boolean fromDatabase) {
        isFromDatabase = fromDatabase;
    }
}
