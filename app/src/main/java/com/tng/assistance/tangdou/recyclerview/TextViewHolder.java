package com.tng.assistance.tangdou.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Supplier;

import com.tng.assistance.tangdou.R;


public class TextViewHolder extends GenericViewHolder<TextView> {
    private final TextView textView;

    public TextViewHolder(@NonNull View itemView, @NonNull Supplier<TextView> viewSupplier) {
        super(itemView);
//        this.textView = itemView.findViewById(R.id.item_text_view);
        this.textView=viewSupplier.get();
    }


    @Override
    TextView getContentView() {
        return textView;
    }
}
