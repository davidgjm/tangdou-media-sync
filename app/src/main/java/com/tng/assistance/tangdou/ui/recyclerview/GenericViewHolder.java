package com.tng.assistance.tangdou.ui.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class GenericViewHolder<V extends View> extends RecyclerView.ViewHolder {
    public GenericViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract V getContentView();
}
