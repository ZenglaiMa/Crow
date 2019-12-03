package com.happier.crow.parent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("test", "===============================");
        //获得标题
        String messageTitle=intent.getStringExtra("messageTitle");
        //获得内容
        String messageContent=intent.getStringExtra("messageContent");

        //接收到广播以后，启动Alarm activity,在alarm中报警
        Intent in=new Intent();
        in.setClass(context, AlarmBeginAndEnd.class);
        in.putExtra("messageTitle", messageTitle);
        in.putExtra("messageContent", messageContent);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //调用Alarm
        context.startActivity(in);
    }
}
