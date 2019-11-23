package com.happier.crow.children;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.happier.crow.R;

public class ChildrenIndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_index);
        SharedPreferences preferences = getSharedPreferences("authid", MODE_PRIVATE);
        Log.e("cid", "" + preferences.getInt("cid", 0));
    }
}
