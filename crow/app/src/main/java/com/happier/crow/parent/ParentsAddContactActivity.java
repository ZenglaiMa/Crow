package com.happier.crow.parent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class ParentsAddContactActivity extends AppCompatActivity {
    private String remark;
    private String phone;
    private String pid;
    int isIce = 0;
    public static final String ADDCONTACT_PATH = "/contact/addContact";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_addcontact);

        SharedPreferences sharedPreferences = getSharedPreferences("authid",MODE_PRIVATE);
        pid = String.valueOf(sharedPreferences.getInt("pid",0));

        Button btn = findViewById(R.id.y_btn_saveContact);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etName = findViewById(R.id.y_et_addNickName);
                EditText etPhone = findViewById(R.id.y_et_teleNum);
                CheckBox checkBox = findViewById(R.id.y_cb_ice);
                if(checkBox.isChecked()){
                    isIce = 1;
                }
                remark = etName.getText().toString();
                phone = etPhone.getText().toString();
                saveContactInfo(pid,remark,phone,isIce);
            }
        });


    }

    private void saveContactInfo(String id,String remark,String phone,int isIce) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("adderStatus","0")
                .add("adderId",id)
                .add("remark",remark)
                .add("phone",phone)
                .add("isIce",String.valueOf(isIce))
                .build();
        final Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL+ADDCONTACT_PATH)
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
                Log.e("myTag",tag);
                    if (tag.equals("1")){
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ParentsAddContactActivity.this,ParentsContactActivity.class);
                        startActivity(intent);
                        finish();
                        Looper.loop();
                    }else if(tag.equals("0")){
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"添加失败,用户可能未注册",Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
            }
        });
    }
}
