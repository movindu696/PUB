package com.movindu.pub.Models;

public class Income {
    private double amount;
    private Datee date;
    private String note;
    private String fb_user_id;
    private String email;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Datee getDate() {
        return date;
    }

    public void setDate(Datee date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFb_user_id() {
        return fb_user_id;
    }

    public void setFb_user_id(String fb_user_id) {
        this.fb_user_id = fb_user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
