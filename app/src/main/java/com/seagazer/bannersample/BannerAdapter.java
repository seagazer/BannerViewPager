package com.seagazer.bannersample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String[] textData = {"TEXT_1", "TEXT_2"};
    private int[] imageData = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3,
            R.drawable.banner4, R.drawable.banner5};
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_IMAGE = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXT) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_text, parent, false);
            return new TextHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_image, parent, false);
            return new ImageHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_TEXT) {
            ((TextHolder) holder).textView.setText(textData[position]);
        }
        if (getItemViewType(position) == TYPE_IMAGE) {
            ((ImageHolder) holder).imageView.setImageResource(imageData[position - 2]);
        }
    }

    @Override
    public int getItemCount() {
        return textData.length + imageData.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 2) {
            return TYPE_TEXT;
        }
        return TYPE_IMAGE;
    }

    class TextHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TextHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        ImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }

}
