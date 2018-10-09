package com.oliver.stepnumber.activity;

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
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qqStepNumber = findViewById(R.id.qq_step_number);
        aLiStepNumber = findViewById(R.id.ali_step_number);
        btn = findViewById(R.id.btn_reset);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qqStepNumber.start();
                aLiStepNumber.start();
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
