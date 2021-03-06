package com.tng.assistance.tangdou.ui.recyclerview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tng.assistance.tangdou.R;
import com.tng.assistance.tangdou.dto.DataSetFilter;
import com.tng.assistance.tangdou.dto.DocumentFileSet;
import com.tng.assistance.tangdou.dto.MediaFileSet;
import com.tng.assistance.tangdou.infrastructure.AndroidBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecyclerViewFragment extends Fragment {
    public static final String TAG = RecyclerViewFragment.class.getSimpleName();
    private final DataSetFilter<DocumentFileSet> dataSetFilter;

    @Inject
    AndroidBus androidBus;

    private RecyclerView recyclerView;
    private DocumentFileListAdapter fileListAdapter;
    private final List<DocumentFile> dataSet = new ArrayList<>();

    public RecyclerViewFragment(DataSetFilter<DocumentFileSet> dataSetFilter) {
        this.dataSetFilter = dataSetFilter;
    }

    @SuppressLint("NotifyDataSetChanged")
    @SuppressWarnings({"unchecked"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidBus.subscribe(dataSetFilter.getFilter(), o -> {
            DocumentFileSet fileSet = dataSetFilter.getDataType().cast(o);
            ;
            Objects.requireNonNull(fileSet, "Media file set is null!");

            boolean changed = dataSet.retainAll(fileSet.getFiles());
            if (changed) {
                fileListAdapter.notifyDataSetChanged();
            }
            for (DocumentFile f : fileSet.getFiles()) {
                if (!dataSet.contains(f)) {
                    dataSet.add(f);
                    fileListAdapter.notifyItemInserted(dataSet.size() - 1);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        //initializing recycler view
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.scrollToPosition(0);

        fileListAdapter = new DocumentFileListAdapter(dataSet, getContext(), getResources());
        recyclerView.setAdapter(fileListAdapter);


        return rootView;
    }

}
