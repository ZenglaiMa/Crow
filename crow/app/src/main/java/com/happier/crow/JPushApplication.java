package com.happier.crow;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.hyphenate.easeui.EaseUI;

import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class JPushApplication extends Application {
    public static String registrationId;

    private List<Activity> mList = new LinkedList<Activity>();
    public static JPushApplication instance;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        registrationId = JPushInterface.getRegistrationID(this);

        EaseUI.getInstance().init(this, null);
    }

    public JPushApplication() {
    }

    public static String getRegistrationId() {
        return registrationId;
    }

    public static void setRegistrationId(String registrationId) {
        JPushApplication.registrationId = registrationId;
    }
}
