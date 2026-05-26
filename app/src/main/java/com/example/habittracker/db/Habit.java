package com.example.habittracker.db;

public class Habit {
    public int id;
    public String name;
    public boolean isDoneToday;

    public Habit(int id, String name, boolean isDoneToday) {
        this.id = id;
        this.name = name;
        this.isDoneToday = isDoneToday;
    }
}