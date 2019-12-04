package com.happier.crow.children;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.parent.ParentSharedPhotoAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChildrenPhotograph extends AppCompatActivity {

    private List<String> paths = new ArrayList<>();
    private ParentSharedPhotoAdapter adapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;

    private static final String SHOW_PHOTO_PATH = "/image/responseChildrenRequest";

    private OkHttpClient client = new OkHttpClient();

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_photograph);

        EventBus.getDefault().register(this);

        recyclerView = findViewById(R.id.m_children_recycler_view);
        smartRefreshLayout = findViewById(R.id.m_c_srl);

        initData();

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
            }
        });
    }

    private void initData() {
        FormBody body = new FormBody.Builder()
                .add("cid", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("cid", 0)))
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + SHOW_PHOTO_PATH)
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
                if (result != null && !result.equals("")) {
                    EventBus.getDefault().post(result);
                } else {
                    Looper.prepare();
                    Toast.makeText(ChildrenPhotograph.this, "服务器错误, 请稍后重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlePhotoPath(String result) {
        paths = gson.fromJson(result, new TypeToken<List<String>>() {}.getType());
        adapter = new ParentSharedPhotoAdapter(this, paths, R.layout.recycler_view_item);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        smartRefreshLayout.finishRefresh();
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
