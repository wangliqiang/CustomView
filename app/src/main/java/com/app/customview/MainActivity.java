package com.app.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.customview.bean.PieData;
import com.app.customview.widget.ClockHelper;
import com.app.customview.view.ClockView;
import com.app.customview.view.PieView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ClockView clockView;
    ClockHelper clockHelper;

    Button toClock;
    PieView pieView;
    ArrayList<PieData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockView = findViewById(R.id.clockView);
        clockHelper = new ClockHelper(clockView);
        clockHelper.start();
        clockView.setOnClickListener(this);

        pieView = findViewById(R.id.pieView);

        list.add(new PieData("1", 55));
        list.add(new PieData("2", 25));
        list.add(new PieData("3", 15));
        list.add(new PieData("4", 5));

        pieView.setData(list);
        pieView.setStartAngle(270);
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
