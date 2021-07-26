package com.tng.assistance.tangdou.services;

import android.util.Log;

import com.tng.assistance.tangdou.Support.TangDouMediaFileScanner;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.android.scopes.ServiceScoped;

//@ActivityScoped
public class MediaFileSyncService {
    private static final String TAG = "MediaFileSyncService";

    private final SettingsService settingsService;
    private final TangDouMediaFileScanner fileScanner;

    @Inject
    public MediaFileSyncService(SettingsService settingsService, TangDouMediaFileScanner fileScanner) {
        this.settingsService = settingsService;
        this.fileScanner = fileScanner;
    }


    private void setupBaseDir() {
        File baseDir = settingsService.getTargetFolder();
        if (!baseDir.exists()) {
            boolean result = baseDir.mkdir();
            Log.i(TAG, "Attempted to create base dir. Result: " + result);
        }
    }

    public void synchronize() {
        Log.i(TAG, "Attempting to synchronize media files for tang dou");
        File targetFolder = settingsService.getTargetFolder();
        setupBaseDir();
        List<File> mediaFiles = fileScanner.scan();
        doSyncFiles(mediaFiles);
    }

    public ArrayList<String> scanOnly() {
        ArrayList<String> files = new ArrayList<>();
        List<File> mediaFiles = fileScanner.scan();
        for (File f : mediaFiles) {
            files.add(f.getAbsolutePath());
        }
        return files;
    }


    private void doSyncFiles(List<File> sourceFiles) {
        File targetBaseDir = settingsService.getTargetFolder();
        for (File mediaFile :
                sourceFiles) {
            if (mediaFile.isDirectory()) {
                //file is a directory
                continue;
            }
            File target = new File(targetBaseDir, mediaFile.getName());
            Log.d(TAG, String.format("Copying %s to %s", mediaFile.getAbsolutePath(), target.getAbsolutePath()));

            try {
                if (settingsService.isCopyMode()) {
                    FileUtils.copyFile(mediaFile, target);
                } else {
                    FileUtils.moveFile(mediaFile, target);
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy file. " + e.getMessage());
            }

        }
    }
}
