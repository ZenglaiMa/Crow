package com.happier.crow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.happier.crow.constant.Constant;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.MODE_PRIVATE;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            SharedPreferences sharedPreferences = context.getSharedPreferences("crow", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("regId", regId);
            editor.commit();
            Log.e("test", regId+"ffff");
        }
    }
}
