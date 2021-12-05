package com.example.projectchibi;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HourData.class}, version = 1, exportSchema = false)
public abstract class HourRoomDB extends RoomDatabase {
    private static HourRoomDB database;
    private static String DATABASE_NAME = "Hour Data Database";
    public synchronized static HourRoomDB getInstance(Context context)
    {
        if(database == null)
        {
            database = Room.databaseBuilder(context.getApplicationContext(),
                    HourRoomDB.class,
                    DATABASE_NAME)
                    //IDK
                    .allowMainThreadQueries()
                    //IDK
                    .fallbackToDestructiveMigration()
                    .build();
            //Problem?
            //database.mainDao().insert(new HourData());
        }
        return database;
    }

    public abstract HourDao mainDao();
}
