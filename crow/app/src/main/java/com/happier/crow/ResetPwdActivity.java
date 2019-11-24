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
import android.widget.Toast;

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

public class ResetPwdActivity extends AppCompatActivity {

    private EditText etPhone;
    private Button btnVerify;
    private EditText etVerify;
    private EditText etResetPwd;
    private EditText etConfirmResetPwd;
    private Button btnReset;

    private String phoneNumber;
    private String password;
    private String verifyCode;
    private String confirmPassword;

    private int who;

    private static final int PARENT_RESET_PASSWORD = 1;
    private static final int CHILDREN_RESET_PASSWORD = 2;

    private static final int RESET_FAILURE = 0;
    private static final int RESET_SUCCESS = 1;

    private static final int VERIFY_SUCCESS = 1;
    private static final int GET_SUCCESS = 2;

    private static String PARENT_RESET_PATH = "/parent/resetPassword";
    private static String CHILDREN_RESET_PATH = "/children/resetPassword";

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
                        reset(who);
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
        setContentView(R.layout.activity_reset_pwd);

        EventBus.getDefault().register(this);

        // 往SMSSDK中注册一个事件接收器
        SMSSDK.registerEventHandler(eh);

        findViews();

        who = getIntent().getIntExtra("who", 0);
        switch (who) {
            case PARENT_RESET_PASSWORD:
                btnReset.setBackgroundColor(getResources().getColor(R.color.colorParent));
                break;
            case CHILDREN_RESET_PASSWORD:
                btnReset.setBackgroundColor(getResources().getColor(R.color.colorChildren));
                break;
        }

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

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = etPhone.getText().toString();
                verifyCode = etVerify.getText().toString();
                password = etResetPwd.getText().toString();
                confirmPassword = etConfirmResetPwd.getText().toString();
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

    private void reset(int who) {
        String path = null;
        if (who == PARENT_RESET_PASSWORD) {
            path = PARENT_RESET_PATH;
        } else if (who == CHILDREN_RESET_PASSWORD) {
            path = CHILDREN_RESET_PATH;
        }
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("phone", phoneNumber)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + path)
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
    public void resetResult(String result) {
        if (Integer.parseInt(result) == RESET_SUCCESS) {
            Toast.makeText(getApplicationContext(), "重置成功, 请重新登陆", Toast.LENGTH_SHORT).show();
            finish();
        } else if (Integer.parseInt(result) == RESET_FAILURE) {
            Toast.makeText(getApplicationContext(), "用户名不存在, 请注册", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendVerifyCode(String phoneNumber) {
        SMSSDK.getVerificationCode("86", phoneNumber);
    }

    private void verify(String verifyCode) {
        SMSSDK.submitVerificationCode("86", phoneNumber, verifyCode);
    }

    private void findViews() {
        etPhone = findViewById(R.id.m_reset_pwd_phone);
        btnVerify = findViewById(R.id.m_btn_reset_pwd_verify);
        etVerify = findViewById(R.id.m_et_reset_pwd_verify);
        etResetPwd = findViewById(R.id.m_et_reset_pwd);
        etConfirmResetPwd = findViewById(R.id.m_et_reset_confirm_pwd);
        btnReset = findViewById(R.id.m_btn_reset);
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
