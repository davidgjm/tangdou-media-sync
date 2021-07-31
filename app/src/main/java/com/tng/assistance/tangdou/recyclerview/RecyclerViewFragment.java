package com.tng.assistance.tangdou.recyclerview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tng.assistance.tangdou.R;
import com.tng.assistance.tangdou.dto.MediaFileSet;
import com.tng.assistance.tangdou.infrastructure.AndroidBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.functions.Predicate;

@AndroidEntryPoint
public class RecyclerViewFragment extends Fragment {
    public static final String TAG = RecyclerViewFragment.class.getSimpleName();
    @Inject
    AndroidBus androidBus;

    private RecyclerView recyclerView;
    private FileListAdapter fileListAdapter;
    private List<String> dataSet;

    @SuppressLint("NotifyDataSetChanged")
    @SuppressWarnings({"unchecked"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataSet = new ArrayList<>();
        androidBus.subscribe(fileListFilter, o -> {
            MediaFileSet fileSet = (MediaFileSet) o;
            for (File f : fileSet.getFiles()) {
                if (!dataSet.contains(f)) {
                    dataSet.add(f.getName());
                    fileListAdapter.notifyItemInserted(dataSet.size()-1);
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

        fileListAdapter = new FileListAdapter(dataSet);
        recyclerView.setAdapter(fileListAdapter);


        return rootView;
    }

    private static final Predicate<Object> fileListFilter = o -> o instanceof MediaFileSet;

}
