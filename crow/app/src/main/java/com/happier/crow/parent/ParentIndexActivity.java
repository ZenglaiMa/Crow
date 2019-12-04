package com.happier.crow.parent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;

public class ParentIndexActivity extends AppCompatActivity {

    private ImageView ivChat;
    private ImageView ivAlarm;
    private ImageView ivContact;
    private ImageView ivPhoto;
    private ImageView ivHome;
    private ImageView ivSos;
    private Intent intent;

    private Trace mTrace;
    private LBSTraceClient mTraceClient;
    private OnTraceListener mTraceListener;
    //设备标识
    private String entityName;
    // 定位周期(单位:秒)
    private int gatherInterval = 5;
    // 打包回传周期(单位:秒)
    private int packInterval = 10;
    // 是否存储图像
    private boolean isNeedObjectStorage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_index);
        findViews();
        init();
        setOnClickListener();
        beginService();

    }

    private void init(){
        //获取设备标识
        entityName=getIntent().getStringExtra("entityName");
        Log.e("entityName",entityName+"1");
        mTrace = new Trace(Constant.serviceId, entityName, isNeedObjectStorage);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(getApplicationContext());
        mTraceClient.setLocationMode(LocationMode.High_Accuracy);
        //设置回传周期
        mTraceClient.setInterval(gatherInterval, packInterval);
        //初始化监听器
        mTraceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {
                Log.e("bind", "已绑定");
            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                Log.e("start", "已启动" + i);
                if (i == 0) {
                    // 开启采集
                    mTraceClient.startGather(mTraceListener);
                }
            }

            @Override
            public void onStopTraceCallback(int i, String s) {

            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                Log.e("gather", "开始采集；");
            }

            @Override
            public void onStopGatherCallback(int i, String s) {

            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {

            }

            @Override
            public void onInitBOSCallback(int i, String s) {

            }
        };
    }
    private void setOnClickListener() {
        MyListener listener = new MyListener();
        ivChat.setOnClickListener(listener);
        ivAlarm.setOnClickListener(listener);
        ivContact.setOnClickListener(listener);
        ivPhoto.setOnClickListener(listener);
        ivHome.setOnClickListener(listener);
        ivSos.setOnClickListener(listener);
    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.m_iv_chat:
                    // todo : 跳转到亲情互动界面
                    Toast.makeText(getApplicationContext(), "Jump to family chat", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_iv_alarm:
                    intent = new Intent(getApplicationContext(), ParentAlarmActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.m_iv_contact:
                    intent = new Intent(ParentIndexActivity.this, ParentsContactActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.m_iv_photo:
                    intent = new Intent(ParentIndexActivity.this, ParentPhotograph.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.m_iv_home:
                    intent = new Intent(ParentIndexActivity.this, LocationActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.m_iv_sos:
                    intent = new Intent(ParentIndexActivity.this, SosActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
            }
        }
    }

    private void findViews() {
        ivChat = findViewById(R.id.m_iv_chat);
        ivAlarm = findViewById(R.id.m_iv_alarm);
        ivContact = findViewById(R.id.m_iv_contact);
        ivPhoto = findViewById(R.id.m_iv_photo);
        ivHome = findViewById(R.id.m_iv_home);
        ivSos = findViewById(R.id.m_iv_sos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parent_personal_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_parent_center:
                Intent intent = new Intent(this, ParentInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void beginService() {
        //开启轨迹服务
        mTraceClient.startTrace(mTrace, mTraceListener);
    }
    public void onBackPressed() {//重写的Activity返回
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }

}
