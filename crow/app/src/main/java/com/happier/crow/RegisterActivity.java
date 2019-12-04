package com.happier.crow;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.util.Const;
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
    private static final int REGIST_SUCCESS = 3;

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
                case REGIST_SUCCESS:
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
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
            addEntity();
//            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
//            finish();
        } else if (Integer.parseInt(result) == REGISTER_FAILURE) {
            Toast.makeText(getApplicationContext(), "服务器错误, 请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void addEntity() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                URL url = null;
                String params = "ak=" + Constant.ak + "&" + "service_id=" + Constant.serviceId + "&"
                        + "entity_name=" + phoneNumber;
                PrintWriter out = null;
                BufferedReader in = null;
                String result = "";
                try {
                    URL realUrl = new URL("http://yingyan.baidu.com/api/v3/entity/add");
                    // 打开和URL之间的连接
                    URLConnection conn = realUrl.openConnection();
                    // 设置通用的请求属性
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("user-agent",
                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8; ");
                    // 发送POST请求必须设置如下两行
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    // 获取URLConnection对象对应的输出流
                    out = new PrintWriter(conn.getOutputStream());
                    // 发送请求参数
                    out.print(params);
                    // flush输出流的缓冲
                    out.flush();
                    // 定义BufferedReader输入流来读取URL的响应
                    in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        result += line;
                    }
                    JSONObject response0 = new JSONObject(result);
                    String message = response0.getString("message");
                    int status = response0.getInt("status");
                    Log.e("message", message + status);
                    Message msg = new Message();
                    msg.what = REGIST_SUCCESS;
                    handler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
