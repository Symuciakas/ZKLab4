package com.example.projectchibi;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface HourDao {
    @Insert(onConflict = REPLACE)
    void insert(HourData hourData);

    @Delete
    void delete(HourData hourData);

    @Delete
    void reset(List<HourData> hours);

    @Query("UPDATE hour_data_table SET steps = :sStep, hour = :sHour, year = :sYear, month = :sMonth, weekday = :sWeekday, day = :sDay WHERE ID = :sID")
    void update(int sID, int sStep, int sHour, int sDay, int sYear, int sMonth, int sWeekday);

    @Query("SELECT * FROM hour_data_table")
    List<HourData> getAll();

    @Query("UPDATE hour_data_table SET steps = :sStep WHERE hour = :sHour AND year = :sYear AND month = :sMonth AND day = :sDay")
    void updateByData(int sStep, int sHour, int sDay, int sMonth, int sYear);

    @Query("SELECT steps FROM hour_data_table WHERE hour = :sHour AND year = :sYear AND month = :sMonth AND day = :sDay")
    int getStepsHour(int sHour, int sDay, int sMonth, int sYear);

    @Query("SELECT SUM(steps) FROM hour_data_table WHERE year = :sYear AND month = :sMonth AND day = :sDay")
    int getAllStepsByDay(int sYear, int sMonth, int sDay);

    @Query("SELECT SUM(steps) FROM hour_data_table WHERE year = :sYear AND month = :sMonth")
    int getAllStepsByMonth(int sYear, int sMonth);

    @Query("SELECT SUM(steps) FROM hour_data_table WHERE year = :sYear")
    int getAllStepsByYear(int sYear);

    @Query("SELECT SUM(steps) FROM hour_data_table")
    int getAllSteps();

    @Query("SELECT * FROM hour_data_table WHERE hour = :sHour AND year = :sYear AND month = :sMonth AND day = :sDay")
    HourData getHourdata(int sHour, int sDay, int sMonth, int sYear);

    @Query("SELECT Count(steps) FROM hour_data_table")
    int getCount();

    @Query("SELECT COUNT(steps) FROM hour_data_table WHERE year = :sYear")
    int getCountByYear(int sYear);

    @Query("SELECT COUNT(steps) FROM hour_data_table WHERE year = :sYear AND month = :sMonth")
    int getCountByMonth(int sYear, int sMonth);

    @Query("SELECT COUNT(steps) FROM hour_data_table WHERE year = :sYear AND month = :sMonth AND day = :sDay")
    int getCountByDay(int sYear, int sMonth, int sDay);
}
