package com.happier.crow.children;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happier.crow.R;
import com.happier.crow.children.fragment.CenterFragment;
import com.happier.crow.children.fragment.InteractFragment;
import com.happier.crow.children.fragment.LocationFragment;

import java.util.HashMap;
import java.util.Map;

public class ChildrenIndexActivity extends AppCompatActivity {

    private class MyTabSpec {
        private ImageView imageView = null;
        private TextView textView = null;
        private int normalImage;
        private int selectImage;
        private Fragment fragment = null;

        // 设置是否被选中
        public void setSelect(boolean b) {
            if (b) {
                imageView.setImageResource(selectImage);
                textView.setTextColor(Color.parseColor("#0000FF"));
            } else {
                imageView.setImageResource(normalImage);
                textView.setTextColor(Color.parseColor("#000000"));
            }
        }


        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public void setNormalImage(int normalImage) {
            this.normalImage = normalImage;
        }

        public void setSelectImage(int selectImage) {
            this.selectImage = selectImage;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }
    }

    private Map<String, MyTabSpec> map = new HashMap<>();

    private String[] tabStrId = {"亲情互动", "父母位置", "个人中心"};
    // 用于记录当前正在显示的Fragment
    private Fragment curFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_index);

        /*
        SharedPreferences preferences = getSharedPreferences("authid", MODE_PRIVATE);
        int cid = preferences.getInt("cid", 0);
        */

        // 1. 初始化MyTabSpec对象
        // Fragment、ImageView、TextView、normalImage、selectImage
        initData();

        // 2. 设置监听器，在监听器中完成切换
        setListener();

        // 3. 设置默认显示的TabSpec
        changeTab(tabStrId[0]);
    }

    // 自定义监听器内部类，完成Tab页面切换及图表转化
    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tab_spec_1:
                    changeTab(tabStrId[0]);
                    break;
                case R.id.tab_spec_2:
                    changeTab(tabStrId[1]);
                    break;
                case R.id.tab_spec_3:
                    changeTab(tabStrId[2]);
                    break;
            }
        }
    }

    // 根据Tab ID 切换Tab
    private void changeTab(String s) {
        // 1 切换Fragment
        changeFragment(s);
        // 2 切换图标及字体颜色
        changeImage(s);
    }

    // 根据Tab ID 切换Tab显示的Fragment
    private void changeFragment(String s) {
        Fragment fragment = map.get(s).getFragment();
        if (curFragment == fragment)
            return;
        // Fragment 事务 - Fragment事务管理器来获取
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 将之前显示的Fragment隐藏掉
        if (curFragment != null)
            transaction.hide(curFragment);
        // 如果当前Fragment没有被添加过 则添加到Activity的帧布局中
        if (!fragment.isAdded()) {
            transaction.add(R.id.tab_content, fragment);
        }
        // 显示对应Fragment
        transaction.show(fragment);
        curFragment = fragment;
        transaction.commit();
    }

    // 根据Tab ID 切换Tab显示的图片及字体颜色
    private void changeImage(String s) {
        // 1 所有Tab的图片和字体恢复默认
        for (String key : map.keySet()) {
            map.get(key).setSelect(false);
        }
        // 2 设置选中的Tab的图片和字体颜色
        map.get(s).setSelect(true);
    }

    // 设置监听器
    private void setListener() {
        LinearLayout layout1 = findViewById(R.id.tab_spec_1);
        LinearLayout layout2 = findViewById(R.id.tab_spec_2);
        LinearLayout layout3 = findViewById(R.id.tab_spec_3);

        MyListener listener = new MyListener();
        layout1.setOnClickListener(listener);
        layout2.setOnClickListener(listener);
        layout3.setOnClickListener(listener);
    }

    // 初始化MyTabSpec对象
    private void initData() {
        // 1 创建MyTabSpec对象
        map.put(tabStrId[0], new MyTabSpec());
        map.put(tabStrId[1], new MyTabSpec());
        map.put(tabStrId[2], new MyTabSpec());

        // 2 设置Fragment
        setFragment();

        // 3 设置ImageView和TextView
        findView();

        // 4 设置图片资源
        setImage();
    }

    // 将图片资源放入map的MyTabSpec对象
    private void setImage() {
        map.get(tabStrId[0]).setNormalImage(R.drawable.interact_normal);
        map.get(tabStrId[0]).setSelectImage(R.drawable.interact_selected);

        map.get(tabStrId[1]).setNormalImage(R.drawable.location_normal);
        map.get(tabStrId[1]).setSelectImage(R.drawable.location_selected);

        map.get(tabStrId[2]).setNormalImage(R.drawable.center_normal);
        map.get(tabStrId[2]).setSelectImage(R.drawable.center_selected);
    }

    // 创建Fragment对象放入map的MyTabSpec对象
    private void setFragment() {
        map.get(tabStrId[0]).setFragment(new InteractFragment());
        map.get(tabStrId[1]).setFragment(new LocationFragment());
        map.get(tabStrId[2]).setFragment(new CenterFragment());
    }

    // 将ImageView和TextView放入map中的MyTabSpec对象
    private void findView() {
        ImageView iv1 = findViewById(R.id.img_1);
        ImageView iv2 = findViewById(R.id.img_2);
        ImageView iv3 = findViewById(R.id.img_3);
        TextView tv1 = findViewById(R.id.tv_1);
        TextView tv2 = findViewById(R.id.tv_2);
        TextView tv3 = findViewById(R.id.tv_3);

        map.get(tabStrId[0]).setImageView(iv1);
        map.get(tabStrId[0]).setTextView(tv1);

        map.get(tabStrId[1]).setImageView(iv2);
        map.get(tabStrId[1]).setTextView(tv2);

        map.get(tabStrId[2]).setImageView(iv3);
        map.get(tabStrId[2]).setTextView(tv3);
    }
}
