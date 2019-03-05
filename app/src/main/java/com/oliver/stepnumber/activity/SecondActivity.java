package com.oliver.stepnumber.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.oliver.stepnumber.R;
import com.oliver.stepnumber.view.SpeedOfProgressView;

public class SecondActivity extends AppCompatActivity {

    private SpeedOfProgressView progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        progressView = findViewById(R.id.speed_of_progress);
        findViewById(R.id.btn_start_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.setCurrent(90);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressView != null) {
            progressView.cancelAnimator();
        }
    }
}
