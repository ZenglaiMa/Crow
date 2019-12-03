package com.happier.crow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happier.crow.entities.Alarm;
import com.happier.crow.parent.ParentAlarmActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class MessageReceiver extends cn.jpush.android.service.JPushMessageReceiver {
    //用户收到自定义消息时被回调(消息)
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Log.e("test", "onMessage");
    }

    //处理消息
    //通知消息到达时回调(通知)
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        Log.e("test", "onNotifyMessageArrived");
        List<Alarm> listTemp = new ArrayList<>();
        try {
            JSONObject js = new JSONObject(notificationMessage.notificationExtras);
            String str = js.optString("extras");
            JSONArray ja = new JSONArray(str);
            for (int i=0; i<ja.length(); i++){
                JSONObject jsTemp = ja.getJSONObject(i);
                String strTemp = jsTemp.getString("attrs");
                Alarm alarm = new Gson().fromJson(strTemp, Alarm.class);
                listTemp.add(alarm);
            }
            EventBus.getDefault().post(listTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //用户点击通知时被回调(通知)
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        Log.e("test", "onNotifyMessageOpened");
    }
}
