package com.newproject.zklab4_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Random;

public class AthleteActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    Context context = this;
    ConstraintLayout athleteActivityLayout;
    Button button1, button2, button3, button4, button5;
    BarChart barChart;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete);
        athleteActivityLayout = findViewById(R.id.athleteActivityLayout);
        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setBackgroundColor(getResources().getColor(R.color.children));
                button2.setBackgroundColor(getResources().getColor(R.color.main));
                button3.setBackgroundColor(getResources().getColor(R.color.main));
                button4.setBackgroundColor(getResources().getColor(R.color.main));
                button5.setBackgroundColor(getResources().getColor(R.color.main));
            }
        });
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setBackgroundColor(getResources().getColor(R.color.main));
                button2.setBackgroundColor(getResources().getColor(R.color.children));
                button3.setBackgroundColor(getResources().getColor(R.color.main));
                button4.setBackgroundColor(getResources().getColor(R.color.main));
                button5.setBackgroundColor(getResources().getColor(R.color.main));
            }
        });
        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setBackgroundColor(getResources().getColor(R.color.main));
                button2.setBackgroundColor(getResources().getColor(R.color.main));
                button3.setBackgroundColor(getResources().getColor(R.color.children));
                button4.setBackgroundColor(getResources().getColor(R.color.main));
                button5.setBackgroundColor(getResources().getColor(R.color.main));
            }
        });
        button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setBackgroundColor(getResources().getColor(R.color.main));
                button2.setBackgroundColor(getResources().getColor(R.color.main));
                button3.setBackgroundColor(getResources().getColor(R.color.main));
                button4.setBackgroundColor(getResources().getColor(R.color.children));
                button5.setBackgroundColor(getResources().getColor(R.color.main));
            }
        });
        button5 = findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setBackgroundColor(getResources().getColor(R.color.main));
                button2.setBackgroundColor(getResources().getColor(R.color.main));
                button3.setBackgroundColor(getResources().getColor(R.color.main));
                button4.setBackgroundColor(getResources().getColor(R.color.main));
                button5.setBackgroundColor(getResources().getColor(R.color.children));
            }
        });
        button1.setBackgroundColor(getResources().getColor(R.color.main));
        button2.setBackgroundColor(getResources().getColor(R.color.main));
        button3.setBackgroundColor(getResources().getColor(R.color.main));
        button4.setBackgroundColor(getResources().getColor(R.color.main));
        button5.setBackgroundColor(getResources().getColor(R.color.main));
        barChart = findViewById(R.id.barChart);
        Random random = new Random();
        ArrayList<BarEntry> data = new ArrayList<>();
        for(int i = 0; i < 24; i++) {
            data.add(new BarEntry(i, random.nextInt(30)+20));
        }
        BarDataSet barDataSet = new BarDataSet(data, "Data");
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Some data");
        barChart.animateY(2000);

    }

    public void openSettingsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.exercise_popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        athleteActivityLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        switch(item.getItemId())
        {
            case R.id.exerciseSettingsItem1:
                createInfoDialog();
                return true;
            case R.id.exerciseSettingsItem2:
                Toast.makeText(context, "You are here!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exerciseSettingsItem3:
                startActivity(new Intent(context, ChildrenActivity.class));
                return true;
            default:
                return false;

        }
    }

    public void createInfoDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View exerciseInfoPopupView = getLayoutInflater().inflate(R.layout.activity_information, null);
        NumberPicker exerciseWeightNumberPicker = exerciseInfoPopupView.findViewById(R.id.exerciseWeightNumberPicker);
        exerciseWeightNumberPicker.setMinValue(10);
        exerciseWeightNumberPicker.setMaxValue(1000);
        exerciseWeightNumberPicker.setValue(75);
        NumberPicker exerciseHeightNumberPicker = exerciseInfoPopupView.findViewById(R.id.exerciseHeightNumberPicker);
        exerciseHeightNumberPicker.setMinValue(0);
        exerciseHeightNumberPicker.setMaxValue(300);
        exerciseHeightNumberPicker.setValue(175);
        Button saveInfoButton = exerciseInfoPopupView.findViewById(R.id.exerciseInfoEnter);
        Button cancelInfoButton = exerciseInfoPopupView.findViewById(R.id.exerciseInfoCancel);
        dialogBuilder.setView(exerciseInfoPopupView);
        dialog = dialogBuilder.create();
        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        cancelInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
