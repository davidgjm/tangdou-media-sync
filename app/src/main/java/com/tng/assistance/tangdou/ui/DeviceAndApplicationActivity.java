package com.tng.assistance.tangdou.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tng.assistance.tangdou.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class DeviceAndApplicationActivity extends AppCompatActivity {
    public static final String TAG = DeviceAndApplicationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_and_application);

        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        setValues();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this::back);

        scanSDCard();
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setValues() {
        DisplayMetrics realMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(realMetrics);
        TextView screenInfoView = findViewById(R.id.screen_info);
        StringBuilder screenInfo = new StringBuilder();
        screenInfo
                .append("Height(px): ").append(realMetrics.heightPixels).append("\n")
                .append("Width(px): ").append(realMetrics.widthPixels).append("\n")
                .append("density: ").append(realMetrics.density).append("\n")
                .append("Density DPI: ").append(realMetrics.densityDpi).append("\n")
                .append("X DPI: ").append(realMetrics.xdpi).append("\n")
                .append("Y DPI: ").append(realMetrics.ydpi).append("\n")
        ;
        screenInfoView.setText(screenInfo);

        TextView screenSize = findViewById(R.id.release_info);
        StringBuilder releaseInfo = new StringBuilder();
        releaseInfo
                .append("Version: ").append(Build.VERSION.RELEASE).append("\n")
                .append("SDK: ").append(Build.VERSION.SDK_INT).append("\n")
                .append("INCREMENTAL: ").append(Build.VERSION.INCREMENTAL).append("\n")
                .append("SECURITY_PATCH: ").append(Build.VERSION.SECURITY_PATCH).append("\n")
        ;
        screenSize.setText(releaseInfo);

        TextView productInfo = findViewById(R.id.product_info);
        StringBuilder productText = new StringBuilder();
        productText
                .append("Brand: ").append(Build.BRAND).append("\n")
                .append("Model: ").append(Build.MODEL).append("\n")
                .append("Manufacture: ").append(Build.MANUFACTURER).append("\n")
                .append("Product: ").append(Build.PRODUCT).append("\n")
                .append("Device: ").append(Build.DEVICE).append("\n")
                .append("ID: ").append(Build.ID).append("\n")
                .append("Build ID: ").append(Build.DISPLAY).append("\n")
                .append("HARDWARE: ").append(Build.HARDWARE).append("\n")
                .append("BOARD: ").append(Build.BOARD).append("\n")

        ;
        productInfo.setText(productText);
    }


    public void back(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }


    private void scanSDCard() {
        File externalDir = getExternalFilesDir(null);
        Log.i(TAG, "External File dir: " + externalDir);
        File[] files = new File("/storage").listFiles();
        for (File f : files) {
            boolean isRemovable = false;
            try {
                isRemovable = Environment.isExternalStorageRemovable(f);
            } catch (RuntimeException e) {
                Log.e(TAG, "RuntimeException: " + e);
            }
            Log.i(TAG, String.format("Root folder %s removable: %s, writable=%s%n", f, isRemovable, f.canWrite()));
        }

        File[] externalDirs = getExternalFilesDirs(null);
        for (File f : externalDirs) {
            boolean isRemovable = false;
            try {
                isRemovable = Environment.isExternalStorageRemovable(f);
            } catch (RuntimeException e) {
                Log.e(TAG, "RuntimeException: " + e);
            }
            Log.i(TAG, String.format("External dir %s removable: %s, writable=%s%n", f, isRemovable, f.canWrite()));
        }


        File externalStorageDir = Environment.getExternalStorageDirectory();

        Log.i(TAG, String.format("externalStorageDir %s removable: %s%n", externalStorageDir, Environment.isExternalStorageRemovable(externalStorageDir)));

        File sdCardDir = new File("/storage/1518-3305");
        Log.i(TAG, String.format("SD card path %s, writeable: %s%n", sdCardDir, sdCardDir.canWrite()));

        File targetDir = new File(sdCardDir, "00-media-files");
        Path targetBaseDir = Paths.get(sdCardDir.getPath(), "My-media-files", "00-media-files");
        Path result = null;
        try {
            result = Files.createDirectory(targetBaseDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null) {

            Log.i(TAG, String.format("SD card path %s created: %s%n", targetDir, result));
        }


        StorageManager storageManager = getSystemService(StorageManager.class);
        List<StorageVolume> volumes = storageManager.getStorageVolumes();

        for (StorageVolume volume : volumes) {
            Log.i(TAG, String.format("Storage Volume: %s, uuid=%s, removable=%s, primary=%s, emulated=%s, state=%s%n",
                    volume.getDescription(this),
                    volume.getUuid(),
                    volume.isRemovable(),
                    volume.isPrimary(),
                    volume.isEmulated(),
                    volume.getState()
            ));
//            volume.writeToParcel();
        }

//        getContentResolver().

        Optional<StorageVolume> volumeOptional = getSecondaryStorage();
        volumeOptional.ifPresent(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra(StorageVolume.EXTRA_STORAGE_VOLUME, v);
        });
    }


    private Optional<StorageVolume> getSecondaryStorage() {
        StorageManager storageManager = getSystemService(StorageManager.class);
        List<StorageVolume> volumes = storageManager.getStorageVolumes();
        for (StorageVolume volume : volumes) {
            Log.d(TAG, String.format("Storage Volume: %s, uuid=%s, removable=%s, primary=%s, emulated=%s, state=%s, string=%s%n",
                    volume.getDescription(this),
                    volume.getUuid(),
                    volume.isRemovable(),
                    volume.isPrimary(),
                    volume.isEmulated(),
                    volume.getState(),
                    volume.toString()
            ));
//            volume.writeToParcel();
            if (!volume.isPrimary() && volume.isRemovable() && volume.getState().equals(Environment.MEDIA_MOUNTED)) {
                return Optional.of(volume);
            }
        }

        return Optional.empty();
    }


    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                Log.i(TAG, "result uri: " + uri.getPath());
            }
        }
    }


    public static final int REQUEST_CODE = 0x00ff;
}