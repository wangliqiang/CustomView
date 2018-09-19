package com.app.customview.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.app.customview.R;
import com.app.customview.widget.ClockHelper;
import com.app.customview.widget.ClockView;

public class ClockActivity extends AppCompatActivity implements View.OnClickListener {

    ClockView clockView;
    ClockHelper clockHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        clockView = findViewById(R.id.clockView);
        clockHelper = new ClockHelper(clockView);
        clockHelper.start();
        clockView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clockView:
                clockHelper.getOff();
                break;
        }
    }
}
