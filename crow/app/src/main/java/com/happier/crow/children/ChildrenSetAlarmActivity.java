package com.happier.crow.children;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.happier.crow.R;

public class ChildrenSetAlarmActivity extends AppCompatActivity {
    private EditText etType = null;
    private EditText etDescription = null;
    private Spinner spMOra = null;
    private Spinner spHour = null;
    private Spinner spMinute = null;
    private Button btnAlarmSubmit = null;
    private String type;
    private String description;
    private String mOra;
    private String hour;
    private String minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_set_alarm);

        initData();
        linsteners();
    }

    private void linsteners() {
        spMOra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("test", "哈哈" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAlarmSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initData(){
        etType = findViewById(R.id.z_et_alarm_type_children);
        etDescription = findViewById(R.id.z_et_alarm_description_children);
        spMOra = findViewById(R.id.z_spinner_mOra_children);
        spHour = findViewById(R.id.z_spinner_hour_children);
        spMinute = findViewById(R.id.z_spinner_minute_children);
        btnAlarmSubmit = findViewById(R.id.z_btn_alarm_submit_children);
    }

}
