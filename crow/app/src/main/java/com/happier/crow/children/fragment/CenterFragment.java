package com.happier.crow.children.fragment;

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

import com.happier.crow.AboutActivity;
import com.happier.crow.MainActivity;
import com.happier.crow.R;
import com.happier.crow.children.ChildrenInfoActivity;

public class CenterFragment extends Fragment {

    private ImageView ivHeader;
    private TextView tvNickName;
    private LinearLayout llAddParent;
    private LinearLayout llPhotograph;
    private LinearLayout llPersonalInfo;
    private LinearLayout llLogout;
    private LinearLayout llAboutUs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_center, container, false);

        findViews(view);

        setListener();

        return view;
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
                    startActivity(personalInfoIntent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.m_ll_children_logout:
                    Intent logoutIntent = new Intent(getContext(), MainActivity.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logoutIntent);
                    break;
                case R.id.m_ll_about_us:
                    Intent aboutUsIntent = new Intent(getContext(), AboutActivity.class);
                    startActivity(aboutUsIntent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
            }
        }
    }
}
