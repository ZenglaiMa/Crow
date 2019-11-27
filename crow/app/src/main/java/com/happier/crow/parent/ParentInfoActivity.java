package com.happier.crow.parent;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.happier.crow.MainActivity;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;
import com.happier.crow.entities.Parent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParentInfoActivity extends AppCompatActivity {

    private ImageView ivHeader;
    private TextView tvParentName;
    private TextView tvParentGender;
    private TextView tvParentAge;
    private TextView tvParentAddress;
    private Button btnModifyParentInfo;
    private Button btnParentLogout;

    private PopupWindow popupWindow = null;
    private View popupView = null;

    private static final String PARENT_PERSONAL_INFO_PATH = "/parent/getInfo";

    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_SELECT_GRAPH = 200;

    private static final String UPLOAD_PATH = "/parent/uploadHeaderImage";

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_info);

        EventBus.getDefault().register(this);

        findViews();

        getInfo();

        // 头像
        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow == null || !popupWindow.isShowing()) {
                    // 显示PopupWindow
                    showPopupWindow();
                }
            }
        });

        // 设置/修改个人信息
        btnModifyParentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ModifyParentInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        // 退出登录
        btnParentLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
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
                startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            }
        });

        btnGraphSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_SELECT_GRAPH);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(ivHeader);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            RequestOptions options = new RequestOptions().circleCrop();
            Glide.with(this).load(bitmap).apply(options).into(ivHeader);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            uploadToServer(baos.toByteArray());
        } else if (requestCode == REQUEST_CODE_SELECT_GRAPH && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                RequestOptions options = new RequestOptions().circleCrop();
                Glide.with(this).load(imagePath).apply(options).into(ivHeader);
                uploadToServer(imagePath);
            }
        }
    }

    private void uploadToServer(byte[] bitmapByte) {
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), bitmapByte);
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + UPLOAD_PATH)
                .post(body)
                .addHeader("pid", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0)))
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
                Log.e("upload", Integer.parseInt(result) == 1 ? "success" : "fail");
            }
        });
    }

    private void uploadToServer(String imagePath) {
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), new File(imagePath));
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + UPLOAD_PATH)
                .post(body)
                .addHeader("pid", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0)))
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
                Log.e("upload", Integer.parseInt(result) == 1 ? "success" : "fail");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfo();
    }

    private void getInfo() {
        int pid = getSharedPreferences("authid", MODE_PRIVATE).getInt("pid", 0);
        FormBody body = new FormBody.Builder()
                .add("pid", String.valueOf(pid))
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + PARENT_PERSONAL_INFO_PATH)
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
                EventBus.getDefault().post(result);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleResult(String result) {
        Gson gson = new Gson();
        Parent parent = gson.fromJson(result, Parent.class);
        if (parent.getName() != null && !parent.getName().equals("")) {
            tvParentName.setText(parent.getName());
        }
        tvParentGender.setText(parent.getGender() == 1 ? "男" : "女");
        if (parent.getAge() != 0) {
            tvParentAge.setText(String.valueOf(parent.getAge()));
        }
        if (parent.getProvince() != null && parent.getCity() != null &&
                parent.getArea() != null && parent.getDetailAddress() != null) {
            String address = parent.getProvince() + parent.getCity() + parent.getArea() + parent.getDetailAddress();
            tvParentAddress.setText(address);
        }
        if (parent.getIconPath() != null && !parent.getIconPath().equals("")) {
            RequestOptions options = new RequestOptions().circleCrop();
            Glide.with(this)
                    .load(Constant.BASE_URL + parent.getIconPath())
                    .apply(options)
                    .into(ivHeader);
        }
    }

    private void findViews() {
        ivHeader = findViewById(R.id.m_iv_parent_header);
        tvParentName = findViewById(R.id.m_tv_parent_name);
        tvParentGender = findViewById(R.id.m_tv_parent_gender);
        tvParentAddress = findViewById(R.id.m_tv_parent_address);
        tvParentAge = findViewById(R.id.m_tv_parent_age);
        btnModifyParentInfo = findViewById(R.id.m_btn_modify_parent_info);
        btnParentLogout = findViewById(R.id.m_btn_parent_logout);
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
