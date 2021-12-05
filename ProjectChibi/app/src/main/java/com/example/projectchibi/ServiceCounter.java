package com.example.projectchibi;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServiceCounter extends Service implements SensorEventListener {

    MediaPlayer player;

    private int totalStepCount;
    private int stepGoalIndex;
    private SensorManager sensorManager;
    private NotificationManagerCompat notificationManagerCompat;
    private int notificationId = 0;
    private BroadcastReceiver hourUpdateReceiver;
    private int currentHour;
    private int currentDay;
    private int currentMonth;
    private int currentYear;
    private int lastHourStepCount;

    public static final String EXERCISE_SHARED_PREFS = "exerciseSharedPrefs";
    public static final String TOTAL_STEP_COUNT = "totalStepCont";
    public static final String STEP_GOAL_INDEX = "stepGoalIndex";
    public static final String NOTIFICATION_ID = "notificationID";
    public static final String USER_INFO_SET = "userInfoSet";
    public static final String USER_HEIGHT = "userHeight";
    public static final String USER_WEIGHT = "userWeight";
    public static final String USER_GENDER = "userGender";
    public static final String CURRENT_HOUR = "currentHour";
    public static final String CURRENT_DAY = "currentDay";
    public static final String CURRENT_MONTH = "currentMonth";
    public static final String CURRENT_YEAR = "currentYear";
    public static final String LAST_HOUR_STEP_COUNT = "lastHourStepCount";
    public static final String EXERCISE_STARTED = "exerciseStarted";

    List<HourData> dataList = new ArrayList<>();
    HourRoomDB database;

    private Goal[] stepGoals = {new Goal("null", 0),
            new Goal("Starting small", 1),
            new Goal("Ten steps", 10),
            new Goal("Nice", 69),
            new Goal("A hundred steps", 100),
            new Goal("Blaze it", 420),
            new Goal("A thousand steps", 1000),
            new Goal("Elite", 1337),
            new Goal("Five  thousand steps", 5000),
            new Goal("Ten thousand steps", 10000),
            new Goal("Fifty thousand steps", 50000),
            new Goal("Hundred thousand steps", 100000),
            new Goal("Half a million steps", 500000),
            new Goal("One million steps!", 1000000),
            new Goal("Ten million steps!!", 10000000),
            new Goal("A hunded mlion teps!!!!!", 100000000),
            new Goal("1billdevbjed!!!!!!!!!", 1000000000)};

    private boolean userInfoSet = false;
    private int userHeight = 0, userWeight = 0;
    private String userGender;
    private boolean isExerciseStarted;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManagerCompat = NotificationManagerCompat.from(this);
        database = HourRoomDB.getInstance(this);
        dataList = database.mainDao().getAll();
        loadPrefData();
        updateDatabase();
        startHourUpdate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);

        }/**/
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        unregisterReceiver(hourUpdateReceiver);
        updateDatabase();
        savePrefData();
        player.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void savePrefData()  {
        SharedPreferences sharedPreferences = getSharedPreferences(EXERCISE_SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TOTAL_STEP_COUNT, totalStepCount);
        editor.putInt(STEP_GOAL_INDEX, stepGoalIndex);
        editor.putInt(NOTIFICATION_ID, notificationId);
        editor.putBoolean(USER_INFO_SET, userInfoSet);
        editor.putInt(USER_WEIGHT, userWeight);
        editor.putInt(USER_HEIGHT, userHeight);
        editor.putString(USER_GENDER, userGender);
        editor.putInt(CURRENT_HOUR, currentHour);
        editor.putInt(CURRENT_DAY, currentDay);
        editor.putInt(CURRENT_MONTH, currentMonth);
        editor.putInt(CURRENT_YEAR, currentYear);
        editor.putInt(LAST_HOUR_STEP_COUNT, lastHourStepCount);
        editor.putBoolean(EXERCISE_STARTED, isExerciseStarted);
        editor.apply();
    }

    void loadPrefData() {
        SharedPreferences sharedPreferences = getSharedPreferences(EXERCISE_SHARED_PREFS, MODE_PRIVATE);
        totalStepCount = sharedPreferences.getInt(TOTAL_STEP_COUNT, 0);
        stepGoalIndex = sharedPreferences.getInt(STEP_GOAL_INDEX, 1);
        notificationId = sharedPreferences.getInt(NOTIFICATION_ID, 0);
        userInfoSet = sharedPreferences.getBoolean(USER_INFO_SET, false);
        userWeight = sharedPreferences.getInt(USER_WEIGHT, 0);
        userHeight = sharedPreferences.getInt(USER_HEIGHT, 0);
        userGender = sharedPreferences.getString(USER_GENDER, "");
        currentHour = sharedPreferences.getInt(CURRENT_HOUR, 0);
        currentDay = sharedPreferences.getInt(CURRENT_DAY, 0);
        currentMonth = sharedPreferences.getInt(CURRENT_MONTH, 0);
        currentYear = sharedPreferences.getInt(CURRENT_YEAR, 0);
        lastHourStepCount = sharedPreferences.getInt(LAST_HOUR_STEP_COUNT, 0);
        isExerciseStarted = sharedPreferences.getBoolean(EXERCISE_STARTED, false);
    }

    void updateDatabase() {
        if(currentYear != Calendar.getInstance().get(Calendar.YEAR) || currentMonth != Calendar.getInstance().get(Calendar.MONTH) || currentDay != Calendar.getInstance().get(Calendar.DAY_OF_MONTH) || currentHour != Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        {
            database.mainDao().updateByData(lastHourStepCount, currentHour, currentDay, currentMonth, currentYear);
            updateCurrentTime();
            database.mainDao().insert(new HourData());
            lastHourStepCount = 0;
        }
        else
        {
            database.mainDao().updateByData(lastHourStepCount, currentHour, currentDay, currentMonth, currentYear);
        }
    }

    void updateCurrentTime() {
        currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
    }

    void startHourUpdate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        hourUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateDatabase();
            }
        };
        registerReceiver(hourUpdateReceiver, intentFilter);
    }

    private void sendNotification(CharSequence title, CharSequence text) {
        //Seems to be done and working
        Intent intent = new Intent(this, ChibiActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel1")
                .setSmallIcon(R.drawable.baseline_directions_walk_black_48dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setAutoCancel(true);

        notificationManagerCompat.notify(notificationId, builder.build());
        notificationId++;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        totalStepCount++;
        lastHourStepCount++;
        if (totalStepCount >= stepGoals[stepGoalIndex].getAmount()) {
            stepGoalIndex++;
            sendNotification("Exercise goal completed!", stepGoals[stepGoalIndex - 1].getName());
        }
        savePrefData();
        updateDatabase();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
