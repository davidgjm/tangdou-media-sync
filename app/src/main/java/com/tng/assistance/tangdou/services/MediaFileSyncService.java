package com.tng.assistance.tangdou.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.tng.assistance.tangdou.Support.TangDouMediaFileScanner;
import com.tng.assistance.tangdou.dto.MediaFileSet;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class MediaFileSyncService {
    private static final String TAG = MediaFileSyncService.class.getSimpleName();

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

    @NonNull
    public MediaFileSet scanSourceFiles() {
        File basePath = settingsService.getSourceFolder();
        MediaFileSet result = null;
        try {
            result = fileScanner.scan(basePath);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Source file scan error! " + e.getMessage());
            result = new MediaFileSet(basePath);
        }
        result.setTargetFiles(false);
        return result;
    }

    @NonNull
    public MediaFileSet scanTargetFiles() {
        File basePath = settingsService.getTargetFolder();
        MediaFileSet result = null;
        try {
            result = fileScanner.scan(basePath);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Target file scan error! " + e.getMessage());
            Set<File> testFiles = new HashSet<>();
            result = new MediaFileSet(basePath, testFiles);
        }
        result.setTargetFiles(true);
        return result;
    }


    public final MediaFileSet synchronize() {
        Log.i(TAG, "Attempting to synchronize media files for tang dou");
        setupBaseDir();
        MediaFileSet fileSet =scanSourceFiles();
        return doSyncFiles(fileSet);
    }



    private MediaFileSet doSyncFiles(MediaFileSet sourceFileSet) {
        File targetBaseDir = settingsService.getTargetFolder();
        Set<File> targets = new HashSet<>();
        for (File mediaFile : sourceFileSet.getFiles()) {
            if (mediaFile.isDirectory()) {
                //file is a directory
                Log.w(TAG, mediaFile.getName() + " is a directory!");
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
            targets.add(target);
        }
        MediaFileSet result = new MediaFileSet(targetBaseDir, targets);
        result.setTargetFiles(true);
        return result;
    }


    private static final String SOURCE_BASE_PATH = "CCDownload";
}
