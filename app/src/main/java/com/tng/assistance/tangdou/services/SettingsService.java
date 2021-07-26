package com.tng.assistance.tangdou.services;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tng.assistance.tangdou.R;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ServiceScoped;


public class SettingsService {
    public static final String TAG = SettingsService.class.getSimpleName();
    private final Context context;
    private final SharedPreferences preferences;
    private final Resources resources;

    @Inject
    public SettingsService(Application application) {
        this.context = application.getApplicationContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.resources = context.getResources();
    }


    public File getTargetFolder() {
        String folderName = preferences.getString(resources.getString(R.string.prefs_settings_target_folder_name), resources.getString(R.string.default_target_folder));
        Log.d(TAG, "Target folder name: "+ folderName);
        return new File(Environment.getExternalStorageDirectory(), folderName);
    }


    public boolean isEmulatedSDCardSupported() {
        return preferences.getBoolean(resources.getString(R.string.prefs_emulated_mode_support), resources.getBoolean(R.bool.default_support_emulated_mode));
    }


    public boolean isCopyMode() {
        return preferences.getBoolean(resources.getString(R.string.prefs_settings_copy_mode), resources.getBoolean(R.bool.default_copy_mode));
    }

}
