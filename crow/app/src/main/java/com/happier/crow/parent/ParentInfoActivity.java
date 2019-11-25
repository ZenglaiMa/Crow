package com.happier.crow.parent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.happier.crow.MainActivity;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.Parent;

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

public class ParentInfoActivity extends AppCompatActivity {

    private ImageView ivHeader;
    private TextView tvParentName;
    private TextView tvParentGender;
    private TextView tvParentAge;
    private TextView tvParentAddress;
    private Button btnModifyParentInfo;
    private Button btnParentLogout;

    private static final String PARENT_PERSONAL_INFO_PATH = "/parent/getInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_info);

        EventBus.getDefault().register(this);

        findViews();

        getInfo();

        // 头像
        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo : upload head image
            }
        });

        // 设置/修改个人信息
        btnModifyParentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ModifyParentInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        // 退出登录
        btnParentLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfo();
    }

    private void getInfo() {
        OkHttpClient client = new OkHttpClient();
        int pid = getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0);
        FormBody body = new FormBody.Builder()
                .add("pid", String.valueOf(pid))
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + PARENT_PERSONAL_INFO_PATH)
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
        Gson gson = new Gson();
        Parent parent = gson.fromJson(result, Parent.class);
        tvParentName.setText(parent.getName());
        tvParentGender.setText(parent.getGender() == 1 ? "男" : "女");
        tvParentAge.setText(String.valueOf(parent.getAge()));
        String address = parent.getProvince() + parent.getCity() + parent.getArea() + parent.getDetailAddress();
        tvParentAddress.setText(address);
    }

    private void findViews() {
        ivHeader = findViewById(R.id.m_iv_parent_header);
        tvParentName = findViewById(R.id.m_tv_parent_name);
        tvParentGender = findViewById(R.id.m_tv_parent_gender);
        tvParentAddress = findViewById(R.id.m_tv_parent_address);
        tvParentAge = findViewById(R.id.m_tv_parent_age);
        btnModifyParentInfo = findViewById(R.id.m_btn_modify_parent_info);
        btnParentLogout = findViewById(R.id.m_btn_parent_logout);
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
