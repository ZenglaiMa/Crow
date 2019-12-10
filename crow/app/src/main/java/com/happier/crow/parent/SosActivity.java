package com.happier.crow.parent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SosActivity extends AppCompatActivity {

    private List<Map<String, Object>> dataSource = new ArrayList<>();
    private String pid;
    public static final String Parent_CONTACT_PATH = "/contact/showSos";
    Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        getSupportActionBar().setElevation(0);

        SharedPreferences sharedPreferences = getSharedPreferences("authid", MODE_PRIVATE);
        pid = String.valueOf(sharedPreferences.getInt("pid", 0));
        // 获取通讯录数据
        GetDataThread thread = new GetDataThread();
        thread.start();
        while (thread.flag == false) {
            Log.e("flag", "Thread is Running");
        }
        for (int i = 0; i < dataSource.size() - 1; i++) {
            smsto(dataSource.get(i).get("phone").toString());
        }
        callPhone(dataSource.get(dataSource.size() - 1).get("phone").toString());
    }

    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:" + phoneNum);
        intent.setData(uri);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    public void smsto(String sms) {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(sms, null, "紧急求救, 紧急求救, 紧急求救!!!", null, null);
    }

    private void initData(String data) {
        try {
            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            dataSource = gson.fromJson(data, type);
            Log.e("dataSource", dataSource.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class GetDataThread extends Thread {
        boolean flag = false;

        public GetDataThread() {

        }

        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL(Constant.BASE_URL + Parent_CONTACT_PATH + "?id=" + pid + "&adderStatus=0");
                URLConnection conn = url.openConnection();
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                String data = reader.readLine();
                if (data != null) {
                    initData(data);
                } else {
                    Log.e("错误", "联系人数据为空");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack();
        }

        private void callBack() {
            flag = true;
        }
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
