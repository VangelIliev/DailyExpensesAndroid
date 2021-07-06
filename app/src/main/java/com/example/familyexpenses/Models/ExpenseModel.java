package com.example.familyexpenses.Models;

public class ExpenseModel {
    String Category, Date, Amount, Remarks, Id;

    public ExpenseModel(){

    }
    public ExpenseModel(String category, String date, String amount, String remarks,String id) {
        Category = category;
        Date = date;
        Amount = amount;
        Remarks = remarks;
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }
}
