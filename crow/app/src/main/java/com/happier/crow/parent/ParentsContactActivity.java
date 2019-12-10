package com.happier.crow.parent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParentsContactActivity extends AppCompatActivity {
    private List<Map<String, Object>> dataSource = new ArrayList<>();
    private ContactAdapter adapter;
    private ListView listView;

    private String pid;
    public static final String PARENT_CONTACT_PATH = "/contact/showContacts";

    private Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_contact);
        getSupportActionBar().setElevation(0);

        EventBus.getDefault().register(this);

        SharedPreferences sharedPreferences = getSharedPreferences("authid", MODE_PRIVATE);
        pid = String.valueOf(sharedPreferences.getInt("pid", 0));

        listView = findViewById(R.id.y_lv_contact);

        getInfo();

        ImageView ivToAdd = findViewById(R.id.y_iv_toAddContact);
        ivToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentsContactActivity.this, ParentsAddContactActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getInfo();
    }

    private void getInfo() {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("id", pid)
                .add("adderStatus", "0")
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + PARENT_CONTACT_PATH)
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
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleResult(String result) {
        dataSource = gson.fromJson(result, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        adapter = new ContactAdapter(this, dataSource, R.layout.listview_contacts);
        listView.setAdapter(adapter);
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
