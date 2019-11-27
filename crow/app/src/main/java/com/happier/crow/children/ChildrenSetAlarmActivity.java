package com.happier.crow.children;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.happier.crow.R;
import com.happier.crow.constant.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChildrenSetAlarmActivity extends AppCompatActivity {
    private EditText etType = null;
    private EditText etDescription = null;
    private Spinner spMOra = null;
    private Spinner spHour = null;
    private Spinner spMinute = null;
    private Button btnAlarmSubmit = null;
    private RadioGroup rgAlarmPerson = null;
    private String type = "";
    private String description = "";
    private String mOra = "";
    private String hour = "";
    private String minute = "";
    private String remark = "";
    private static final String PARENT_SET_INFO_PATH = "/alarm/setAlarm";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_set_alarm);
        EventBus.getDefault().register(this);
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
                type = etType.getText().toString();
                description = etDescription.getText().toString();
                switch (rgAlarmPerson.getCheckedRadioButtonId()){
                    case R.id.z_rb_alarm_father:
                        remark = "父亲";
                        break;
                    case R.id.z_rb_alarm_mother:
                        remark = "母亲";
                        break;
                }
                if(type.equals("") || description.equals("") || mOra.equals("") || hour.equals("") || minute.equals("") || remark.equals("")){
                    Toast.makeText(ChildrenSetAlarmActivity.this, "提醒信息不全", Toast.LENGTH_LONG).show();
                }else {
                    setOrModify();
                }
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
        rgAlarmPerson = findViewById(R.id.z_rg_alarm_person);
    }

    private void setOrModify() {
        OkHttpClient client = new OkHttpClient();
        int cid = getSharedPreferences("authid", MODE_PRIVATE).getInt("cid", 0);
        FormBody body = new FormBody.Builder()
                .add("cid", String.valueOf(cid))
                .add("type", type)
                .add("description", description)
                .add("remark", remark)
                .add("mOra", mOra)
                .add("hour", hour)
                .add("minute", minute)
                .build();
        Log.e("test2", remark);
        final Request request = new Request.Builder()
                .url(Constant.BASE_URL + PARENT_SET_INFO_PATH)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if(result.equals("0")){
                }else {
                    EventBus.getDefault().post("");
                }
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleResult(String result) {
        Toast.makeText(ChildrenSetAlarmActivity.this, "提醒设置成功", Toast.LENGTH_LONG).show();
        finish();
    }
}
