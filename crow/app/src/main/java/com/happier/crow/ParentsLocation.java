package com.happier.crow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.trace.Trace;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.fence.OnFenceListener;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.analysis.OnAnalysisListener;
import com.baidu.trace.api.bos.OnBosListener;
import com.baidu.trace.model.PushMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

public class ParentsLocation extends AppCompatActivity {
    private MapView mapView;
    private BaiduMap baiduMap;
    //轨迹服务ID
    private long serviceId = 217607;
    //AK
    private static String ak = "48sp5txV8ifKVu622199moP9oYMKGoAT&&" +
            "mcode=D4:7D:00:95:6D:0D:35:2C:84:29:63:DF:B6:C3:EB:96:A3:3E:4E:70;com.happier.crow";
    //本机号码
    private String entityName;
    // 定位周期(单位:秒)
    private int gatherInterval = 5;
    // 打包回传周期(单位:秒)
    private int packInterval = 10;
    // 设置定位和打包周期
    private boolean isNeedObjectStorage = true;
    private Trace mTrace;
    private LBSTraceClient mTraceClient;
    private OnTraceListener mTraceListener;
    private Handler mainHandler;
    private Double longitude;
    private Double latitude;
    private JSONObject response;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //环境初始化最好是应用程序级别的上下文
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.baidu_location);
        Log.e("nihao", "begin");
        getView();
        initAll();
        joinInTrace(entityName);


    }

    private void entityLocation() {
        JSONArray jsonArray = null;
        try {
            jsonArray = response.getJSONArray("entities");
            JSONObject obj = (JSONObject) jsonArray.get(0);
            JSONObject location = obj.getJSONObject("latest_location");
            longitude = location.getDouble("longitude");
            latitude = location.getDouble("latitude");
            Log.e("location", longitude + "" + latitude + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getView() {
        //获取地图控件
        mapView = findViewById(R.id.map_view);
        //获取百度地图
        baiduMap = mapView.getMap();
    }

    private void initAll() {
        //显示普通地图模式
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 设备标识
        entityName = "lky";
        // 初始化轨迹服务
        mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(getApplicationContext());
        //设置回传周期
        mTraceClient.setInterval(gatherInterval, packInterval);
        //初始化mainHandler
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                entityLocation();
                // 开启服务
                mTraceClient.startTrace(mTrace, mTraceListener);
                // 开启采集
                mTraceClient.startGather(mTraceListener);
            }
        };
        //初始化监听器
        mTraceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {
                Log.e("bind", "已绑定");
            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                Log.e("start", "已启动");
            }

            @Override
            public void onStopTraceCallback(int i, String s) {

            }

            @Override
            public void onStartGatherCallback(int i, String s) {

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

    private void joinInTrace(final String entity_name) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                URL url = null;
                PrintWriter out = null;
                BufferedReader in = null;
                String result = "";
                try {
                    URL realUrl = new URL("http://yingyan.baidu.com/api/v3/entity/list?ak=" + ak + "&service_id=" + serviceId + "&filter=entity_names:" + entity_name + "&&coord_type_output=bd09ll");
                    // 打开和URL之间的连接
                    URLConnection conn = realUrl.openConnection();
                    // 设置通用的请求属性
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("user-agent",
                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8; ");
                    // 定义BufferedReader输入流来读取URL的响应
                    in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        result += line;
                    }
                    response = new JSONObject(result);
                    int total = response.getInt("total");
                    String message = response.getString("message");
                    JSONArray jsonArray = response.getJSONArray("entities");
                    Log.e("array", jsonArray.toString());
                    Log.e("response", result);
                    Log.e("total", total + "");
                   // entityLocation();
                    if (total == 0) {
                        addEntity();
                    }
                    Message msg = new Message();
                    mainHandler.sendMessage(msg);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void addEntity() {
        URL url = null;
        String params = "ak=" + ak + "&" + "service_id=" + serviceId + "&"
                + "entity_name=" + entityName;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL("http://yingyan.baidu.com/api/v3/entity/add");
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8; ");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            JSONObject response0 = new JSONObject(result);
            String message = response0.getString("message");
            int status = response0.getInt("status");
            Log.e("message", message + status);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
