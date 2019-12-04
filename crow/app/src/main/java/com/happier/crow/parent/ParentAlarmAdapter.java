package com.happier.crow.parent;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.Alarm;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class ParentAlarmAdapter extends BaseAdapter {
    private List<Alarm> list = null;
    private Context context = null;
    private int item;
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    private static final String PARENT_SET_INFO_PATH = "/alarm/changeState";
    private AlarmManager alm;
    private PendingIntent pi;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvType = convertView.findViewById(R.id.z_tv_parent_alarm_item_type);
            viewHolder.tvTime = convertView.findViewById(R.id.z_tv_parent_alarm_item_time);
            viewHolder.swState = convertView.findViewById(R.id.z_sw_parent_alarm_item_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String timeTemp = list.get(position).getTime();
        String[] times = timeTemp.split(",");
        String time = times[0] + "  " + times[1] + ":" + times[2];
        String type = list.get(position).getType();
        viewHolder.tvTime.setText(time);
        viewHolder.tvType.setText(type);
        if (list.get(position).getState() == 0) {//关
            viewHolder.swState.setChecked(false);
        } else {// == 1  开
            viewHolder.swState.setChecked(true);
            setAlarm(type, list.get(position).getDescription(), times, position);
        }
        viewHolder.swState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//改成了开
                    setOrModify(list.get(position).getId(), 1);
                    list.get(position).setState(1);
                } else {//改成了关
                    setOrModify(list.get(position).getId(), 0);
                    list.get(position).setState(0);
                    alm.cancel(pi);
                }
            }
        });
        return convertView;
    }

    private void setOrModify(int id, int state) {
        OkHttpClient client = new OkHttpClient();
        int pid = context.getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0);
        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("state", String.valueOf(state))
                .build();
        final Request request = new Request.Builder()
                .url(Constant.BASE_URL + PARENT_SET_INFO_PATH)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    private class ViewHolder {
        public TextView tvType = null;
        public TextView tvTime = null;
        public Switch swState = null;
    }

    private void setAlarm(String type, String description, String[] times, int position) {
        int hour = Integer.parseInt(times[1]);
        int minute = Integer.parseInt(times[2]);
        Intent intent = new Intent();
        intent.setClass(context, AlarmReceiver.class);

        Calendar c = Calendar.getInstance();
        if (times[0].equals("下午")) {
            c.set(Calendar.AM_PM, Calendar.PM);
        }

        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        intent.putExtra("messageTitle", type);
        intent.putExtra("messageContent", description);

        pi = PendingIntent.getBroadcast(context, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (System.currentTimeMillis() > c.getTimeInMillis()) {
            c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
        }
        alm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

}
