package com.example.projectchibi;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class MyPrefsBackupAgent extends BackupAgentHelper {
    static final String PREFS = "exerciseSharedPrefs";

    static final String PREFS_BACKUP_KEY = "2d5g144F4dg4F545F";

    @Override
    public void onCreate()
    {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
