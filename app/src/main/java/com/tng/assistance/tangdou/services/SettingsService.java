package com.tng.assistance.tangdou.services;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tng.assistance.tangdou.R;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;


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


    public File getSourceFolder() {
        String basePath = preferences.getString(resources.getString(R.string.prefs_key_source_folder), resources.getString(R.string.default_source_folder));
        Log.d(TAG, "Source folder name: "+ basePath);
        return new File(Environment.getExternalStorageDirectory(), basePath);
    }


    public String getTargetFolderName() {
        String folderName = preferences.getString(resources.getString(R.string.prefs_key_target_folder), resources.getString(R.string.default_target_folder));
        Log.d(TAG, "Target folder name: "+ folderName);
        return folderName;
    }

    public Uri getSourceDataRoot() {
        String sourceRoot = preferences.getString(resources.getString(R.string.prefs_key_source_root_uri), resources.getString(R.string.default_source_root_uri));
        return Uri.parse(sourceRoot);
    }

    public void setSourceDataRoot(Uri sourceDataRoot) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(resources.getString(R.string.prefs_key_source_root_uri), sourceDataRoot.toString());
        editor.apply();
    }

    public Uri getTargetDataRoot() {
        String targetRoot = preferences.getString(resources.getString(R.string.prefs_key_target_root_uri), resources.getString(R.string.default_target_root_uri));
        return Uri.parse(targetRoot);
    }

    public void setTargetDataRoot(Uri targetDataRoot) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(resources.getString(R.string.prefs_key_target_root_uri), targetDataRoot.toString());
        editor.apply();
    }

    public boolean isEmulatedSDCardSupported() {
        return preferences.getBoolean(resources.getString(R.string.prefs_key_emulated_mode), resources.getBoolean(R.bool.default_support_emulated_mode));
    }


    public boolean isCopyMode() {
        return preferences.getBoolean(resources.getString(R.string.prefs_key_copy_mode), resources.getBoolean(R.bool.default_copy_mode));
    }

    public Set<String> getMediaTypes() {
        String[] array = resources.getStringArray(R.array.media_types);
        Set<String> defaultValues = new HashSet<>(Arrays.asList(array));
        return preferences.getStringSet(resources.getString(R.string.prefs_key_media_type), defaultValues);
    }
}
