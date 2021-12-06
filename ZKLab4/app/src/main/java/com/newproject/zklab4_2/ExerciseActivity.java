package com.newproject.zklab4_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.HashMap;
import java.util.Map;

public class ExerciseActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    Context context = this;
    boolean started = false;
    int steps;
    TextView stepTextView, goalTextView;
    ProgressBar stepProgressBar, goalProgressBar;
    Button exerciseStartButton;
    ConstraintLayout exerciseActivityLayout;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        steps = 0;
        stepTextView = findViewById(R.id.stepTextView);
        stepTextView.setText(String.valueOf(steps));
        goalTextView = findViewById(R.id.goalTextView);
        goalTextView.setText(String.valueOf(steps));
        stepProgressBar = findViewById(R.id.stepProgressBar);
        stepProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(started) {
                    steps++;
                    stepTextView.setText(String.valueOf(steps));
                    goalTextView.setText(String.valueOf(steps));
                    stepProgressBar.setProgress(steps);
                    goalProgressBar.setProgress((int)(steps/4.2));
                }
            }
        });
        goalProgressBar = findViewById(R.id.goalProgressBar);
        goalProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(started) {
                    steps++;
                    stepTextView.setText(String.valueOf(steps));
                    goalTextView.setText(String.valueOf(steps));
                    stepProgressBar.setProgress(steps);
                    goalProgressBar.setProgress((int)(steps/4.2));
                }
            }
        });
        exerciseStartButton = findViewById(R.id.exerciseStartButton);
        exerciseStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                started = !started;
                if(started)
                    Toast.makeText(context, "Exercise Started", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Exercise Ended", Toast.LENGTH_SHORT).show();
            }
        });
        exerciseActivityLayout = findViewById(R.id.exerciseActivityLayout);
    }

    public void openSettingsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.exercise_popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        exerciseActivityLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        switch(item.getItemId())
        {
            case R.id.exerciseSettingsItem1:
                createInfoDialog();
                return true;
            case R.id.exerciseSettingsItem2:
                startActivity(new Intent(context, AthleteActivity.class));
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
