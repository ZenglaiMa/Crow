package com.happier.crow.parent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.happier.crow.R;

public class AlarmBeginAndEnd extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_begin_and_end);
        try
        {
            //播放指定的音乐
            mMediaPlayer=MediaPlayer.create(this, R.raw.x2);
            //设置播放的音量
            mMediaPlayer.setVolume(300, 350);
            //设置循环
            mMediaPlayer.setLooping(true);
        }catch (Exception e){
            Toast.makeText(this,"音乐文件播放异常",Toast.LENGTH_SHORT);
        }
        //开始播放
        mMediaPlayer.start();
        Intent intent=getIntent();
        //获得标题
        String messageTitle=intent.getStringExtra("messageTitle");
        //获得内容
        String messageContent=intent.getStringExtra("messageContent");
        //新建对话框
        AlertDialog.Builder adb=new AlertDialog.Builder(this);
        adb.setTitle(messageTitle);
        adb.setMessage(messageContent);
        adb.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //关闭媒体播放器
                mMediaPlayer.stop();
                mMediaPlayer.release();
                finish();
            }
        });
        //显示对话框
        adb.show();
    }
}
