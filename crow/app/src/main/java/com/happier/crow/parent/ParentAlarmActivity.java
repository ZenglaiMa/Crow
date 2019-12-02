package com.happier.crow.parent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.happier.crow.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParentAlarmActivity extends AppCompatActivity {
    private ImageView ivParentAlertBack = null;
    private TextView tvParentAlertBack = null;
    private ListView lvParentShowAlarm = null;
    private LinearLayout llParentAddAlarm = null;
    public List<Map<String, String>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_alarm);
        initData();
        linsteners();
    }

    private void linsteners() {
        ivParentAlertBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvParentAlertBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llParentAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentAlarmActivity.this, ParentAlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData(){
        ivParentAlertBack = findViewById(R.id.z_iv_parent_alert_back);
        tvParentAlertBack = findViewById(R.id.z_tv_parent_alert_back);
        lvParentShowAlarm = findViewById(R.id.z_lv_parent_show_alarm);
        llParentAddAlarm = findViewById(R.id.z_ll_parent_add_alarm);
    }

}
