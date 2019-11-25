package com.happier.crow.parent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.happier.crow.R;

public class ParentIndexActivity extends AppCompatActivity {

    private ImageView ivChat;
    private ImageView ivAlarm;
    private ImageView ivContact;
    private ImageView ivPhoto;
    private ImageView ivHome;
    private ImageView ivSos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_index);

        findViews();
        setOnClickListener();

        /*
        SharedPreferences preferences = getSharedPreferences("authid", MODE_PRIVATE);
        int pid = preferences.getInt("pid", 0);
        */
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
                    // todo : 跳转到定时提醒界面
                    Toast.makeText(getApplicationContext(), "Jump to alarm", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_iv_contact:
                    // todo : 跳转到通讯录界面
                    Toast.makeText(getApplicationContext(), "Jump to contact", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_iv_photo:
                    // todo : 跳转到亲情相册界面
                    Toast.makeText(getApplicationContext(), "Jump to photo", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_iv_home:
                    // todo : 跳转到回家地图界面
                    Toast.makeText(getApplicationContext(), "Jump to home map", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_iv_sos:
                    // todo : 跳转到紧急呼叫界面
                    Toast.makeText(getApplicationContext(), "Jump to sos", Toast.LENGTH_SHORT).show();
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
}
