package com.happier.crow.parent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.happier.crow.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> dataSource = new ArrayList<>();
    private int item_layout_id;
    public static final int REQUEST_CALL_PERMISSION = 10111;

    public ContactAdapter(Context context, List<Map<String, Object>> dataSource, int item_layout_id) {
        this.context = context;
        this.dataSource = dataSource;
        this.item_layout_id = item_layout_id;
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(item_layout_id, null);
        }

        TextView tvName = convertView.findViewById(R.id.ytv_contactName);
        TextView tvPhone = convertView.findViewById(R.id.ytv_contactPhone);
        ImageView ivCall = convertView.findViewById(R.id.y_iv_call);

        final Map<String, Object> map = dataSource.get(position);

        tvName.setText(map.get("remark").toString());
        tvPhone.setText(map.get("phone").toString());
        Log.e("myPhone", map.get("phone").toString());
        //点击拨打电话
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhone("tel:" + map.get("phone").toString());
            }

        });

        return convertView;
    }

    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(context, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions((Activity) context, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    private void callPhone(String phoneNum) {
        if (checkReadPermission(Manifest.permission.CALL_PHONE, REQUEST_CALL_PERMISSION)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNum));
            context.startActivity(intent);
        }
    }
}
