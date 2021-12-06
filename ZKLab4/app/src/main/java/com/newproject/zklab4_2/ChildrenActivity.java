package com.newproject.zklab4_2;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChildrenActivity extends AppCompatActivity {
    TextView childTextView;
    ProgressBar childProgressBar;
    int steps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        childTextView = findViewById(R.id.childTextView);
        steps = 0;
        childTextView.setText("0");
        childProgressBar = findViewById(R.id.childProgressBar);
        childProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                steps++;
                childTextView.setText(String.valueOf(steps));
                childProgressBar.setProgress((int)(steps/10));
            }
        });
    }
}
