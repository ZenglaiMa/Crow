package com.happier.crow.children.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.happier.crow.AboutActivity;
import com.happier.crow.MainActivity;
import com.happier.crow.R;
import com.happier.crow.children.ChildrenInfoActivity;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.Children;

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

public class CenterFragment extends Fragment {

    private ImageView ivHeader;
    private TextView tvNickName;
    private LinearLayout llAddParent;
    private LinearLayout llPhotograph;
    private LinearLayout llPersonalInfo;
    private LinearLayout llLogout;
    private LinearLayout llAboutUs;

    private Children children;

    private static final String CHILDREN_INFO_PATH = "/children/getInfo";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        EventBus.getDefault().register(this);

        View view = inflater.inflate(R.layout.fragment_center, container, false);

        findViews(view);

        setListener();

        getInfo();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }

    private void getInfo() {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("cid", String.valueOf(getContext().getSharedPreferences("authid", Context.MODE_PRIVATE).getInt("cid", 0)))
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + CHILDREN_INFO_PATH)
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
                String result = response.body().string();
                if (result != null && !result.equals("")) {
                    EventBus.getDefault().post(result);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleResult(String result) {
        children = new Gson().fromJson(result, Children.class);
        if (children.getName() != null && !children.getName().equals("")) {
            tvNickName.setText(children.getName());
        }
        if (children.getIconPath() != null && !children.getIconPath().equals("")) {
            RequestOptions options = new RequestOptions().circleCrop();
            Glide.with(this).load(Constant.BASE_URL + children.getIconPath()).apply(options).into(ivHeader);
        }
    }

    private void setListener() {
        MyListener listener = new MyListener();
        llAddParent.setOnClickListener(listener);
        llPhotograph.setOnClickListener(listener);
        llPersonalInfo.setOnClickListener(listener);
        llLogout.setOnClickListener(listener);
        llAboutUs.setOnClickListener(listener);
    }

    private void findViews(View view) {
        ivHeader = view.findViewById(R.id.m_iv_children_header_image);
        tvNickName = view.findViewById(R.id.m_tv_children_nickname);
        llAddParent = view.findViewById(R.id.m_ll_add_parent);
        llPhotograph = view.findViewById(R.id.m_ll_my_photograph);
        llPersonalInfo = view.findViewById(R.id.m_ll_my_personal_info);
        llLogout = view.findViewById(R.id.m_ll_children_logout);
        llAboutUs = view.findViewById(R.id.m_ll_about_us);
    }

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.m_ll_add_parent:
                    // todo : add parents
                    Toast.makeText(getContext(), "添加父母", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_ll_my_photograph:
                    // todo : my photograph
                    Toast.makeText(getContext(), "我的相册", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_ll_my_personal_info:
                    Intent personalInfoIntent = new Intent(getContext(), ChildrenInfoActivity.class);
                    personalInfoIntent.putExtra("headerImagePath", children.getIconPath());
                    personalInfoIntent.putExtra("name", children.getName());
                    personalInfoIntent.putExtra("age", children.getAge());
                    personalInfoIntent.putExtra("gender", children.getGender());
                    personalInfoIntent.putExtra("profile", children.getProfile());
                    startActivity(personalInfoIntent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.m_ll_children_logout:
                    Intent logoutIntent = new Intent(getContext(), MainActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    break;
                case R.id.m_ll_about_us:
                    Intent aboutUsIntent = new Intent(getContext(), AboutActivity.class);
                    startActivity(aboutUsIntent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
