package com.example.projectchibi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener, PopupMenu.OnMenuItemClickListener {

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
    public static final String DATA_CLOUDED = "dataClouded";

    List<HourData> dataList = new ArrayList<>();
    HourRoomDB database;

    private Context context = this;
    private LinearLayout exerciseActivityLayout;
    private boolean isActivityRunning;
    private boolean isExerciseStarted;
    private Chibi currentChibi;
    private TextView exerciseChibiTextView;
    private Button exerciseStartButton;
    private TextView exerciseGoalValueTextView;
    private int totalStepCount;
    private TextView exerciseStepsValueTextView;
    private int stepGoalIndex;
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
    private ProgressBar goalProgressBar;
    private SensorManager sensorManager;
    private NotificationManagerCompat notificationManagerCompat;
    private int notificationId = 0;
    private PieChart exercisePieChart;
    //private BarChart exerciseBarChart;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private NumberPicker exerciseWeightNumberPicker, exerciseHeightNumberPicker;
    private Button saveInfoButton, cancelInfoButton;
    private RadioGroup exerciseGenderRadioGroup;
    private RadioButton exerciseInfoMale, exerciseInfoFemale;
    private boolean userInfoSet = false;
    private int userHeight = 0, userWeight = 0;
    private String userGender;

    private TextView statisticsStepCountTextView;
    private TextView statisticsKcalCountTextView;
    private TextView statisticsDistanceCountTextView;
    private Button exerciseStatisticsButton;

    private BroadcastReceiver hourUpdateReceiver;
    private int currentHour;
    private int currentDay;
    private int currentMonth;
    private int currentYear;
    private int lastHourStepCount;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String personId;
    private boolean cloudDataSet;

    private int last5SecSteps;
    private int A[] = {0, 0, 0, 0 ,0, 0, 0, 0, 0 ,0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exerciseactivitydesign);
        exerciseActivityLayout = findViewById(R.id.exerciseActivityLayout);
        Intent intent = getIntent();
        currentChibi = intent.getParcelableExtra("Data");
        personId = intent.getStringExtra("userId");

        exerciseChibiTextView = findViewById(R.id.exerciseChibiNameTextView);
        exerciseChibiTextView.setText(currentChibi.getName());
        exerciseStartButton = findViewById(R.id.exerciseStartButton);
        exerciseStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopExercise();
            }
        });
        exerciseGoalValueTextView = findViewById(R.id.exerciseGoalValueTextView);
        exerciseStepsValueTextView = findViewById(R.id.exerciseStepsValueTextView);
        goalProgressBar = findViewById(R.id.goalProgressBar);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        isActivityRunning = false;
        isExerciseStarted = false;
        database = HourRoomDB.getInstance(this);
        checkCloudData();
        //recognise running walking biking
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(this, ServiceCounter.class));
        exerciseActivityLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        isActivityRunning = true;
        dataList = database.mainDao().getAll();
        loadPrefData();
        updateDatabase();
        startHourUpdate();
        updateStepGoalCountTextView();
        updateStepCountTextView();
        int a = (totalStepCount - stepGoals[stepGoalIndex - 1].getAmount()) * 100 / (stepGoals[stepGoalIndex].getAmount() - stepGoals[stepGoalIndex - 1].getAmount());
        goalProgressBar.setProgress(a);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDataToCloud();
        savePrefData();
        updateDatabase();
        isActivityRunning = false;
        sensorManager.unregisterListener(this);
        unregisterReceiver(hourUpdateReceiver);
        if(isExerciseStarted)
        {
            startService(new Intent(this, ServiceCounter.class));
        }
    }

    @Override
    protected void onStop() {
        //for data reset
        //destroyAll();
        super.onStop();
    }

    private void startStopExercise() {
        if(isExerciseStarted)
        {
            exerciseStartButton.setText(R.string.start_value);
            isExerciseStarted = false;
        }
        else {
            exerciseStartButton.setText(R.string.stop_value);
            isExerciseStarted = true;
        }
    }

    private void updateStepCountTextView() {
        String a = String.valueOf(totalStepCount);
        exerciseStepsValueTextView.setText(a);
    }

    private void updateStepGoalCountTextView() {
        String text = String.valueOf(stepGoals[stepGoalIndex].getAmount());
        exerciseGoalValueTextView.setText(text);
    }

    public void populateChartDay(PieChart pieChart)  {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for(int i = 0; i < 24; i++) {
            int a = database.mainDao().getStepsHour(i, currentDay, currentMonth, currentYear);
            if(a > 0) {
                String z = "0";
                if(i > 9)
                {
                     z = "";
                }
                entries.add(new PieEntry(a, z + i + ":00"));
            }
        }
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setCenterText("Steps walked today");
        pieChart.animateY(2000);
    }

    public void populateChartMonth(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for(int i = 0; i < 31; i++)
        {
            int a = database.mainDao().getAllStepsByDay(currentYear, currentMonth, i);

            if(a > 0)
            {
                switch (i)
                {
                    case 0:
                        entries.add(new PieEntry(a, String.valueOf(i) + "st"));
                        break;
                    case 1:
                        entries.add(new PieEntry(a, String.valueOf(i) + "nd"));
                        break;
                    case 2:
                        entries.add(new PieEntry(a, String.valueOf(i) + "rd"));
                        break;
                    default:
                        entries.add(new PieEntry(a, String.valueOf(i) + "th"));
                        break;
                }
            }
        }
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setCenterText("Steps walked this month");
        pieChart.animateY(2000);
    }

    public void populateChartYear(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for(int i = 0; i < 12; i++)
        {
            int a = database.mainDao().getAllStepsByMonth(currentYear, i);
            if(a > 0)
            {
                switch (i){
                    case 0:
                        entries.add(new PieEntry(a, "Jan"));
                        break;
                    case 1:
                        entries.add(new PieEntry(a, "Feb"));
                        break;
                    case 2:
                        entries.add(new PieEntry(a, "Mar"));
                        break;
                    case 3:
                        entries.add(new PieEntry(a, "Apr"));
                        break;
                    case 4:
                        entries.add(new PieEntry(a, "May"));
                        break;
                    case 5:
                        entries.add(new PieEntry(a, "Jun"));
                        break;
                    case 6:
                        entries.add(new PieEntry(a, "Jul"));
                        break;
                    case 7:
                        entries.add(new PieEntry(a, "Aug"));
                        break;
                    case 8:
                        entries.add(new PieEntry(a, "Sep"));
                        break;
                    case 9:
                        entries.add(new PieEntry(a, "Oct"));
                        break;
                    case 10:
                        entries.add(new PieEntry(a, "Nov"));
                        break;
                    case 11:
                        entries.add(new PieEntry(a, "Dec"));
                        break;
                }
            }
        }
        ;
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setCenterText("Steps walked this year");
        pieChart.animateY(2000);
    }

    public void openSettingsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.exercise_popup_menu);
        popup.show();
    }

    public void createInfoDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View exerciseInfoPopupView = getLayoutInflater().inflate(R.layout.informationactivitydesign, null);
        exerciseWeightNumberPicker = exerciseInfoPopupView.findViewById(R.id.exerciseWeightNumberPicker);
        exerciseWeightNumberPicker.setMinValue(10);
        exerciseWeightNumberPicker.setMaxValue(1000);
        exerciseWeightNumberPicker.setValue(75);
        exerciseHeightNumberPicker = exerciseInfoPopupView.findViewById(R.id.exerciseHeightNumberPicker);
        exerciseHeightNumberPicker.setMinValue(0);
        exerciseHeightNumberPicker.setMaxValue(300);
        exerciseHeightNumberPicker.setValue(175);
        exerciseGenderRadioGroup = exerciseInfoPopupView.findViewById(R.id.exerciseInfoGenderRadioGroup);
        exerciseInfoMale = exerciseInfoPopupView.findViewById(R.id.exerciseInfoMale);
        exerciseInfoFemale = exerciseInfoPopupView.findViewById(R.id.exerciseInfoFemale);
        saveInfoButton = exerciseInfoPopupView.findViewById(R.id.exerciseInfoEnter);
        cancelInfoButton = exerciseInfoPopupView.findViewById(R.id.exerciseInfoCancel);
        dialogBuilder.setView(exerciseInfoPopupView);
        dialog = dialogBuilder.create();
        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exerciseInfoMale.isChecked() || exerciseInfoFemale.isChecked())
                {
                    if(exerciseInfoMale.isChecked())
                    {
                        userGender = "Male";
                    }
                    if(exerciseInfoFemale.isChecked())
                    {
                        userGender = "Female";
                    }
                    userHeight = exerciseHeightNumberPicker.getValue();
                    userWeight = exerciseWeightNumberPicker.getValue();
                    userInfoSet = true;
                    if(personId != null)
                    {
                        Map<String, Object> info = new HashMap<>();
                        info.put("Set", true);
                        info.put("Height", userHeight);
                        info.put("Weight", userWeight);
                        info.put("Gender", userGender);
                        db.collection("users").document(personId).set(info);
                    }
                    dialog.cancel();
                }
                else
                {
                    Toast.makeText(context, "Please select one of two genders", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        if(userInfoSet)
        {
            exerciseWeightNumberPicker.setValue(userWeight);
            exerciseHeightNumberPicker.setValue(userHeight);
            if(userGender == "Male")
            {
                exerciseInfoMale.setChecked(true);
            }
            if(userGender == "Female")
            {
                exerciseInfoFemale.setChecked(true);
            }
        }
        dialog.show();
    }

    void createStatisticDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View exerciseStatisticsPopupView = getLayoutInflater().inflate(R.layout.statisticsactivitydesign, null);
        statisticsStepCountTextView = exerciseStatisticsPopupView.findViewById(R.id.statisticsStepCountTextView);
        statisticsKcalCountTextView = exerciseStatisticsPopupView.findViewById(R.id.statisticsKcalCountTextView);
        statisticsDistanceCountTextView = exerciseStatisticsPopupView.findViewById(R.id.statisticsDistanceCountTextView);

        exercisePieChart = exerciseStatisticsPopupView.findViewById(R.id.exercisePieChart);
        exerciseStatisticsButton = exerciseStatisticsPopupView.findViewById(R.id.exerciseStatisticsCancelButton);
        exerciseStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        exerciseStatisticsPopupView.setOnTouchListener(new View.OnTouchListener() {
            int chartId = 1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float maxX = exerciseStatisticsPopupView.getMeasuredWidth();
                if(maxX/3 > x)
                {
                    chartId--;
                }
                else if(maxX/3*2 < x)
                {
                    chartId++;
                }
                if(chartId == 0 )
                {
                    chartId = 3;
                }
                if(chartId == 4)
                {
                    chartId = 1;
                }
                switch (chartId)
                {
                    case 1 :
                        populateChartDay(exercisePieChart);
                        break;
                    case 2 :
                        populateChartMonth(exercisePieChart);
                        break;
                    case 3 :
                        populateChartYear(exercisePieChart);
                        break;
                }
                return false;
            }
        });
        dialogBuilder.setView(exerciseStatisticsPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
        statisticsStepCountTextView.setText(String.valueOf(totalStepCount));
        statisticsKcalCountTextView.setText(String.valueOf((int)(totalStepCount*userWeight*userHeight*0.0000002)));
        String a = String.valueOf((int)(totalStepCount*userHeight/300)) + "m";
        statisticsDistanceCountTextView.setText(a);
        populateChartDay(exercisePieChart);
    }

    void startHourUpdate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        hourUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateDatabase();
                saveDataToCloud();
            }
        };
        registerReceiver(hourUpdateReceiver, intentFilter);
    }

    void updateDatabase() {
        if(currentYear != Calendar.getInstance().get(Calendar.YEAR) || currentMonth != Calendar.getInstance().get(Calendar.MONTH) || currentDay != Calendar.getInstance().get(Calendar.DAY_OF_MONTH) || currentHour != Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        {
            database.mainDao().updateByData(lastHourStepCount, currentHour, currentDay, currentMonth, currentYear);
            updateCurrentTime();
            if(database.mainDao().getHourdata(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR)) == null)
            {
                database.mainDao().insert(new HourData());
            }
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

    void savePrefData() {
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
        if(isExerciseStarted)
        {
            exerciseStartButton.setText(R.string.stop_value);
        }
    }

    void destroyAll()  {
        database.mainDao().reset(dataList);
        SharedPreferences sharedPreferences = getSharedPreferences(EXERCISE_SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOTAL_STEP_COUNT);
        editor.remove(STEP_GOAL_INDEX);
        editor.remove(NOTIFICATION_ID);
        editor.remove(USER_INFO_SET);
        editor.remove(USER_WEIGHT);
        editor.remove(USER_HEIGHT);
        editor.remove(USER_GENDER);
        editor.remove(CURRENT_HOUR);
        editor.remove(CURRENT_DAY);
        editor.remove(CURRENT_MONTH);
        editor.remove(CURRENT_YEAR);
        editor.remove(LAST_HOUR_STEP_COUNT);
        editor.apply();
    }

    private void checkCloudData() {
        SharedPreferences sharedPreferences = getSharedPreferences(EXERCISE_SHARED_PREFS, MODE_PRIVATE);
        cloudDataSet = sharedPreferences.getBoolean(DATA_CLOUDED, false);
        if(cloudDataSet) {

        } else {
            Toast.makeText(context, "Data load from cloud", Toast.LENGTH_SHORT).show();
            cloudDataSet = true;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DATA_CLOUDED, cloudDataSet);
            editor.apply();
            getDataFromCloud();
            //saveDataToCloud();
        }
    }

    private void saveDataToCloud() {
        if(dataList.get(0) != null && personId != null)
        {
            for (HourData h :
                 dataList) {
                db.collection("users").document(personId).collection("data").document(
                        String.valueOf(h.getYear()) + "_" +
                                String.valueOf(h.getMonth()) + "_" +
                                String.valueOf(h.getDay()) + "_" +
                                String.valueOf(h.getHour())).set(h);
            }
        }
    }

    private void getDataFromCloud() {
        if(personId != null) {
            DocumentReference refD = db.collection("users").document(personId);
            CollectionReference refC = refD.collection("data");
            refC.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    if(database == null)
                    {
                        Toast.makeText(context, "Check database", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DocumentSnapshot ds: documentSnapshots) {
                            try {
                                int steps = Integer.parseInt(String.valueOf(ds.get("steps")));
                                int year = Integer.parseInt(String.valueOf(ds.get("year")));
                                int month = Integer.parseInt(String.valueOf(ds.get("month")));
                                int day = Integer.parseInt(String.valueOf(ds.get("day")));
                                int hour = Integer.parseInt(String.valueOf(ds.get("hour")));
                                int weekday = Integer.parseInt(String.valueOf(ds.get("weekday")));
                                if(database.mainDao().getHourdata(hour, day, month, year) != null)
                                {
                                    database.mainDao().updateByData(steps, hour, day, month, year);
                                } else {
                                    database.mainDao().insert(new HourData(steps, year, month, day, hour, weekday));
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(context, "Failed to cast int", Toast.LENGTH_SHORT).show();
                            }
                        }
                        totalStepCount = database.mainDao().getAllSteps();
                        stepGoalIndex = 0;
                        while (totalStepCount >= stepGoals[stepGoalIndex].getAmount()) {
                            stepGoalIndex++;
                        }
                        notificationId = 0;
                        updateStepCountTextView();
                        updateStepGoalCountTextView();
                        savePrefData();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(isExerciseStarted)
        {
            totalStepCount++;
            last5SecSteps++;
            lastHourStepCount++;
            if(totalStepCount >= stepGoals[stepGoalIndex].getAmount())
            {
                stepGoalIndex++;
                if(isActivityRunning)
                {
                    updateStepGoalCountTextView();
                }
            }
        }
        if(isActivityRunning && isExerciseStarted)
        {
            updateStepCountTextView();
            int a = (totalStepCount-stepGoals[stepGoalIndex-1]
                    .getAmount())*100/(stepGoals[stepGoalIndex].
                    getAmount()-stepGoals[stepGoalIndex-1].getAmount());
            goalProgressBar.setProgress(a);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        exerciseActivityLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        switch(item.getItemId())
        {
            case R.id.exerciseSettingsItem1:
                createInfoDialog();
                return true;
            case R.id.exerciseSettingsItem2:
                createStatisticDialog();
                return true;
            case R.id.exerciseSettingsItem3:
                Toast.makeText(context, "Item3 clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;

        }
    }
}