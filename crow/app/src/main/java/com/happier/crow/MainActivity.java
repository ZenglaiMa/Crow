package com.happier.crow;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.happier.crow.children.ChildrenIndexActivity;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.Children;
import com.happier.crow.entities.Parent;
import com.happier.crow.parent.ParentIndexActivity;

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

public class MainActivity extends AppCompatActivity {

    private Button location;
    private RadioGroup rg;
    private RadioButton rbParent;
    private RadioButton rbChildren;
    private ImageView ivLoginHeader;
    private Button btnLogin;
    private EditText etPhone;
    private EditText etPassword;
    private TextView tvForgetPassword;
    private TextView tvRegister;
    private String phoneNumber;
    private String password;

    private static final String PARENT_LOGIN_PATH = "/parent/login";
    private static final String CHILDREN_LOGIN_PATH = "/children/login";

    private static final int PARENT_LOGIN_STATE = 1;
    private static final int CHILDREN_LOGIN_STATE = 2;

    private static final int LOGIN_RESULT_ERROR = -1;
    private static final int LOGIN_RESULT_FAILURE = 0;
    private static final int LOGIN_PARENT_RESULT_SUCCESS = 1;
    private static final int LOGIN_CHILDREN_RESULT_SUCCESS = 2;

    private static final int PARENT_RESET_PASSWORD = 1;
    private static final int CHILDREN_RESET_PASSWORD = 2;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        findViews();

        // autoLogin();

        location = findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ParentsLocation.class);
                startActivity(intent);
            }
        });

        // 根据登陆者身份切换样式
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.m_rb_parent:
                        changeStyleToParent();
                        break;
                    case R.id.m_rb_children:
                        changeStyleToChildren();
                        break;
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = etPhone.getText().toString();
                password = etPassword.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(password)) {
                    if (rbParent.isChecked()) {
                        login(PARENT_LOGIN_PATH, PARENT_LOGIN_STATE);
                    } else if (rbChildren.isChecked()) {
                        login(CHILDREN_LOGIN_PATH, CHILDREN_LOGIN_STATE);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "请输入手机号和密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResetPwdActivity.class);
                if (rbParent.isChecked()) {
                    intent.putExtra("who", PARENT_RESET_PASSWORD);
                } else if (rbChildren.isChecked()) {
                    intent.putExtra("who", CHILDREN_RESET_PASSWORD);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

    }

    /*
    private void autoLogin() {
        int pid = getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0);
        String pPhone = getSharedPreferences("authid", MODE_PRIVATE).getString("p_phone", "");
        String pPassword = getSharedPreferences("authid", MODE_PRIVATE).getString("p_password", "");

        int cid = getSharedPreferences("authid", MODE_PRIVATE).getInt("cid", 0);
        String cPhone = getSharedPreferences("authid", MODE_PRIVATE).getString("c_phone", "");
        String cPassword = getSharedPreferences("authid", MODE_PRIVATE).getString("c_password", "");

        if (pid != 0) {
            rbParent.setChecked(true);
            if (pPhone != null & !pPhone.equals("")) {
                if (pPassword != null && !pPassword.equals("")) {
                    phoneNumber = pPhone;
                    password = pPassword;
                    btnLogin.performClick();
                }
            }
        }

        if (cid != 0) {
            rbChildren.setChecked(true);
            if (cPhone != null & !cPhone.equals("")) {
                if (cPassword != null && !cPassword.equals("")) {
                    phoneNumber = cPhone;
                    password = cPassword;
                    btnLogin.performClick();
                }
            }
        }
    }
    */

    private void login(String loginPath, final int loginState) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("phone", phoneNumber)
                .add("password", password)
                .build();
        final Request request = new Request.Builder()
                .url(Constant.BASE_URL + loginPath)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                EventBus.getDefault().post(LOGIN_RESULT_ERROR);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if (result != null && !result.equals("")) {
                    // 登陆成功之后 服务器端将当前用户信息返回给客户端 将用户id存放到SharedPreferences中 以便后续使用
                    SharedPreferences preferences = getSharedPreferences("authid", MODE_PRIVATE);
                    switch (loginState) {
                        case PARENT_LOGIN_STATE:
                            Parent parent = gson.fromJson(result, Parent.class);
                            SharedPreferences.Editor pEditor = preferences.edit();
                            /*
                            pEditor.putString("p_phone", parent.getPhone());
                            pEditor.putString("p_password", password);
                            */
                            pEditor.putInt("pid", parent.getPid());
                            pEditor.commit();
                            EventBus.getDefault().post(LOGIN_PARENT_RESULT_SUCCESS);
                            break;
                        case CHILDREN_LOGIN_STATE:
                            Children children = gson.fromJson(result, Children.class);
                            SharedPreferences.Editor cEditor = preferences.edit();
                            /*
                            cEditor.putString("c_phone", children.getPhone());
                            cEditor.putString("c_password", password);
                            */
                            cEditor.putInt("cid", children.getCid());
                            cEditor.commit();
                            EventBus.getDefault().post(LOGIN_CHILDREN_RESULT_SUCCESS);
                            break;
                    }
                } else {
                    EventBus.getDefault().post(LOGIN_RESULT_FAILURE);
                }
            }
        });
    }

    // 坑: @Subscribe注解的订阅者接收参数类型必须是引用类型!
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleLoginResult(Integer loginResult) {
        switch (loginResult) {
            case LOGIN_RESULT_ERROR:
                Toast.makeText(this, "服务器错误, 请稍后重试", Toast.LENGTH_SHORT).show();
                break;
            case LOGIN_RESULT_FAILURE:
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                break;
            case LOGIN_PARENT_RESULT_SUCCESS:
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                etPhone.setText("");
                etPassword.setText("");
                Intent pIntent = new Intent(this, ParentIndexActivity.class);
                startActivity(pIntent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
                break;
            case LOGIN_CHILDREN_RESULT_SUCCESS:
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                etPhone.setText("");
                etPassword.setText("");
                Intent cIntent = new Intent(this, ChildrenIndexActivity.class);
                startActivity(cIntent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
                break;
        }
    }

    private void changeStyleToChildren() {
        Glide.with(this).load(R.drawable.login_children).into(ivLoginHeader);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorChildren));
    }

    private void changeStyleToParent() {
        Glide.with(this).load(R.drawable.login_parent).into(ivLoginHeader);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorParent));
    }

    private void findViews() {
        rg = findViewById(R.id.m_rg_status);
        ivLoginHeader = findViewById(R.id.m_iv_login_header);
        btnLogin = findViewById(R.id.m_btn_login);
        rbParent = findViewById(R.id.m_rb_parent);
        rbChildren = findViewById(R.id.m_rb_children);
        etPhone = findViewById(R.id.m_et_phone);
        etPassword = findViewById(R.id.m_et_password);
        tvForgetPassword = findViewById(R.id.m_tv_forget_password);
        tvRegister = findViewById(R.id.m_tv_register);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
