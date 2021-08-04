package com.tng.assistance.tangdou;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.hardware.display.DisplayManagerCompat;

import android.annotation.SuppressLint;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DeviceAndApplicationActivity extends AppCompatActivity {

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
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setValues() {
        TextView storageInfoView = findViewById(R.id.storage_info);
        StringBuilder storageInfo = new StringBuilder();
        storageInfo
                .append("External Storage State: ").append(Environment.getExternalStorageState()).append("\n")
                .append("External Storage Removable: ").append(Environment.isExternalStorageRemovable()).append("\n")
                .append("External Storage Emulated: ").append(Environment.isExternalStorageEmulated()).append("\n")
                .append("External Storage Directory: ").append(Environment.getExternalStorageDirectory().getPath()).append("\n")
                .append("Root Directory: ").append(Environment.getRootDirectory()).append("\n")
                .append("Data Directory: ").append(Environment.getDataDirectory()).append("\n")
                .append("Download Cache Directory: ").append(Environment.getDownloadCacheDirectory()).append("\n")
                ;
        storageInfoView.setText(storageInfo);


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
}