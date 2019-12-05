package com.happier.crow.parent;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.Alarm;

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
    private static final String PARENT_SET_INFO_PATH = "/alarm/setAlarmParent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_add_alarm);
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
                if (type.equals("") || description.equals("") || mOra.equals("") || hour.equals("") || minute.equals("")) {
                    Toast.makeText(ParentAddAlarmActivity.this, "信息不全", Toast.LENGTH_LONG).show();
                } else {
                    setOrModify();
                }
            }
        });
    }

    private void setOrModify() {
        OkHttpClient client = new OkHttpClient();
        int pid = getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0);
        FormBody body = new FormBody.Builder()
                .add("pid", String.valueOf(pid))
                .add("type", type)
                .add("description", description)
                .add("mOra", mOra)
                .add("hour", hour)
                .add("minute", minute)
                .build();
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
                if (result.equals("0")) {
                    Looper.prepare();
                    Toast.makeText(ParentAddAlarmActivity.this, "提醒设置失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(ParentAddAlarmActivity.this, "提醒设置成功", Toast.LENGTH_LONG).show();
                    EventBus.getDefault().post(result);
                    Looper.loop();
                }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleResult(String result) {
        Alarm alarm = new Alarm();
        alarm.setType(type);
        alarm.setTime(mOra + "," + hour + "," + minute);
        alarm.setState(1);
        alarm.setDescription(description);
        Intent intent = new Intent(ParentAddAlarmActivity.this, ParentAlarmActivity.class);
        intent.putExtra("newData", new Gson().toJson(alarm));
        setResult(99, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
