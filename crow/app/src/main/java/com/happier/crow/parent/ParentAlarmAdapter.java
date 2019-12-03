package com.happier.crow.parent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.happier.crow.R;
import com.happier.crow.entities.Alarm;

import java.util.List;

public class ParentAlarmAdapter extends BaseAdapter {
    private List<Alarm> list = null;
    private Context context = null;
    private int item;

    public ParentAlarmAdapter(List<Alarm> list, Context context, int item) {
        this.list = list;
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvType = convertView.findViewById(R.id.z_tv_parent_alarm_item_type);
            viewHolder.tvTime = convertView.findViewById(R.id.z_tv_parent_alarm_item_time);
            viewHolder.swState = convertView.findViewById(R.id.z_sw_parent_alarm_item_state);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String timeTemp = list.get(position).getTime();
        String[] times = timeTemp.split(",");
        String time = times[0] + "  " + times[1] + ":" + times[2];
        String type = list.get(position).getType();
        viewHolder.tvTime.setText(time);
        viewHolder.tvType.setText(type);
        if(list.get(position).getState() == 0){//关
            viewHolder.swState.setChecked(false);
        }else {// == 1  开
            viewHolder.swState.setChecked(true);
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView tvType = null;
        public TextView tvTime = null;
        public Switch swState = null;
    }
}
