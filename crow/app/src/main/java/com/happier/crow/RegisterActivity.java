package com.happier.crow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivHeader;
    private RadioGroup rgStatus;
    private RadioButton rbParent;
    private RadioButton rbChildren;
    private EditText etPhone;
    private Button btnVerify;
    private EditText etVerify;
    private EditText etPassword;
    private EditText etConfirmPwd;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();

        rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.m_register_rb_children:
                        changeStyleToChildren();
                        break;
                    case R.id.m_register_rb_parent:
                        changeStyleToParent();
                        break;
                }
            }
        });
    }

    private void changeStyleToParent() {
        Glide.with(this).load(R.drawable.login_parent).into(ivHeader);
        btnRegister.setBackgroundColor(getResources().getColor(R.color.colorParent));
    }

    private void changeStyleToChildren() {
        Glide.with(this).load(R.drawable.login_children).into(ivHeader);
        btnRegister.setBackgroundColor(getResources().getColor(R.color.colorChildren));
    }

    private void findViews() {
        ivHeader = findViewById(R.id.m_iv_register_header);
        rgStatus = findViewById(R.id.m_register_rg_status);
        rbParent = findViewById(R.id.m_register_rb_parent);
        rbChildren = findViewById(R.id.m_register_rb_children);
        etPhone = findViewById(R.id.m_register_phone);
        btnVerify = findViewById(R.id.m_btn_register_verify);
        etVerify = findViewById(R.id.m_et_register_verify);
        etPassword = findViewById(R.id.m_et_register_pwd);
        etConfirmPwd = findViewById(R.id.m_et_register_confirm_pwd);
        btnRegister = findViewById(R.id.m_btn_register);
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
