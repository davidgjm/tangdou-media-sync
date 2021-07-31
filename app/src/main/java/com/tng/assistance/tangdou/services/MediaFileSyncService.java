package com.tng.assistance.tangdou.services;

import android.util.Log;

import com.tng.assistance.tangdou.Support.TangDouMediaFileScanner;
import com.tng.assistance.tangdou.dto.MediaFileSet;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.android.scopes.ServiceScoped;

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

    public final MediaFileSet synchronize() {
        Log.i(TAG, "Attempting to synchronize media files for tang dou");
        File targetFolder = settingsService.getTargetFolder();
        setupBaseDir();
        MediaFileSet fileSet = fileScanner.scan().get(0);
        return doSyncFiles(fileSet);
    }

    public MediaFileSet scanOnly() {
        return fileScanner.scan().get(0);
    }


    private MediaFileSet doSyncFiles(MediaFileSet sourceFileSet) {
        File targetBaseDir = settingsService.getTargetFolder();
        Set<File> targets = new HashSet<>();
        for (File mediaFile : sourceFileSet.getFiles()) {
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
            targets.add(target);
        }
        return new MediaFileSet(targetBaseDir, targets);
    }
}
