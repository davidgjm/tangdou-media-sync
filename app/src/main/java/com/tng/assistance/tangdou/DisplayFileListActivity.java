package com.tng.assistance.tangdou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.tng.assistance.tangdou.infrastructure.AndroidBus;
import com.tng.assistance.tangdou.services.MediaFileSyncService;

import java.util.ArrayList;
import java.util.StringJoiner;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Predicate;

@AndroidEntryPoint
public class DisplayFileListActivity extends AppCompatActivity {
    private static final String TAG = DisplayFileListActivity.class.getSimpleName();
    TextView externalStorageInfo;
    TextView mediaFileList;

    @Inject
    AndroidBus androidBus;

    @Inject
    MediaFileSyncService syncService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file_list);
        externalStorageInfo = findViewById(R.id.externalStorageInfo);

        String storageInfo = String.format("Simulated? %s, State: %s", Environment.isExternalStorageEmulated(), Environment.getExternalStorageState());
        externalStorageInfo.setText(storageInfo);
        mediaFileList = findViewById(R.id.file_list);
        renderMediaFiles();
    }


    private void renderMediaFiles() {
        androidBus.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Throwable {
                ArrayList<String> files = (ArrayList<String>) o;
                mediaFileList.setText("");
                Log.i(TAG, "Received files: " + files);
                for (String file : files) {
                    mediaFileList.append(file + "\n");
                }

            }
        }, fileListFilter);
    }

    private static Predicate<Object> fileListFilter = o -> o instanceof ArrayList;
}
