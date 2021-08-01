package com.tng.assistance.tangdou.recyclerview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tng.assistance.tangdou.R;

import java.io.File;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<TextViewHolder>{
    public static final String TAG = FileListAdapter.class.getSimpleName();
    private final List<File> dataItems;
    private final Resources resources;

    public FileListAdapter(List<File> dataItems, Resources resources) {
        this.dataItems = dataItems;
        this.resources = resources;
    }


    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);

        return new TextViewHolder(view, () -> view.findViewById(R.id.item_text_view));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TextViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        File fileItem = dataItems.get(position);
        Log.d(TAG, "showing file: " + fileItem);
        String fileName = fileItem.getName();

        TextView textView = viewHolder.getContentView();
        textView.setText(fileName);

//        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(fileItem.getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
//        Drawable d = new BitmapDrawable(this.resources, thumbnail);
//        d.setBounds(4,0,0,0);
//        textView.setCompoundDrawablesRelative(d, null, null, null);
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }
}
