package com.tng.assistance.tangdou.services;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.tng.assistance.tangdou.Support.DocumentFileUtil;
import com.tng.assistance.tangdou.Support.TangDouMediaFileScanner;
import com.tng.assistance.tangdou.dto.DocumentFileSet;
import com.tng.assistance.tangdou.dto.MediaFileSet;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

public class MediaFileSyncService {
    private static final String TAG = MediaFileSyncService.class.getSimpleName();

    private final Context context;
    private final SettingsService settingsService;
    private final FileScanService fileScanService;

    @Inject
    public MediaFileSyncService(Application application, SettingsService settingsService, FileScanService fileScanService) {
        this.context = application.getApplicationContext();
        this.settingsService = settingsService;
        this.fileScanService = fileScanService;
    }

    public DocumentFileSet scanFiles(@NonNull DocumentFile directory, boolean isTargetFiles) {
        Log.i(TAG, String.format("Scanning files(target=%s) for directory %s", isTargetFiles, directory.getUri()));
        List<DocumentFile> files = fileScanService.scan(directory);
        DocumentFileSet result = new DocumentFileSet(directory, files);
        result.setTargetFiles(isTargetFiles);
        return result;
    }

    @NonNull
    public DocumentFileSet scanSourceFiles() {
        Uri uri = settingsService.getSourceDataRoot();
        DocumentFile dir = DocumentFile.fromTreeUri(context, uri);
        if (dir == null) {
            dir = DocumentFile.fromFile(settingsService.getSourceFolder());
        }
        return scanFiles(dir, false);
    }

    @NonNull
    public DocumentFileSet scanTargetFiles(DocumentFile targetDir) {
        if (targetDir == null) {
            Log.w(TAG, "scanTargetFiles: target base dir is null");
            return DocumentFileSet.EMPTY;
        }
        return scanFiles(targetDir, true);
    }

    @NonNull
    public DocumentFileSet scanTargetFiles() {
        DocumentFile dir = getTargetBaseDir();
        if (dir == null) {
            Log.w(TAG, "scanTargetFiles: target base dir is null");
            return DocumentFileSet.EMPTY;
        }
        return scanTargetFiles(dir);
    }


    public final DocumentFileSet synchronize() {
        Log.i(TAG, "Attempting to synchronize media files for tang dou");
        DocumentFileSet fileSet = scanSourceFiles();
        return doSyncFiles(fileSet);
    }


    private DocumentFileSet doSyncFiles(DocumentFileSet sourceFileSet) {
        DocumentFile targetBaseDir = getTargetBaseDir();
        List<DocumentFile> targets = sourceFileSet.getFiles().stream()
                .filter(DocumentFile::isFile)
                .map(f -> DocumentFileUtil.copyToDirectory(context, f, targetBaseDir))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        DocumentFileSet result = new DocumentFileSet(targetBaseDir, targets);
        result.setTargetFiles(true);
        return result;
    }

    public DocumentFile getTargetBaseDir(@NonNull DocumentFile root, boolean createIfNotPresent) {
        String targetFolder = settingsService.getTargetFolderName();
        DocumentFile baseDir = root.findFile(targetFolder);
        if (createIfNotPresent && baseDir == null) {
            baseDir = root.createDirectory(targetFolder);
        }
        if (baseDir != null) {
            Log.i(TAG, "Current target base dir: " + baseDir.getUri().getPath());
        }
        return baseDir;
    }

    @RequiresApi(21)
    public DocumentFile getTargetBaseDir() {
        Uri rootUri = settingsService.getTargetDataRoot();
        DocumentFile root = DocumentFile.fromTreeUri(context, rootUri);
        return getTargetBaseDir(root, false);
    }
}
