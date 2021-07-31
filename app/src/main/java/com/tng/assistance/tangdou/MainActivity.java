package com.tng.assistance.tangdou;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.tng.assistance.tangdou.dto.DataSetFilter;
import com.tng.assistance.tangdou.dto.MediaFileSet;
import com.tng.assistance.tangdou.infrastructure.AndroidBus;
import com.tng.assistance.tangdou.recyclerview.FileListAdapter;
import com.tng.assistance.tangdou.recyclerview.RecyclerViewFragment;
import com.tng.assistance.tangdou.services.MediaFileSyncService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
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


    private TextView sourceBaseLocation, targetBaseLocation;
    private ImageButton syncButton;

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
        syncButton = findViewById(R.id.syncBtn);
        syncButton.setOnClickListener(this::syncFiles);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RecyclerViewFragment sourceListFragment = buildSourceListFragment();
            transaction.replace(R.id.sourceFileListView, sourceListFragment);

            RecyclerViewFragment targetListFragment = buildTargetListFragment();
            transaction.replace(R.id.targetFileListView, targetListFragment);
            transaction.commit();
        }
    }

    private RecyclerViewFragment buildSourceListFragment() {
        DataSetFilter<MediaFileSet> filter = new DataSetFilter<MediaFileSet>(MediaFileSet.class, o -> {
            if (o == null) {
                return false;
            }
            if (!(o instanceof MediaFileSet)) {
                return false;
            }
            MediaFileSet fileSet = (MediaFileSet) o;
            return !fileSet.isTargetFiles();
        });
        return new RecyclerViewFragment(filter);
    }

    private RecyclerViewFragment buildTargetListFragment() {
        DataSetFilter<MediaFileSet> filter = new DataSetFilter<MediaFileSet>(MediaFileSet.class, o -> {
            if (o == null) {
                return false;
            }
            if (!(o instanceof MediaFileSet)) {
                return false;
            }
            MediaFileSet fileSet = (MediaFileSet) o;
            return fileSet.isTargetFiles();
        });
        return new RecyclerViewFragment(filter);
    }


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

    public void scanMediaFiles(View view) {
        disposable = Observable.fromArray(syncService.scanSourceFiles(), syncService.scanTargetFiles())
                .subscribeOn(Schedulers.io())
                .subscribe(mediaFileSet -> {
                    if (mediaFileSet.isTargetFiles()) {
                        targetBaseLocation.setText(mediaFileSet.getBaseDir().getPath());
                    } else {
                        sourceBaseLocation.setText(mediaFileSet.getBaseDir().getPath());
                    }
                    androidBus.publish(mediaFileSet);
                })

        ;
    }

    public void syncFiles(View view) {
        Observable.just(syncService.synchronize())
                .subscribeOn(Schedulers.io())
                .filter(MediaFileSet::isTargetFiles)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaFileSet -> {
                    if (!mediaFileSet.isEmpty()) {
                        androidBus.publish(mediaFileSet);
                        showMessage(view, getResources().getString(R.string.message_sync_successful));
                    } else {
                        showMessage(view, getResources().getString(R.string.message_no_files_found));
                    }
                })
        ;
    }


    public void showSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showMessage(View view, CharSequence message) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_ok, v -> {
                })
                .setAnchorView(targetBaseLocation)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .show();
    }
}
