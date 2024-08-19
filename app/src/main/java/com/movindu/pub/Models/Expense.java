package com.movindu.pub.Models;

public class Expense {
   private Category category;
   private String fb_user_id;
   private Datee date;
   private double amount;
   private String email;
   private String note;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getFb_user_id() {
        return fb_user_id;
    }

    public void setFb_user_id(String fb_user_id) {
        this.fb_user_id = fb_user_id;
    }

    public Datee getDate() {
        return date;
    }

    public void setDate(Datee date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
