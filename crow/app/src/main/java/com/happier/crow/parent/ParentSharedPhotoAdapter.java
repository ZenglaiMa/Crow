package com.happier.crow.parent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.happier.crow.R;
import com.happier.crow.constant.Constant;

import java.util.List;

public class ParentSharedPhotoAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<String> photoPaths;
    private int itemId;

    public ParentSharedPhotoAdapter(Context context, List<String> photoPaths, int itemId) {
        this.context = context;
        this.photoPaths = photoPaths;
        this.itemId = itemId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(itemId, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        String path = photoPaths.get(i);
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int height = screenHeight;
        int width = screenWidth;
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.m_placeholder)
                .error(R.drawable.m_error)
                .fallback(R.drawable.m_fallback)
                .override(width, height);
        Glide.with(context)
                .load(Constant.BASE_URL + path)
                .apply(options)
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        if (photoPaths != null) {
            return photoPaths.size();
        }
        return 0;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPhoto;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.m_iv_shared_photo);
        }
    }
}
