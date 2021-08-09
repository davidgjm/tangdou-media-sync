package com.tng.assistance.tangdou.ui.recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.tng.assistance.tangdou.R;

import java.io.IOException;
import java.util.List;

public class DocumentFileListAdapter extends RecyclerView.Adapter<TextViewHolder>{
    public static final String TAG = DocumentFileListAdapter.class.getSimpleName();
    private final List<DocumentFile> dataItems;
    private final Context context;
    private final Resources resources;

    public DocumentFileListAdapter(List<DocumentFile> dataItems, @NonNull Context context, Resources resources) {
        this.dataItems = dataItems;
        this.context = context;
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
        DocumentFile fileItem = dataItems.get(position);
        Log.d(TAG, "showing file: " + fileItem.getUri().getPath());
        String fileName = fileItem.getName();

        TextView textView = viewHolder.getContentView();
        textView.setText(fileName);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            try {
//                Bitmap thumbnail = context.getContentResolver().loadThumbnail(fileItem.getUri(), Size.parseSize("32x32"), new CancellationSignal());
//                Drawable d = new BitmapDrawable(this.resources, thumbnail);
//                d.setBounds(4,0,0,0);
//        //        textView.setCompoundDrawablesRelative(d, null, null, null);
//            } catch (IOException e) {
//                Log.e(TAG, "onBindViewHolder: failed to load thumbnail", e);
//            }
//        }


    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }
}
