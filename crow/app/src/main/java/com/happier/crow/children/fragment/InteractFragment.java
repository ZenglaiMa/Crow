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

import com.happier.crow.R;
import com.happier.crow.children.ChildrenSetAlarmActivity;

public class InteractFragment extends Fragment {
    private View view = null;
    private LinearLayout btnSetNotify = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_interact, container, false);
        btnSetNotify = view.findViewById(R.id.z_ll_setNotify);
        init();
        return view;
    }
    private void init(){
        btnSetNotify = view.findViewById(R.id.z_ll_setNotify);

        MyListener listener = new MyListener();
        btnSetNotify.setOnClickListener(listener);
    }
    public class MyListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.z_ll_setNotify:
                    Intent intent = new Intent(getContext(), ChildrenSetAlarmActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
