package com.happier.crow.parent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.happier.crow.R;
import com.happier.crow.children.ChildrenSetAlarmActivity;

public class ParentAddAlarmActivity extends AppCompatActivity {
    private EditText etType = null;
    private EditText etDescription = null;
    private Spinner spMOra = null;
    private Spinner spHour = null;
    private Spinner spMinute = null;
    private Button btnAlarmSubmit = null;
    private String type = "";
    private String description = "";
    private String mOra = "";
    private String hour = "";
    private String minute = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_add_alarm);
        initData();
        linsteners();
    }
    private void linsteners() {
        spMOra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mOra = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spMinute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minute = parent.getItemAtPosition(position).toString();
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

    private void initData() {
        etType = findViewById(R.id.z_et_alarm_type_parent);
        etDescription = findViewById(R.id.z_et_alarm_description_parent);
        spMOra = findViewById(R.id.z_spinner_mOra_parent);
        spHour = findViewById(R.id.z_spinner_hour_parent);
        spMinute = findViewById(R.id.z_spinner_minute_parent);
        btnAlarmSubmit = findViewById(R.id.z_btn_alarm_submit_parent);
    }
}
