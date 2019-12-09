package com.happier.crow.children.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.happier.crow.ChatActivity;
import com.happier.crow.R;
import com.happier.crow.children.ChildrenPhotograph;
import com.happier.crow.children.ChildrenSetAlarmActivity;
import com.happier.crow.constant.Constant;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class InteractFragment extends Fragment {

    private LinearLayout llSetNotify;
    private LinearLayout llUploadPicture;
    private LinearLayout llLookPicture;
    private LinearLayout llChat;

    private PopupWindow popupWindow = null;
    private View popupView = null;

    private static final int TAKE_PHOTO = 1;
    private static final int SELECT_GRAPH = 2;

    private static final int CHILDREN_STATUS = 1;

    private static final String C_UPLOAD_PATH = "/image/uploadPhoto";

    private OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interact, container, false);

        setHasOptionsMenu(true);
        findViews(view);

        setListener();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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
                    if (popupWindow == null || !popupWindow.isShowing()) {
                        showPopupWindow();
                    }
                    break;
                case R.id.m_ll_look_picture:
                    Intent uploadIntent = new Intent(getActivity(), ChildrenPhotograph.class);
                    startActivity(uploadIntent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.m_ll_chat:
                    EMClient.getInstance().login(Constant.USER_L, "123456", new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Log.e("success", "login success");
                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                            intent.putExtra(EaseConstant.EXTRA_USER_ID, Constant.GROUP_ID);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        }

                        @Override
                        public void onError(int code, String error) {
                            Log.e("error", "login error, error code is " + code);
                        }

                        @Override
                        public void onProgress(int progress, String status) {
                        }
                    });
                    break;
            }
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
        View parent = (View) (getActivity().findViewById(android.R.id.content)).getParent();
        popupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            uploadToServer(baos.toByteArray());
        } else if (requestCode == SELECT_GRAPH && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                uploadToServer(imagePath);
            }
        }
    }

    private void uploadToServer(byte[] bitmapByte) {
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), bitmapByte);
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + C_UPLOAD_PATH)
                .post(body)
                .addHeader("id", String.valueOf(getContext().getSharedPreferences("authid", MODE_PRIVATE).getInt("cid", 0)))
                .addHeader("status", String.valueOf(CHILDREN_STATUS))
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
                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });
    }

    private void uploadToServer(String imagePath) {
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), new File(imagePath));
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + C_UPLOAD_PATH)
                .post(body)
                .addHeader("status", String.valueOf(CHILDREN_STATUS))
                .addHeader("id", String.valueOf(getContext().getSharedPreferences("authid", MODE_PRIVATE).getInt("cid", 0)))
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
                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });
    }
}
