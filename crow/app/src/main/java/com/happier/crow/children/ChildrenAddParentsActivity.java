package com.happier.crow.children;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.ContactParent;

import org.greenrobot.eventbus.EventBus;

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

public class ChildrenAddParentsActivity extends AppCompatActivity {
    private int cid;
    private EditText etFatherPhone;
    private EditText etMotherPhone;
    private ImageView ivSetFather;
    private ImageView ivSetMother;
    private int adderStatus = 1;
    private LinearLayout linearLayoutMain;

    private List<Map<String, Object>> dataSource = new ArrayList<>();

    private Gson gson = new Gson();

    public static final String SHOW_CONTACT_PATH = "/contact/showContacts";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("authid", MODE_PRIVATE);
        cid = sharedPreferences.getInt("cid", 0);
        setContentView(R.layout.activity_children_addparents);

        etFatherPhone = findViewById(R.id.y_et_fatherPhone);
        etMotherPhone = findViewById(R.id.y_et_motherPhone);
        ivSetFather = findViewById(R.id.y_iv_setFather);
        ivSetMother = findViewById(R.id.y_iv_setMother);
        linearLayoutMain = findViewById(R.id.layout_main);
        showParents();


        ivSetFather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFatherPhone.setFocusable(true);
                etFatherPhone.setFocusableInTouchMode(true);
                etFatherPhone.requestFocus();
                ChildrenAddParentsActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        ivSetMother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMotherPhone.setFocusable(true);
                etMotherPhone.setFocusableInTouchMode(true);
                etMotherPhone.requestFocus();
                ChildrenAddParentsActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        linearLayoutMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                linearLayoutMain.setFocusable(true);
                linearLayoutMain.setFocusableInTouchMode(true);
                linearLayoutMain.requestFocus();
                return false;
            }
        });

        etFatherPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //监听父亲栏焦点
                if (!hasFocus) {
                    String phone = etFatherPhone.getText().toString();
                    String remark = "父亲";
                    int id = cid;
                    ContactParent contactParent = new ContactParent();
                    contactParent.setId(id);
                    contactParent.setPhone(phone);
                    contactParent.setRemark(remark);
                    String data = gson.toJson(contactParent, ContactParent.class);
                    EventBus.getDefault().postSticky(data);
                    CustomDialog dialog = new CustomDialog();
                    dialog.setCancelable(false);
                    dialog.show(getSupportFragmentManager(), "myDialog");
                }
            }
        });

        etMotherPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String phone = etMotherPhone.getText().toString();
                    String remark = "母亲";
                    int id = cid;
                    ContactParent contactParent = new ContactParent();
                    contactParent.setId(id);
                    contactParent.setPhone(phone);
                    contactParent.setRemark(remark);
                    String data = gson.toJson(contactParent, ContactParent.class);
                    EventBus.getDefault().postSticky(data);
                    CustomDialog dialog = new CustomDialog();
                    dialog.setCancelable(false);
                    dialog.show(getSupportFragmentManager(), "myDialog");
                }
            }
        });
    }

    private void showParents() {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(cid))
                .add("adderStatus", String.valueOf(adderStatus))
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + SHOW_CONTACT_PATH)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (data != null) {
                    initData(data);
                }
            }
        });
    }

    private void initData(String data) {
        dataSource = gson.fromJson(data, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        String fPhone = "";
        String mPhone = "";
        for (int i = 0; i < dataSource.size(); i++) {
            if (dataSource.get(i).get("remark").toString().equals("父亲")) {
                fPhone = dataSource.get(i).get("phone").toString();
                etFatherPhone.setText(fPhone);
            }
            if (dataSource.get(i).get("remark").toString().equals("母亲")) {
                mPhone = dataSource.get(i).get("phone").toString();
                etMotherPhone.setText(mPhone);
            }
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