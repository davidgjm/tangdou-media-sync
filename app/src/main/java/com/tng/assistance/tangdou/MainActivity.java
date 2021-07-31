package com.tng.assistance.tangdou;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.tng.assistance.tangdou.Support.TangDouMediaFileScanner;
import com.tng.assistance.tangdou.infrastructure.AndroidBus;
import com.tng.assistance.tangdou.recyclerview.FileListAdapter;
import com.tng.assistance.tangdou.recyclerview.RecyclerViewFragment;
import com.tng.assistance.tangdou.services.MediaFileSyncService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import org.reactivestreams.Subscription;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    public static final String[] EXTERNAL_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private Disposable disposable;

    public static final int EXTERNAL_REQUEST = 138;
    Toolbar toolbar;

    @Inject
    MediaFileSyncService syncService;

    @Inject
    AndroidBus androidBus;

    private RecyclerView sourceFileListView;
    private FileListAdapter sourceFileListAdapter;
    private List<String> sourceFiles;

    private RecyclerView targetFileListView;
    private FileListAdapter targetFileListAdapter;
    private List<String> targetFiles;

    private TextView sourceBaseLocation, targetBaseLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestForPermission();


        // Find the toolbar view and set as ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        sourceBaseLocation = findViewById(R.id.sourceBaseDir);
        targetBaseLocation = findViewById(R.id.targetBaseDir);

//        initSourceFileView();
//        initTargetFileView();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RecyclerViewFragment fragment = new RecyclerViewFragment();
            transaction.replace(R.id.sourceFileListView, fragment);
            transaction.commit();
        }
    }


//    private void initSourceFileView() {
//        sourceFileListView = findViewById(R.id.sourceFileListView);
//        sourceFileListView.setLayoutManager(new LinearLayoutManager(this));
//        sourceFileListView.scrollToPosition(0);
//
////        sourceFiles = new ArrayList<>();
////        sourceFileListAdapter = new FileListAdapter(sourceFiles);
////        sourceFileListView.setAdapter(sourceFileListAdapter);
//    }
//
//
//    private void initTargetFileView() {
//        targetFileListView = findViewById(R.id.targetFileListView);
//        targetFileListView.setLayoutManager(new LinearLayoutManager(this));
//        targetFileListView.scrollToPosition(0);
//
//        targetFiles = new ArrayList<>();
//        targetFileListAdapter = new FileListAdapter(targetFiles);
//        targetFileListView.setAdapter(targetFileListAdapter);
//    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void requestForPermission() {

        requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);

    }

    public void showFileList(View view) {
        loadFiles();
//        Intent intent = new Intent(this, DisplayFileListActivity.class);
//        startActivity(intent);
    }

    private void loadFiles() {
        disposable = Observable.fromSupplier(() -> syncService.scanOnly())
                .subscribeOn(Schedulers.io())
                .subscribe(mediaFileSet -> {
                    sourceBaseLocation.setText(mediaFileSet.getBaseDir().getPath());
                    androidBus.publish(mediaFileSet);
                })

        ;

//        sourceFiles = syncService.scanOnly();
//        sourceFiles = new ArrayList<>();
//        sourceFileListAdapter = new FileListAdapter(sourceFiles);
//        sourceFileListView.setAdapter(sourceFileListAdapter);
    }

    public void showSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
