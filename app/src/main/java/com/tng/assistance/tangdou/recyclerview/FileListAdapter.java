package com.tng.assistance.tangdou.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tng.assistance.tangdou.R;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class FileListAdapter extends RecyclerView.Adapter<TextViewHolder>{
    public static final String TAG = FileListAdapter.class.getSimpleName();
    private List<String> dataItems;

    public FileListAdapter(List<String> dataItems) {
        this.dataItems = dataItems;
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
        String item = dataItems.get(position);
        Log.d(TAG, "showing item text: " + item);
        viewHolder.getContentView().setText(item);

    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }
}
