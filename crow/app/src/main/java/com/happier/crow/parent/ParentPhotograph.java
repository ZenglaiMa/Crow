package com.happier.crow.parent;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParentPhotograph extends AppCompatActivity {

    private List<String> paths = new ArrayList<>();
    private ParentSharedPhotoAdapter adapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;

    private PopupWindow popupWindow = null;
    private View popupView = null;

    private OkHttpClient client = new OkHttpClient();

    private Gson gson = new Gson();

    private static final String SHOW_PHOTO_PATH = "/image/responseParentRequest";
    private static final String P_UPLOAD_PHOTO_PATH = "/image/uploadPhoto";

    private static final int TAKE_PHOTO = 1;
    private static final int SELECT_GRAPH = 2;

    private static final int PARENT_STATUS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_photograph);
        getSupportActionBar().setElevation(0);

        EventBus.getDefault().register(this);

        recyclerView = findViewById(R.id.m_recycler_view);
        smartRefreshLayout = findViewById(R.id.m_p_srf);

        initData();

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
            }
        });
    }

    private void initData() {
        FormBody body = new FormBody.Builder()
                .add("pid", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0)))
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + SHOW_PHOTO_PATH)
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
                } else {
                    Looper.prepare();
                    Toast.makeText(ParentPhotograph.this, "服务器错误, 请稍后重试", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlePhotoPath(String result) {
        paths = gson.fromJson(result, new TypeToken<List<String>>() {}.getType());
        adapter = new ParentSharedPhotoAdapter(this, paths, R.layout.recycler_view_item);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        smartRefreshLayout.finishRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parent_upload, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_parent_upload:
                if (popupWindow == null || !popupWindow.isShowing()) {
                    showPopupWindow();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupWindow() {
        popupWindow = new PopupWindow();
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupView = getLayoutInflater().inflate(R.layout.header_popup_window, null);
        popupWindow.setContentView(popupView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        Button btnPhotograph = popupView.findViewById(R.id.m_btn_photograph);
        Button btnGraphSelected = popupView.findViewById(R.id.m_btn_graph_selected);
        Button btnCancel = popupView.findViewById(R.id.m_btn_cancel_select);

        // 选择拍照
        btnPhotograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        btnGraphSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_GRAPH);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        View parent = (View) (this.findViewById(android.R.id.content)).getParent();
        popupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            uploadToServer(baos.toByteArray());
        } else if (requestCode == SELECT_GRAPH && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                uploadToServer(imagePath);
            }
        }
    }

    private void uploadToServer(byte[] bitmapByte) {
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), bitmapByte);
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + P_UPLOAD_PHOTO_PATH)
                .post(body)
                .addHeader("id", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0)))
                .addHeader("status", String.valueOf(PARENT_STATUS))
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
                if (result.equals("1")) {
                    Looper.prepare();
                    Toast.makeText(ParentPhotograph.this, "上传成功", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(ParentPhotograph.this, "上传失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });
    }

    private void uploadToServer(String imagePath) {
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), new File(imagePath));
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + P_UPLOAD_PHOTO_PATH)
                .post(body)
                .addHeader("status", String.valueOf(PARENT_STATUS))
                .addHeader("id", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0)))
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
                if (result.equals("1")) {
                    Looper.prepare();
                    Toast.makeText(ParentPhotograph.this, "上传成功", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(ParentPhotograph.this, "上传失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
