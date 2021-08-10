package com.tng.assistance.tangdou.services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

public class FileScanServiceImpl implements FileScanService {
    public static final String TAG = FileScanServiceImpl.class.getSimpleName();
    private final SettingsService settingsService;

    @Inject
    public FileScanServiceImpl(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Override
    public List<DocumentFile> scan(@NonNull DocumentFile directory) {
        Objects.requireNonNull(directory, "Base URI cannot be null!");
        Log.i(TAG, String.format("Scanning files for directory %s", directory.getUri()));

        if (!directory.isDirectory() || !directory.exists() || !directory.canRead()) {
            Log.e(TAG, "Provided DocumentFile is invalid directory");
            return new ArrayList<>();
        }
        Set<String> supportedTypes = settingsService.getMediaTypes();
        return Stream.of(directory.listFiles())
                .filter(DocumentFile::isFile)
                .filter(f -> supportedTypes.contains(f.getType()))
                .peek(f -> Log.i(TAG, "scan: file "+ f.getUri().getPath()))
                .collect(Collectors.toList())
                ;
    }
}
