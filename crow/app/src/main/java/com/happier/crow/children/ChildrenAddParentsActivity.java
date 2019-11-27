package com.happier.crow.children;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.happier.crow.R;
import com.happier.crow.constant.Constant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChildrenAddParentsActivity extends AppCompatActivity {
    private int cid;
    private String remark;
    private String phone;
    private Button btnSave;
    private EditText etFatherPhone;
    private EditText etMotherPhone;
    private int adderStatus = 1;
    private int isIce = 0;//子女添加联系人时，紧急联系人值为0
    public static final String ADDCONTACT_PATH = "/contact/addContact";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_children_addparents);
        SharedPreferences sharedPreferences = getSharedPreferences("authid", MODE_PRIVATE);
        cid = sharedPreferences.getInt("cid", 0);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 1; i++) {
                    if (i == 0) {
                        remark = "父亲";
                        phone = etFatherPhone.getText().toString();
                        addContact(adderStatus, phone, remark);
                    } else if (i == 1) {
                        remark = "母亲";
                        phone = etMotherPhone.getText().toString();
                        addContact(adderStatus, phone, remark);
                    }
                }

            }
        });

    }

    private void addContact(int adderStatus, String phone, String remark) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("adderId", String.valueOf(cid))
                .add("adderStatus", String.valueOf(adderStatus))
                .add("phone", phone)
                .add("remark", remark)
                .add("isIce", String.valueOf(isIce))
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + ADDCONTACT_PATH)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String tag = response.body().string();
                if (tag.equals("101")) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "添加的父亲或母亲尚未注册，请先注册！", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });
    }
}
