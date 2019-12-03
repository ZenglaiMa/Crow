package com.happier.crow.parent;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.google.gson.Gson;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.BaiduAPIResponse;
import com.happier.crow.entities.Parent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationActivity extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private LocationClientOption locationClientOption;

    private LatLng endPoint;

    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    private static final int REQUEST_LOCATION = 1;
    private static final String GET_PARENT_INFO_PATH = "/parent/getInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);

        EventBus.getDefault().register(this);

        mapView = findViewById(R.id.m_map_view);
        baiduMap = mapView.getMap();

        hideBaiduLogo();
        setDefaultScale();

        requestHomeAddress();

    }

    private void requestHomeAddress() {
        FormBody body = new FormBody.Builder()
                .add("pid", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0)))
                .build();
        Request request = new Request.Builder().url(Constant.BASE_URL + GET_PARENT_INFO_PATH).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if (result != null && !result.equals("")) {
                    Parent parent = gson.fromJson(result, Parent.class);
                    EventBus.getDefault().post(parent);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getHomeAddress(Parent parent) {
        String province = parent.getProvince();
        String city = parent.getCity();
        String area = parent.getArea();
        String detail = parent.getDetailAddress();
        if (province != null && !province.equals("") && city != null && !city.equals("") && area != null && !area.equals("") && detail != null && !detail.equals("")) {
            String homeAddress = province + city + area + detail;
            getGeoCoder(homeAddress);
        } else {
            Toast.makeText(this, "请设置家庭住址", Toast.LENGTH_LONG).show();
        }
    }

    private void getGeoCoder(String homeAddress) {
        Request request = new Request.Builder()
                .url("http://api.map.baidu.com/geocoding/v3/?address=" + homeAddress + "&output=json&ak=" + Constant.BAIDU_API_KEY + "&mcode=" + Constant.BAIDU_MCODE)
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
                if (result != null && !result.equals("")) {
                    BaiduAPIResponse baiduAPIResponse = gson.fromJson(result, BaiduAPIResponse.class);
                    EventBus.getDefault().post(baiduAPIResponse);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void parseLatLng(BaiduAPIResponse baiduAPIResponse) {
        double latitude = baiduAPIResponse.getResult().getLocation().getLat();
        double longitude = baiduAPIResponse.getResult().getLocation().getLng();
        endPoint = new LatLng(latitude, longitude);

        locationClient = new LocationClient(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            startLocation();
        }
    }

    private void startLocation() {
        locationClientOption = new LocationClientOption();
        locationClientOption.setOpenGps(true);
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClient.setLocOption(locationClientOption);
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                LatLng point = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                OverlayOptions options = new MarkerOptions()
                        .position(point)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                baiduMap.addOverlay(options);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(point);
                baiduMap.animateMapStatus(msu);
                startNavigation(point);
            }
        });
        locationClient.start();
    }

    private void startNavigation(final LatLng point) {
        WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                Log.e("success", "引擎初始化成功");
                routeWalkPlanWithParam(point);
            }

            @Override
            public void engineInitFail() {
                Log.e("fail", "引擎初始化失败");
            }
        });
    }

    private void routeWalkPlanWithParam(LatLng startPoint) {
        WalkNaviLaunchParam mWalkParam = new WalkNaviLaunchParam().stPt(startPoint).endPt(endPoint);
        WalkNavigateHelper.getInstance().routePlanWithParams(mWalkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.e("routePlanStart", "开始算路");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.e("routePlanSuccess", "算路成功");
                Intent intent = new Intent(LocationActivity.this, NavigationActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                // 步行导航中距离过远会算路失败
                Log.e("routePlanFail", "算路失败");
            }
        });
    }

    private void setDefaultScale() {
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
        baiduMap.setMapStatus(msu);
    }

    private void hideBaiduLogo() {
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        EventBus.getDefault().unregister(this);
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
