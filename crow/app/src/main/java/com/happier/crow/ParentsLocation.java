package com.happier.crow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.Trace;
import com.baidu.trace.LBSTraceClient;

import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.QueryCacheTrackResponse;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.happier.crow.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ParentsLocation extends AppCompatActivity {
    /*百度地图定位显示*/
    private MapView mapView;
    private BaiduMap baiduMap;
    private final int REQUEST_GPS = 1;
    private static final int GET_LOCATION =1 ;
    /*主线程*/
    private Handler mainHandler;

    /*固定设备采集定位*/
    //本机号码
    private String entityName;
    // 定位周期(单位:秒)
    private int gatherInterval = 5;
    // 打包回传周期(单位:秒)
    private int packInterval = 10;
    // 是否存储图像
    private boolean isNeedObjectStorage = true;
    //经纬度
    private Double longitude;
    private Double latitude;
    //定位回传结果
    private JSONObject response;
    private Trace mTrace;
    private LBSTraceClient mTraceClient;
    private OnTraceListener mTraceListener;

    /*绘制轨迹*/
    private List<com.baidu.mapapi.model.LatLng> trackPoints;
    // 历史轨迹请求实例
    private HistoryTrackRequest historyTrackRequest;
    private long startTime = System.currentTimeMillis() / 1000 - 12 * 60 * 60;
    // 结束时间(单位：秒)
    private long endTime = System.currentTimeMillis() / 1000;
    private long timeMillis = System.currentTimeMillis();
    private OnTrackListener mHistoryListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.baidu_location);
        getView();
        initAll();
        beginService();

    }

    private void beginService() {
        //开启服务
        mTraceClient.startTrace(mTrace, mTraceListener);
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
        // 设备标识ky
        entityName = "lky";
        // 初始化轨迹服务
        mTrace = new Trace(Constant.serviceId, entityName, isNeedObjectStorage);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(getApplicationContext());
        mTraceClient.setLocationMode(LocationMode.High_Accuracy);
        //设置回传周期
        mTraceClient.setInterval(gatherInterval, packInterval);
        //初始化监听器
        mTraceListener = new OnTraceListener() {

            @Override
            public void onBindServiceCallback(int i, String s) {
                Log.e("bind", "已绑定");
            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                Log.e("start", "已启动"+i);
                getLocation(entityName);
                if(i==0){
                    // 开启采集
                    mTraceClient.startGather(mTraceListener);
                }
            }

            @Override
            public void onStopTraceCallback(int i, String s) {

            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                Log.e("gather", "开始采集；");
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
        //初始化mainHandler
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case GET_LOCATION:
                        drawLocation();
                        break;
                }
            }
        };

        //绘制历史轨迹
        /*
        historyTrackRequest = new HistoryTrackRequest(1, Constant.serviceId, entityName);
        historyTrackRequest.setStartTime(startTime);
        // 设置结束时间
        historyTrackRequest.setEndTime(endTime);

        historyTrackRequest.setPageSize(1000);
        historyTrackRequest.setPageIndex(1);*/
       /* mHistoryListener = new OnTrackListener() {
            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
                super.onLatestPointCallback(latestPointResponse);
                Log.e("xixi",latestPointResponse.toString());
            }

            // 历史轨迹回调
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                int total = response.getTotal();
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    //Toast.makeText(ParentsLocation.this, "结果为：" + response.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("trace",response.getMessage().toString()+response.getStatus());
                } else if (0 == total) {
                    Toast.makeText(ParentsLocation.this, "未查询到历史轨迹", Toast.LENGTH_SHORT).show();
                } else {
                    List<TrackPoint> points = response.getTrackPoints();
                    if (null != points) {
                        for (TrackPoint trackPoint : points) {
                            if (!TraceUtil.isZeroPoint(trackPoint.getLocation().getLatitude(),
                                    trackPoint.getLocation().getLongitude())) {
                                trackPoints.add(TraceUtil.convertTrace2Map(trackPoint.getLocation()));
                            }
                        }
                    }
                    Log.e("poins",points.toArray().toString());
                }
                TraceUtil traceUtil=new TraceUtil();
                Log.e("xiix",trackPoints.toArray().toString());
                traceUtil.drawHistoryTrack(baiduMap,trackPoints, SortType.asc);
            }
        };
        */
    /*
        mHistoryListener= new OnTrackListener() {
           private List<LatLng> trackPoints = new ArrayList<>();
           public static final int PAGE_SIZE = 5000;
           private SortType sortType = SortType.asc;
           private int pageIndex = 1;
           // 历史轨迹回调
           @Override
           public void onHistoryTrackCallback(HistoryTrackResponse response) {
               int toal = response.getTotal();
               if (StatusCodes.SUCCESS != response.getStatus()) {
                   Log.e("fail",response.message.toString()+response.tag+response.status);
               }else if (0 == toal){
                   Log.e("fail","未查询到轨迹");
               }else {
                   List<TrackPoint> points = response.getTrackPoints();
                   if (null != points) {
                       for (TrackPoint trackPoint : points) {
                           if (!MapUtils.isZeroPoint(trackPoint.getLocation().getLatitude(),
                                   trackPoint.getLocation().getLongitude())) {
                               trackPoints.add(new LatLng(trackPoint.getLocation().latitude, trackPoint.getLocation().longitude));
                           }
                       }
                   }
               }
               TraceUtil1 traceUtil=new TraceUtil1();
               traceUtil.drawHistoryTrack(baiduMap,trackPoints, sortType);

           }
       };*/
    }
    //创建选项菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parent_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.father_location:
                getLocation(entityName);
                break;
            case R.id.mother_location:
                getLocation(entityName);
                break;
            case R.id.father_trace:
                //mTraceClient.queryHistoryTrack(historyTrackRequest,mHistoryListener);
                break;
            case R.id.mother_trace:
                //mTraceClient.queryHistoryTrack(historyTrackRequest,mHistoryListener);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void drawLocation() {
       // entityLocation();
        LatLng latLng = new LatLng(latitude, longitude);
        //添加标志物来标明当前位置
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.location_normal);
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(descriptor)
                .position(latLng)
                .draggable(true);
        //3.添加到地图显示
        Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
        marker.setAlpha(0.5f);
        //移动地图显示当前位置
        MapStatusUpdate move = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(move);
    }

    private void getLocation(final String entity_name) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                URL url = null;
                PrintWriter out = null;
                BufferedReader in = null;
                String result = "";
                try {
                    URL realUrl = new URL("http://yingyan.baidu.com/api/v3/entity/search?ak=" + Constant.ak + "&&service_id=" + Constant.serviceId + "&&filter=entity_names:" + entity_name + "&&coord_type_output=bd09ll");
//                  URL realUrl = new URL("http://yingyan.baidu.com/api/v3/entity/list?ak=" + Constant.ak + "&service_id=" + Constant.serviceId  + "&&coord_type_output=bd09ll");
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
                    String message = response.getString("message");
                    int total = response.getInt("total");
                    JSONArray jsonArray = response.getJSONArray("entities");
                    JSONObject obj = (JSONObject) jsonArray.get(0);
                    JSONObject location = obj.getJSONObject("latest_location");
                    longitude = location.getDouble("longitude");
                    latitude = location.getDouble("latitude");
                    Log.e("location", longitude + "" + latitude + "");
                    Log.e("array", jsonArray.toString());
                    Log.e("response", result);
                    Log.e("total", total + "");
//                    if (total == 0) {
//                       addEntity();
                    //              }
                   Message msg = new Message();
                   msg.what=GET_LOCATION;
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
        String params = "ak=" + Constant.ak + "&" + "service_id=" + Constant.serviceId + "&"
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
    private void deleteEntity() {
        new Thread() {
            @Override
            public void run() {
                URL url = null;
                String params = "ak=" + Constant.ak + "&" + "service_id=" + Constant.serviceId + "&"
                        + "entity_name=" + entityName;
                PrintWriter out = null;
                BufferedReader in = null;
                String result = "";
                try {
                    URL realUrl = new URL("http://yingyan.baidu.com/api/v3/entity/delete");
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
        }.start();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("stop", "stop");
        //  super.onDestroy();
        mTraceClient.stopTrace(mTrace, mTraceListener);
    }
}
