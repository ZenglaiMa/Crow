package com.happier.crow;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class MyMessageReceiver extends JPushMessageReceiver {
    //用户收到自定义消息时被回调(消息)
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Log.e("test", customMessage.message);
    }

    //处理消息
    //通知消息到达时回调(通知)
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        Log.e("test", "接收到通知, 通知标题：" + notificationMessage.notificationTitle
                + "  通知内容:" + notificationMessage.notificationContent + " 附加字段：" + notificationMessage.notificationExtras);
    }

    //用户点击通知时被回调(通知)
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
    }
}
