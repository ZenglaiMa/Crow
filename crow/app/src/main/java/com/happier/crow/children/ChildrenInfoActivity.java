package com.happier.crow.children;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;

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

public class ChildrenInfoActivity extends AppCompatActivity {

    private ImageView ivHeader;
    private EditText etName;
    private EditText etAge;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private EditText etProfile;
    private Button btnSet;

    private PopupWindow popupWindow = null;
    private View popupView = null;

    private String name;
    private int gender;
    private String age;
    private String profile;

    private OkHttpClient client = new OkHttpClient();

    private static final String SET_CHILDREN_INFO_PATH = "/children/setInfo";
    private static final String UPLOAD_PATH = "/children/uploadHeaderImage";

    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_SELECT_GRAPH = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_info);
        getSupportActionBar().setElevation(0);

        EventBus.getDefault().register(this);

        findViews();

        setDefaultValues();

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                age = etAge.getText().toString();
                gender = rbMale.isChecked() ? 1 : 0;
                profile = etProfile.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(age)) {
                        if (!TextUtils.isEmpty(profile)) {
                            setPersonalInfo();
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入个人简介", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请输入年龄", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请输入姓名", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow == null || !popupWindow.isShowing()) {
                    showPopupWindow();
                }
            }
        });
    }

    private void setDefaultValues() {
        Intent intent = getIntent();
        String path = intent.getStringExtra("headerImagePath");
        String name = intent.getStringExtra("name");
        int age = intent.getIntExtra("age", 0);
        int gender = intent.getIntExtra("gender", -1);
        String profile = intent.getStringExtra("profile");
        if (path != null && !path.equals("")) {
            RequestOptions options = new RequestOptions().circleCrop();
            Glide.with(this).load(Constant.BASE_URL + path).apply(options).into(ivHeader);
        }
        if (name != null && !name.equals("")) {
            etName.setText(intent.getStringExtra("name"));
        }
        if (age != 0) {
            etAge.setText(String.valueOf(age));
        }
        if (gender != -1) {
            if (gender == 0) {
                rbFemale.setChecked(true);
            } else if (gender == 1) {
                rbMale.setChecked(true);
            }
        }
        if (profile != null && !profile.equals("")) {
            etProfile.setText(profile);
        }
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
        View parent = (View) (this.findViewById(android.R.id.content)).getParent();
        popupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                .addHeader("cid", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("cid", 0)))
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
                Log.e("take a photo", result);
            }
        });
    }

    private void uploadToServer(String imagePath) {
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), new File(imagePath));
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + UPLOAD_PATH)
                .post(body)
                .addHeader("cid", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("cid", 0)))
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
                Log.e("select from photograph", result);
            }
        });
    }

    private void setPersonalInfo() {
        FormBody body = new FormBody.Builder()
                .add("name", name)
                .add("age", age)
                .add("gender", String.valueOf(gender))
                .add("profile", profile)
                .build();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + SET_CHILDREN_INFO_PATH)
                .addHeader("cid", String.valueOf(getSharedPreferences("authid", MODE_PRIVATE).getInt("cid", 0)))
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
        if (result.equals("1")) {
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void findViews() {
        ivHeader = findViewById(R.id.m_iv_children_header_info);
        etName = findViewById(R.id.m_et_children_set_name);
        etAge = findViewById(R.id.m_et_children_set_age);
        rbMale = findViewById(R.id.m_rb_children_gender_male);
        rbFemale = findViewById(R.id.m_rb_children_gender_female);
        etProfile = findViewById(R.id.m_et_children_profile);
        btnSet = findViewById(R.id.m_btn_children_set_info);
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
