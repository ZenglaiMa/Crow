package com.happier.crow.Util;

import android.graphics.Color;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.model.SortType;
import com.happier.crow.R;
import com.happier.crow.children.ChildrenIndexActivity;

import java.util.List;


public class TraceUtil1 {
    public BitmapDescriptor bmStart = null;
    public BitmapDescriptor bmEnd = null;
    public BitmapDescriptor bmArrowPoint = null;
    public BaiduMap Map;
    /**
     * 路线覆盖物
     */
    public Overlay polylineOverlay = null;
    private Marker mMoveMarker = null;
    private MapStatus mapStatus = null;

    /**
     * 绘制历史轨迹
     */
    public void drawHistoryTrack(String entityName, BaiduMap map, final List<LatLng> points, SortType sortType) {
        Map = map;
        // 绘制新覆盖物前，清空之前的覆盖物
        map.clear();
        if (null != mMoveMarker) {
            mMoveMarker.remove();
            mMoveMarker = null;
        }
        if (points == null || points.size() == 0) {
            if (null != polylineOverlay) {
                polylineOverlay.remove();
                polylineOverlay = null;
            }
            return;
        }
//资源文件
        bmStart = BitmapDescriptorFactory.fromResource(R.drawable.start);
        bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.end);
        bmArrowPoint = BitmapDescriptorFactory.fromResource(R.drawable.location_selected);
        if (points.size() == 1) {
            OverlayOptions startOptions = new MarkerOptions().position(points.get(0)).icon(bmStart)
                    .zIndex(9).draggable(true);
            Map.addOverlay(startOptions);
            animateMapStatus(points.get(0), 18.0f);
            return;
        }

        LatLng startPoint;
        LatLng endPoint;
        if (sortType == SortType.asc) {
            startPoint = points.get(0);
            endPoint = points.get(points.size() - 1);
        } else {
            startPoint = points.get(points.size() - 1);
            endPoint = points.get(0);
        }

        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions()
                .position(startPoint).icon(bmStart)
                .zIndex(9).draggable(true);
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions().position(endPoint)
                .icon(bmEnd).zIndex(9).draggable(true);

        // 添加路线（轨迹）
        OverlayOptions polylineOptions;
        if (entityName.equals(ChildrenIndexActivity.fPhone)) {
            polylineOptions = new PolylineOptions().width(10)
                    .color(Color.BLUE).points(points);
        }else{
            polylineOptions = new PolylineOptions().width(10)
                    .color(Color.RED).points(points);
        }

        Map.addOverlay(startOptions);
        Map.addOverlay(endOptions);
        polylineOverlay = Map.addOverlay(polylineOptions);

        OverlayOptions markerOptions =
                new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(bmArrowPoint)
                        .position(points.get(points.size() - 1))
                        .rotate((float) MapUtils.getAngle(points.get(0), points.get(1)));
        mMoveMarker = (Marker) Map.addOverlay(markerOptions);
        animateMapStatus(points);

    }

    public void animateMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        Map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    public void animateMapStatus(List<LatLng> points) {
        if (null == points || points.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
        Map.animateMapStatus(msUpdate);
    }
}
