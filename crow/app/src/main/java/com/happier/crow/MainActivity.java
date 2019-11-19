package com.happier.crow;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

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

    private static final int PARENT_LOGIN_STATE = 1;
    private static final int CHILDREN_LOGIN_STATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

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
                        login(PARENT_LOGIN_STATE);
                    } else if (rbChildren.isChecked()) {
                        login(CHILDREN_LOGIN_STATE);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "请输入手机号或密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void login(int loginState) {
        switch (loginState) {
            case PARENT_LOGIN_STATE:
                // todo : 网络操作 服务器端执行父母登录逻辑
                break;
            case CHILDREN_LOGIN_STATE:
                // todo : 网络操作 服务器端执行子女登录逻辑
                break;
        }
    }

    private class LoginTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
}
