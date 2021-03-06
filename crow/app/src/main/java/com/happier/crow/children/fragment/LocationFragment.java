package com.happier.crow.children.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;
import com.happier.crow.Util.MapUtils;
import com.happier.crow.R;
import com.happier.crow.Util.TraceUtil1;
import com.happier.crow.children.ChildrenIndexActivity;
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

public class LocationFragment extends Fragment {
    /*百度地图定位显示*/
    private MapView mapView;
    private BaiduMap baiduMap;
    private static final int GET_LOCATION = 1;
    private RefreshThread refreshThread;
    public String entityName;
    /*主线程*/
    private Handler mainHandler;
    //经纬度
    private Double longitude;
    private Double latitude;
    //定位回传结果
    private JSONObject response;
    private View popView;
    /*绘制轨迹*/
    // 历史轨迹请求实例
    private HistoryTrackRequest historyTrackRequest;
    private long startTime = System.currentTimeMillis() / 1000 - 24 * 60 * 60;
    // 结束时间(单位：秒)
    private long endTime = System.currentTimeMillis() / 1000;
    private OnTrackListener mHistoryListener;
    // 初始化轨迹服务
    Trace mTrace;
    // 初始化轨迹服务客户端
    LBSTraceClient mTraceClient;
    private boolean isNeedObjectStorage = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        getView(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.location, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.m_parent_location): {
                showPopWindow();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopWindow() {
        popView = this.getLayoutInflater().inflate(R.layout.layout_pop_window, null);
        //将物理像素转化为真实像素
        final PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0000000000));
        popupWindow.setOutsideTouchable(true); // 点击外部关闭
        Button btn1 = popView.findViewById(R.id.btn1);
        Button btn2 = popView.findViewById(R.id.btn2);
        Button btn3 = popView.findViewById(R.id.btn3);
        Button btn4 = popView.findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                startRefreshThread(false);
                changeName("mother");
                historyTrackRequest.setEntityName(entityName);
                mTraceClient.queryHistoryTrack(historyTrackRequest, mHistoryListener);

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                startRefreshThread(false);
                changeName("father");
                historyTrackRequest.setEntityName(entityName);
                mTraceClient.queryHistoryTrack(historyTrackRequest, mHistoryListener);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                changeName("mother");
                startRefreshThread(true);
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                changeName("father");
                startRefreshThread(true);
            }
        });
        popupWindow.showAtLocation(ChildrenIndexActivity.v, Gravity.RIGHT | Gravity.TOP, 12, 150);
    }

    @Override
    public void onStart() {
        super.onStart();
        initAll();
        startRefreshThread(true);
    }

    private void getView(View view) {
        //获取地图控件
        mapView = view.findViewById(R.id.map_view);
        //获取百度地图
        baiduMap = mapView.getMap();
    }

    private void changeName(String status) {
        if (status.equals("father")) {
            entityName = ChildrenIndexActivity.fPhone;

        } else {
            entityName = ChildrenIndexActivity.mPhone;
        }
    }

    private void initAll() {
        //显示普通地图模式
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //修改地图的默认比例尺
        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(20.0f);
        baiduMap.setMapStatus(update);
        entityName = ChildrenIndexActivity.entityName;
        //初始化轨迹服务
        mTrace = new Trace(Constant.serviceId, entityName, isNeedObjectStorage);
        //初始化轨迹客户端
        mTraceClient = new LBSTraceClient(this.getContext());
        //初始化mainHandler
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_LOCATION:
                        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(20.0f);//大小按需求计算就可以
                        baiduMap.animateMapStatus(u);
                        drawLocation();
                        break;
                }
            }
        };

        historyTrackRequest = new HistoryTrackRequest(1, Constant.serviceId, ChildrenIndexActivity.mPhone);
        //父亲历史轨迹
        historyTrackRequest.setStartTime(startTime);
        historyTrackRequest.setEndTime(endTime);
        historyTrackRequest.setPageSize(1000);
        historyTrackRequest.setPageIndex(1);
        historyTrackRequest.setProcessed(true);

        // 创建纠偏选项实例
        ProcessOption processOption = new ProcessOption();
        processOption.setNeedDenoise(true);
        processOption.setNeedVacuate(true);
        processOption.setNeedMapMatch(true);
        processOption.setRadiusThreshold(100);
        processOption.setTransportMode(TransportMode.walking);
        historyTrackRequest.setProcessOption(processOption);

        //历史轨迹监听器
        mHistoryListener = new OnTrackListener() {
            private List<LatLng> trackPoints = new ArrayList<>();
            private SortType sortType = SortType.asc;

            // 历史轨迹回调
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                int toal = response.getTotal();
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    Log.e("fail", response.message.toString() + response.tag + response.status);
                } else if (0 == toal) {
                    Log.e("fail", "未查询到轨迹");
                } else {
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
                TraceUtil1 traceUtil = new TraceUtil1();
                traceUtil.drawHistoryTrack(entityName, baiduMap, trackPoints, sortType);
                trackPoints.clear();
            }
        };
    }

    private void startRefreshThread(boolean isStart) {
        if (refreshThread == null) {
            refreshThread = new RefreshThread();
        }

        refreshThread.refresh = isStart;

        if (isStart) {
            if (!refreshThread.isAlive()) {
                refreshThread.start();
            }
        } else {
            refreshThread = null;
        }
    }

    private void drawLocation() {
        baiduMap.clear();
        LatLng latLng = new LatLng(latitude, longitude);
        BitmapDescriptor descriptor;
        //添加标志物来标明当前位置
        if (entityName.equals(ChildrenIndexActivity.fPhone)) {
            descriptor = BitmapDescriptorFactory.fromResource(R.drawable.dad);
        } else {
            descriptor = BitmapDescriptorFactory.fromResource(R.drawable.mom);
        }
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

    private class RefreshThread extends Thread {

        protected boolean refresh = true;
        protected int count = 0;

        public void run() {

            while (refresh) {
                getLocation(entityName);
                try {
                    count++;
                    Log.e("count", count + "");
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    System.out.println("线程休眠失败");
                }
            }

        }
    }

    private void getLocation(final String entity_name) {
        URL url = null;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        Log.e("entity", entity_name);
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
            entityLocation();

/*
            JSONArray jsonArray = response.getJSONArray("entities");
            JSONObject obj = (JSONObject) jsonArray.get(0);
            JSONObject location = obj.getJSONObject("latest_location");
            longitude = location.getDouble("longitude");
            latitude = location.getDouble("latitude");*/
            /*
            String message = response.getString("message");
            int total = response.getInt("total");
            Log.e("message", message);
            Log.e("location", longitude + "" + latitude + "");
            Log.e("array", jsonArray.toString());
            Log.e("response", result);
            Log.e("total", total + "");*/

            Message msg = new Message();
            msg.what = GET_LOCATION;
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
    public void onPause() {
        super.onPause();
        startRefreshThread(false);
    }
}
