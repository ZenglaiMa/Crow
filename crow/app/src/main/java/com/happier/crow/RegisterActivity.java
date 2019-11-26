package com.happier.crow;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.happier.crow.constant.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    private String phoneNumber;
    private String password;
    private String verifyCode;
    private String confirmPassword;

    private static final int REGISTER_FAILURE = 0;
    private static final int REGISTER_SUCCESS = 1;

    private static final int VERIFY_SUCCESS = 1;
    private static final int GET_SUCCESS = 2;

    private static String PARENT_REGISTER_PATH = "/parent/register";
    private static String CHILDREN_REGISTER_PATH = "/children/register";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case VERIFY_SUCCESS:
                    HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
                    String country = (String) map.get("country");
                    String phone = (String) map.get("phone");
                    if (country.equals("86") && phone.equals(phoneNumber)) {
                        if (rbParent.isChecked()) {
                            register(PARENT_REGISTER_PATH);
                        } else if (rbChildren.isChecked()) {
                            register(CHILDREN_REGISTER_PATH);
                        }
                    }
                    break;
                case GET_SUCCESS:
                    boolean state = (boolean) msg.obj;
                    if (!state) {
                        Toast.makeText(getApplicationContext(), "验证码发送成功", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    /**
     * 短信验证的回调监听 afterEvent 方法执行在子线程中, 因此不能在此直接更新主UI界面.
     */
    private EventHandler eh = new EventHandler() {
        @Override
        public void afterEvent(int i, int i1, Object o) {
            if (i1 == SMSSDK.RESULT_COMPLETE) { // 回调完成
                if (i == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { // 提交验证码成功
                    Message msg = new Message();
                    msg.what = VERIFY_SUCCESS;
                    msg.obj = o;
                    handler.sendMessage(msg);
                } else if (i == SMSSDK.EVENT_GET_VERIFICATION_CODE) { // 当手机收到短信验证码时执行该分支
                    Message msg = new Message();
                    msg.what = GET_SUCCESS;
                    msg.obj = o;
                    handler.sendMessage(msg);
                }
            } else {
                ((Throwable) o).printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EventBus.getDefault().register(this);

        // 往SMSSDK中注册一个事件接收器
        SMSSDK.registerEventHandler(eh);

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

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = etPhone.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    sendVerifyCode(phoneNumber);
                } else {
                    Toast.makeText(getApplicationContext(), "请输入手机号码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = etPhone.getText().toString();
                verifyCode = etVerify.getText().toString();
                password = etPassword.getText().toString();
                confirmPassword = etConfirmPwd.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    if (!TextUtils.isEmpty(verifyCode)) {
                        if (!TextUtils.isEmpty(password)) {
                            if (!TextUtils.isEmpty(confirmPassword)) {
                                if (confirmPassword.equals(password)) {
                                    verify(verifyCode);
                                } else {
                                    Toast.makeText(getApplicationContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "请确认密码", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请输入手机号码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register(String registerPath) {
        OkHttpClient client = new OkHttpClient();
        FormBody body;
        if (registerPath.equals(PARENT_REGISTER_PATH)) {
            body = new FormBody.Builder()
                    .add("phone", phoneNumber)
                    .add("password", password)
                    .add("registerId", getSharedPreferences("crow", MODE_PRIVATE).getString("regId", ""))
                    .build();
        } else {
            body = new FormBody.Builder()
                    .add("phone", phoneNumber)
                    .add("password", password)
                    .build();
        }
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + registerPath)
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
    public void registerResult(String result) {
        if (Integer.parseInt(result) == REGISTER_SUCCESS) {
            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
            finish();
        } else if (Integer.parseInt(result) == REGISTER_FAILURE) {
            Toast.makeText(getApplicationContext(), "服务器错误, 请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 验证输入的验证码是否正确
     */
    private void verify(String verifyCode) {
        SMSSDK.submitVerificationCode("86", phoneNumber, verifyCode);
    }

    /**
     * 发送手机验证码
     */
    private void sendVerifyCode(String phoneNumber) {
        SMSSDK.getVerificationCode("86", phoneNumber);
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        SMSSDK.unregisterEventHandler(eh);
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
