package com.tng.assistance.tangdou.Support;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tng.assistance.tangdou.dto.MediaFileSet;
import com.tng.assistance.tangdou.services.SettingsService;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ServiceScoped;

public final class TangDouMediaFileScanner {
    public static final String TAG = TangDouMediaFileScanner.class.getSimpleName();
    private final SettingsService settingsService;

    @Inject
    public TangDouMediaFileScanner(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

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
        FileFilter filter = buildFilter();
        List<File> files = new ArrayList<>(Arrays.asList(basePath.listFiles(filter)));
        Collections.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        Log.i(TAG, files.size() + " media files scanned for base dir " + basePath.getPath());
        MediaFileSet fileSet = new MediaFileSet(basePath, files);
        Log.d(TAG, String.format("Scan result for %s. %s", basePath, files));
        return fileSet;
    }

    private MediaFileSet doScan(String baseDir) {
        File basePath = new File(Environment.getExternalStorageDirectory(), baseDir);
        return doScan(basePath);
    }


    private FileFilter buildFilter() {
        Set<String> mediaTypes = settingsService.getMediaTypes();
        List<FileFilter> filters = new ArrayList<>();
        for (String mediaType : mediaTypes) {
            filters.add(FileFilterUtils.suffixFileFilter(mediaType, IOCase.INSENSITIVE));
        }

        return FileFilterUtils.or(filters.toArray(new IOFileFilter[0]));
    }
}
