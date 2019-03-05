package com.oliver.stepnumber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.oliver.stepnumber.R;
import com.oliver.stepnumber.view.ALiStepNumber;
import com.oliver.stepnumber.view.QQStepNumber;

public class MainActivity extends AppCompatActivity {
    private QQStepNumber qqStepNumber;
    private ALiStepNumber aLiStepNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qqStepNumber = findViewById(R.id.qq_step_number);
        aLiStepNumber = findViewById(R.id.ali_step_number);
        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qqStepNumber.start();
                aLiStepNumber.start();
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qqStepNumber.cancelAnimator();
        aLiStepNumber.cancelAnimator();
    }
}
