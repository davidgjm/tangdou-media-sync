package com.tng.assistance.tangdou;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.tng.assistance.tangdou.Support.DocumentFileUtil;
import com.tng.assistance.tangdou.dto.DataSetFilter;
import com.tng.assistance.tangdou.dto.DocumentFileSet;
import com.tng.assistance.tangdou.dto.MediaFileSet;
import com.tng.assistance.tangdou.infrastructure.AndroidBus;
import com.tng.assistance.tangdou.services.FileScanService;
import com.tng.assistance.tangdou.services.MediaFileSyncService;
import com.tng.assistance.tangdou.services.SettingsService;
import com.tng.assistance.tangdou.ui.DeviceAndApplicationActivity;
import com.tng.assistance.tangdou.ui.recyclerview.RecyclerViewFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Supplier;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import java.io.FileNotFoundException;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
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

    @Inject
    FileScanService fileScanService;

    @Inject
    SettingsService settingsService;


    private TextView sourceBaseLocation, targetBaseLocation;
    private ImageButton syncButton;

    private DocumentFile sourceDir;
    private DocumentFileSet sourceFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestForPermission();

        initToolbar();

        sourceBaseLocation = findViewById(R.id.sourceBaseDir);
        targetBaseLocation = findViewById(R.id.targetBaseDir);
        syncButton = findViewById(R.id.syncBtn);
        syncButton.setOnClickListener(this::syncFiles);

        ImageButton scanTargetButton = findViewById(R.id.findTargetDir);
        scanTargetButton.setOnClickListener(this::scanTargetFiles);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment sourceListFragment = buildSourceListFragment();
            transaction.replace(R.id.sourceFileListView, sourceListFragment);

            Fragment targetListFragment = buildTargetListFragment();
            transaction.replace(R.id.targetFileListView, targetListFragment);

            transaction.commit();
        }

        //scan media files on startup
        doScanMediaFiles(() -> syncService.scanSourceFiles(), ()->syncService.scanTargetFiles());
    }

    private void initToolbar() {
        // Find the toolbar view and set as ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.drawable.baseline_groups_white_48dp);

    }

    private Fragment buildSourceListFragment() {
        DataSetFilter<DocumentFileSet> filter = new DataSetFilter<>(DocumentFileSet.class, o -> {
            if (o == null) {
                return false;
            }
            if (!(o instanceof DocumentFileSet)) {
                return false;
            }
            DocumentFileSet fileSet = (DocumentFileSet) o;
            return !fileSet.isTargetFiles();
        });
        return new RecyclerViewFragment(filter);
    }

    private Fragment buildTargetListFragment() {
        DataSetFilter<DocumentFileSet> filter = new DataSetFilter<>(DocumentFileSet.class, o -> {
            if (o == null) {
                return false;
            }
            if (!(o instanceof DocumentFileSet)) {
                return false;
            }
            DocumentFileSet fileSet = (DocumentFileSet) o;
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettings(item);
                return true;
            case R.id.action_about:
                showAbout(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SneakyThrows
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        Log.i(TAG, "Received an \"Activity Result\"");
        // BEGIN_INCLUDE (parse_open_document_response)
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                persistUriPermissionGrant(resultData, uri);
                Log.i(TAG, "source Uri: " + uri.toString());
                sourceBaseLocation.setText(uri.toString());
                settingsService.setSourceDataRoot(uri);
                sourceDir = DocumentFile.fromTreeUri(this, uri);
                doScanMediaFiles(() -> syncService.scanFiles(sourceDir, false));
            }
            // END_INCLUDE (parse_open_document_response)
        }

        //TARGET files
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE_TARGET && resultCode == Activity.RESULT_OK) {
            Uri rootUri = null;
            if (resultData != null) {
                rootUri = resultData.getData();
                Log.i(TAG, "target Uri: " + rootUri.toString());
                persistUriPermissionGrant(resultData, rootUri);
                settingsService.setTargetDataRoot(rootUri);

                DocumentFile rootDir = DocumentFile.fromTreeUri(this, rootUri);
                DocumentFile targetDir = syncService.getTargetBaseDir(rootDir, true);

                targetBaseLocation.setText(targetDir.getUri().getPath());
                doScanMediaFiles(() -> syncService.scanTargetFiles(targetDir));
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void persistUriPermissionGrant(Intent intent, Uri uri) {
        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
// Check for the freshest data.
        getContentResolver().takePersistableUriPermission(uri, takeFlags);
    }

    public void requestForPermission() {

        requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);

    }

    public void scanMediaFiles(View view) {
        Uri sourceRoot = settingsService.getSourceDataRoot();
        DocumentFile sourceDir=DocumentFile.fromTreeUri(this, sourceRoot);

        if (sourceDir != null && sourceDir.exists() && sourceDir.canRead()) {
            doScanMediaFiles(() -> syncService.scanSourceFiles());
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, sourceRoot);
            startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
        }
    }

    public void scanTargetFiles(View view) {
        DocumentFile targetBaseDir = syncService.getTargetBaseDir();
        if (targetBaseDir == null || !targetBaseDir.exists()) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, settingsService.getSourceDataRoot());
            startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE_TARGET);
        } else {
            targetBaseLocation.setText(targetBaseDir.getUri().getPath());
            doScanMediaFiles(() -> syncService.scanTargetFiles());
        }
    }

    public void syncFiles(View view) {
        DocumentFile targetBaseDir = syncService.getTargetBaseDir();
        if (targetBaseDir == null) {
            Uri rootUri = settingsService.getTargetDataRoot();
            DocumentFile root = DocumentFile.fromTreeUri(this, rootUri);
            targetBaseDir = root.createDirectory(settingsService.getTargetFolderName());
        }
        Observable.just(syncService.synchronize())
                .subscribeOn(Schedulers.io())
                .filter(DocumentFileSet::isTargetFiles)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileSet -> {
                    if (!fileSet.isEmpty()) {
                        doScanMediaFiles(() -> syncService.scanTargetFiles());
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

    public void showAbout(MenuItem item) {
        Intent intent = new Intent(this, DeviceAndApplicationActivity.class);
        startActivity(intent);
    }

    @SafeVarargs
    private final void doScanMediaFiles(Supplier<DocumentFileSet>... suppliers) {
        disposable = Observable.fromArray(suppliers)
                .map(Supplier::get)
                .filter(s -> !s.isEmpty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileSet -> {
                    Log.i(TAG, String.format("Received %d %s files", fileSet.getFiles().size(), fileSet.isTargetFiles() ? "target" : "source"));
                    if (fileSet.isTargetFiles()) {
                        targetBaseLocation.setText(fileSet.getBaseDir().getUri().getPath());
                    } else {
                        sourceBaseLocation.setText(fileSet.getBaseDir().getUri().getPath());
                    }
                    androidBus.publish(fileSet);
                })

        ;
    }

    private void showMessage(View view, CharSequence message) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_ok, v -> {
                })
                .setAnchorView(targetBaseLocation)
//                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .show();

//
//        AlertDialog alertDialog= new MaterialAlertDialogBuilder(this)
//                .setMessage(message)
//                .setTitle("DONE")
//                .setIcon(R.drawable.baseline_done_black_48dp)
//                .setNeutralButton("OK",null)
//                .show();
    }


    public static final int OPEN_DIRECTORY_REQUEST_CODE = 0xff10;
    public static final int OPEN_DIRECTORY_REQUEST_CODE_TARGET = 0xff11;
}
