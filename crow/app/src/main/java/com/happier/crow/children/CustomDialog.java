package com.happier.crow.children;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.ContactParent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomDialog extends DialogFragment {

    private TextView tvConfirm;
    private TextView tvCancel;
    private String phone;
    private String remark;
    private int id;
    private int adderStatus = 1;
    Gson gson = new Gson();
    ContactParent contactParent;
    public static final String CHECK_PATH = "/contact/checkParents";
    public static final String ADDPARENTS_PATH = "/contact/addContact";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm, null);
        EventBus.getDefault().register(this);
        tvConfirm = view.findViewById(R.id.y_tv_confirm);
        tvCancel = view.findViewById(R.id.y_tv_cancel);

        //为按钮绑定监听器
        CustomDialogListener listener = new CustomDialogListener();
        tvConfirm.setOnClickListener(listener);
        tvCancel.setOnClickListener(listener);
        return view;
    }

    //设置按钮样式
    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.25);
        win.setAttributes(params);
    }

    @Subscribe(sticky = true)
    public void receiveMessage(String event) {
        contactParent = gson.fromJson(event, new TypeToken<ContactParent>() {
        }.getType());
    }


    private class CustomDialogListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.y_tv_confirm) {
                //确认,保存信息
                phone = contactParent.getPhone();
                id = contactParent.getId();
                remark = contactParent.getRemark();
                checkInfo(id, phone, remark);
            }
            if (v.getId() == R.id.y_tv_cancel) {
                //取消
                getDialog().dismiss();
            }
        }
    }

    //检查信息是否正确
    private void checkInfo(final int id, final String phone, final String remark) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("adderId", String.valueOf(id))
                .add("phone", phone)
                .add("remark", remark)
                .build();

        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + CHECK_PATH)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String tag = response.body().string();
                if (tag.equals("101")) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "添加的账户尚未注册, 请注册后添加", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                    Looper.loop();
                } else if (tag.equals("999")) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "已添加该用户, 请勿重复添加", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                    Looper.loop();
                } else if (tag.equals("11")) {
                    addParent(id, phone, remark);//信息正确执行添加方法
                }
            }
        });

    }

    //添加方法
    private void addParent(int id, String phone, String remark) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("adderId", String.valueOf(id))
                .add("phone", phone)
                .add("remark", remark)
                .add("adderStatus", String.valueOf(adderStatus))
                .add("isIce", String.valueOf(0))
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + ADDPARENTS_PATH)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String tag = response.body().string();
                if (tag.equals("1")) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                    Looper.loop();
                } else if (tag.equals("0")) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "添加失败", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                    Looper.loop();
                }
            }
        });
    }

}
