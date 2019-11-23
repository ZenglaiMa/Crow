package com.happier.crow.parent;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.happier.crow.R;

public class ParentIndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_index);

        SharedPreferences preferences = getSharedPreferences("authid", MODE_PRIVATE);
        Log.e("pid", "" + preferences.getInt("pid", 0));
    }
}
