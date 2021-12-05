package com.example.projectchibi;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "hour_data_table")
public class HourData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "steps")
    private int steps;

    @ColumnInfo(name = "year")
    private int year;
    @ColumnInfo(name = "month")
    private int month;
    @ColumnInfo(name = "day")
    private int day;
    @ColumnInfo(name = "hour")
    private int hour;
    @ColumnInfo(name = "weekday")
    private int weekday;

    public HourData() {
        steps = 0;
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        weekday = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        while(weekday > 6)
        {
            weekday = weekday - 7;
        }
    }

    public HourData(int steps, int year, int month, int day, int hour, int weekday) {
        this.steps = steps;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.weekday = weekday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }
}
