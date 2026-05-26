package com.example.habittracker.db;

public class Habit {
    public int id;
    public String name;
    public boolean isDoneToday;
    public int streak;
    public String lastDoneDate;

    public Habit(int id, String name, boolean isDoneToday, int streak, String lastDoneDate) {
        this.id = id;
        this.name = name;
        this.isDoneToday = isDoneToday;
        this.streak = streak;
        this.lastDoneDate = lastDoneDate;
    }
}