package com.happier.crow.parent;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.Alarm;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParentAlarmActivity extends AppCompatActivity {
    private ListView lvParentShowAlarm = null;
    private LinearLayout llParentAddAlarm = null;
    public List<Alarm> list = new ArrayList<>();
    private ParentAlarmAdapter adapter;
    private static final String PARENT_SET_INFO_PATH = "/alarm/getAlarmParent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_alarm);

        EventBus.getDefault().register(this);
        initData();
        linsteners();
    }

    private void linsteners() {
        llParentAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentAlarmActivity.this, ParentAddAlarmActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

    }

    private void initData() {
        lvParentShowAlarm = findViewById(R.id.z_lv_parent_show_alarm);
        llParentAddAlarm = findViewById(R.id.z_ll_parent_add_alarm);
        setOrModify();
//        adapter = new ParentAlarmAdapter(list, this, R.layout.parent_alarm_item);
//        lvParentShowAlarm.setAdapter(adapter);
    }

    private void setOrModify() {
        OkHttpClient client = new OkHttpClient();
        int pid = getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0);
        FormBody body = new FormBody.Builder()
                .add("pid", String.valueOf(pid))
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
                EventBus.getDefault().post(result);
//                Log.e("test", result);
//                JSONArray ja = null;
//                try {
//                    ja = new JSONArray(result);
//                    for (int i = 0; i < ja.length(); i++) {
//                        JSONObject jsTemp = ja.getJSONObject(i);
//                        String strTemp = jsTemp.getString("attrs");
//                        Alarm alarm = new Gson().fromJson(strTemp, Alarm.class);
//                        list.add(alarm);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.e("test1", list.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100 && resultCode == 99) {
            Alarm alarm = new Gson().fromJson(data.getStringExtra("newData"), Alarm.class);
            list.add(alarm);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleResult(String result) {
        Log.e("test", result);
        JSONArray ja = null;
        try {
            ja = new JSONArray(result);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jsTemp = ja.getJSONObject(i);
                String strTemp = jsTemp.getString("attrs");
                Alarm alarm = new Gson().fromJson(strTemp, Alarm.class);
                list.add(alarm);
            }
            adapter = new ParentAlarmAdapter(list, this, R.layout.parent_alarm_item);
            lvParentShowAlarm.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("test1", list.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleLoginResult(List<Alarm> listTemp) {
        this.list.add(listTemp.get(listTemp.size() - 1));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
