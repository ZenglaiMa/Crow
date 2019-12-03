package com.happier.crow.parent;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;
import com.baidu.platform.comapi.walknavi.widget.ArCameraView;

public class NavigationActivity extends Activity {

    private WalkNavigateHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = WalkNavigateHelper.getInstance();
        View view = helper.onCreate(this);
        if (view != null) {
            setContentView(view);
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ArCameraView.WALK_AR_PERMISSION);

        helper.setWalkNaviStatusListener(new IWNaviStatusListener() {
            @Override
            public void onWalkNaviModeChange(int i, WalkNaviModeSwitchListener walkNaviModeSwitchListener) {
                helper.switchWalkNaviMode(NavigationActivity.this, i, walkNaviModeSwitchListener);
            }

            @Override
            public void onNaviExit() {
            }
        });

        helper.startWalkNavi(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ArCameraView.WALK_AR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                helper.startCameraAndSetMapView(this);
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "需要开启相机使用权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.quit();
    }

}
