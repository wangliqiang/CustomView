package com.app.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.customview.widget.ClockHelper;
import com.app.customview.widget.ClockView;

public class MainActivity extends AppCompatActivity {

    ClockView clockView;
    ClockHelper clockHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clockView = findViewById(R.id.clockView);
        clockHelper = new ClockHelper(clockView);
        clockHelper.start();
    }
}
