package com.tng.assistance.tangdou.Support;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tng.assistance.tangdou.dto.MediaFileSet;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import dagger.hilt.android.scopes.ServiceScoped;

public final class TangDouMediaFileScanner {
    public static final String TAG = TangDouMediaFileScanner.class.getSimpleName();

    public MediaFileSet scan(@NonNull String baseDir) {
        Objects.requireNonNull(baseDir, "Base directory cannot be null!");
        if (baseDir.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid base directory!");
        }
        return doScan(baseDir);
    }

    public MediaFileSet scan(@NonNull File baseDir) {
        Objects.requireNonNull(baseDir, "Base directory cannot be null!");
        return doScan(baseDir);
    }


    private MediaFileSet doScan(File basePath) {
        if (!basePath.exists() || basePath.isFile()) {
            Log.e(TAG, "Invalid base directory: " + basePath);
            throw new IllegalArgumentException("Invalid base directory!");
        }
        Set<File> files = new HashSet<>(Arrays.asList(basePath.listFiles(MEDIA_FILE_FILTER)));

        Log.i(TAG, files.size() + " media files scanned for base dir " + basePath.getPath());
        MediaFileSet fileSet = new MediaFileSet(basePath, files);
        Log.d(TAG, String.format("Scan result for %s. %s", basePath, files));
        return fileSet;
    }

    private MediaFileSet doScan(String baseDir) {
        File basePath = new File(Environment.getExternalStorageDirectory(), baseDir);
        return doScan(basePath);
    }

    private static final FileFilter MEDIA_FILE_FILTER =
            FileFilterUtils.suffixFileFilter("mp4", IOCase.INSENSITIVE)
                    .or(FileFilterUtils.suffixFileFilter("mp3", IOCase.INSENSITIVE))
            ;
}
