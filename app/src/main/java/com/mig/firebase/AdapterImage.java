package com.mig.firebase;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class AdapterImage extends RecyclerView.ViewHolder {
    public AdapterImage(@NonNull View itemView) {
        super(itemView);
    }

    void setDisplayImage(String imageURL, Context context){
        ImageView image = itemView.findViewById(R.id.gambar);
        Glide.with(context).load(imageURL).into(image);
    }
}
