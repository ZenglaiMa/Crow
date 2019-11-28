package com.happier.crow.children.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.happier.crow.R;
import com.happier.crow.children.ChildrenSetAlarmActivity;

public class InteractFragment extends Fragment {

    private LinearLayout llSetNotify;
    private LinearLayout llUploadPicture;
    private LinearLayout llLookPicture;
    private LinearLayout llChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interact, container, false);

        findViews(view);

        setListener();

        return view;
    }

    private void setListener() {
        MyListener listener = new MyListener();
        llSetNotify.setOnClickListener(listener);
        llUploadPicture.setOnClickListener(listener);
        llLookPicture.setOnClickListener(listener);
        llChat.setOnClickListener(listener);
    }

    private void findViews(View view) {
        llSetNotify = view.findViewById(R.id.z_ll_setNotify);
        llUploadPicture = view.findViewById(R.id.m_ll_upload_picture);
        llLookPicture = view.findViewById(R.id.m_ll_look_picture);
        llChat = view.findViewById(R.id.m_ll_chat);
    }

    public class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.z_ll_setNotify:
                    Intent intent = new Intent(getContext(), ChildrenSetAlarmActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.m_ll_upload_picture:
                    // todo : upload pictures
                    Toast.makeText(getContext(), "上传亲情图片", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_ll_look_picture:
                    // todo : scan pictures
                    Toast.makeText(getContext(), "查看亲情图片", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_ll_chat:
                    // todo : go to chat
                    Toast.makeText(getContext(), "即时聊天", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
