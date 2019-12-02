package com.happier.crow.parent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.Children;
import com.happier.crow.entities.Contact;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParentsContactActivity extends AppCompatActivity {
    private List<Map<String,Object>> dataSource = new ArrayList<>();
    private String pid;
    public static final String Parent_CONTACT_PATH = "/contact/showContacts";
    public static final String SHOW_CONTACT_PATH = "/contact/showContacts";
    ContactAdapter adapter;
    ListView list;
    Gson gson = new Gson();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_contact);

        SharedPreferences sharedPreferences = getSharedPreferences("authid",MODE_PRIVATE);
        pid = String.valueOf(sharedPreferences.getInt("pid",0));


        //获取通讯录数据
        GetDataThread thread = new GetDataThread();
        thread.start();
        while (thread.flag == false){
            Log.e("flage","ThreadisRunning");
        }


        list = findViewById(R.id.y_lv_contact);
        adapter = new ContactAdapter(
                this,
                dataSource,
                R.layout.listview_contacts
        );
        list.setAdapter(adapter);

        ImageView ivToAdd = findViewById(R.id.y_iv_toAddContact);
        ivToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentsContactActivity.this,ParentsAddContactActivity.class);
                startActivity(intent);
            }
        });


    }


    


    @Override
    protected void onResume() {
        super.onResume();
        GetDataThread thread = new GetDataThread();
        thread.start();
        while (thread.flag == false){
            Log.e("flage","ThreadisRunning");
        }
        Log.e("newData",dataSource.toString());
        adapter.notifyDataSetChanged();
    }


    private void initData(String data) {
        try{
            Type type = new TypeToken<List<Map<String,Object>>>(){}.getType();
            dataSource = gson.fromJson(data,type);
            adapter.notifyDataSetChanged();
            Log.e("dataSource",dataSource.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private class GetDataThread extends Thread{
        boolean flag=false;
        String jsonUrl;
        String result;

        public GetDataThread(){

        }

        @Override
        public void run() {
            super.run();
            try{
                URL url = new URL(Constant.BASE_URL+Parent_CONTACT_PATH+"?id="+pid+"&adderStatus=0");
                URLConnection conn = url.openConnection();
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                String data = reader.readLine();
                Log.e("dataaa",data);
                if(data!=null){
                    initData(data);
                }else {
                    Log.e("错误","联系人数据为空");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            callBack();
        }

        private void callBack() {
            flag=true;
        }
    }


}
