package com.bawp.babyneeds.model;

public class Item {
    private int id;
    private String Plan;
    private String Place;
    private String Time;
    private String Deadline; // 3, 4 months...12 months...
    private String dateItemAdded;

    public Item() {
    }

    public Item(String Plan, String Place, String Time, String Deadline, String dateItemAdded) {
        this.Plan = Plan;
        this.Place = Place;
        this.Time = Time;
        this.Deadline = Deadline;
        this.dateItemAdded = dateItemAdded;
    }
    public Item(int id,String Plan, String Place, String Time, String Deadline, String dateItemAdded) {
        this.id = id;
        this.Plan = Plan;
        this.Place = Place;
        this.Time = Time;
        this.Deadline = Deadline;
        this.dateItemAdded = dateItemAdded;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlan() {
        return Plan;
    }

    public void setPlan(String plan) {
        Plan = plan;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDeadline() {
        return Deadline;
    }

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }

    public String getDateItemAdded() {
        return dateItemAdded;
    }

    public void setDateItemAdded(String dateItemAdded) {
        this.dateItemAdded = dateItemAdded;
    }
}



