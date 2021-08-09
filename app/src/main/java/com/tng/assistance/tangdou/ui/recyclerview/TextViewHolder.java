package com.tng.assistance.tangdou.ui.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Supplier;


public class TextViewHolder extends GenericViewHolder<TextView> {
    private final TextView textView;

    public TextViewHolder(@NonNull View itemView, @NonNull Supplier<TextView> viewSupplier) {
        super(itemView);
        this.textView=viewSupplier.get();
    }


    @Override
    public TextView getContentView() {
        return textView;
    }
}
