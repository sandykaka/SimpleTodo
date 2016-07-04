package com.example.simpletodo;

public class ItemsList {
    private String item;
    private String priority;
    private String date;

    public ItemsList(String item, String priority, String date) {
        this.item = item;
        this.priority = priority;
        this.date = date;
    }

    public String getItems() {
        return item;
    }

    public String getPriority() {
        return priority;
    }

    public String getDate() {
        return date;
    }
}

