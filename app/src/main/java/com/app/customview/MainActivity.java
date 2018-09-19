package com.app.customview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.customview.view.ClockActivity;
import com.app.customview.widget.ClockHelper;
import com.app.customview.widget.ClockView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button toClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toClock = findViewById(R.id.toClock);

        toClock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toClock:
                startActivity(new Intent(MainActivity.this, ClockActivity.class));
                break;
        }
    }
}
