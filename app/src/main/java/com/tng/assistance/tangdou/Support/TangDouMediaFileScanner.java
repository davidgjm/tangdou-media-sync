package com.tng.assistance.tangdou.Support;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dagger.hilt.android.scopes.ServiceScoped;

@ServiceScoped
public final class TangDouMediaFileScanner {
    public static final String TAG = TangDouMediaFileScanner.class.getSimpleName();
    private static final Set<File> mediaFiles = new HashSet<>();

    public List<File> scan() {
        doScan();
        return Collections.unmodifiableList(new ArrayList<>(mediaFiles));
    }


    private void doScan() {
        List<File> sourceFolders = loadSourceFolders();
        FileFilter filter =
                FileFilterUtils.suffixFileFilter("mp4", IOCase.INSENSITIVE)
                        .or(FileFilterUtils.suffixFileFilter("mp3", IOCase.INSENSITIVE));
        for (File folder : sourceFolders) {
            if (!folder.exists() || folder.isFile()) {
                Log.i(TAG, "Candidate does not exist or is a file" + folder);
                continue;
            }
            mediaFiles.addAll(Arrays.asList(folder.listFiles(filter)));
        }
        Log.i(TAG, mediaFiles.size() + " media files scanned");
    }

    private List<File> loadSourceFolders() {
        String[] candidates = {
                "CCDownload"
        };

        List<File> sourceFolders = new ArrayList<>();
        for (String candidate :
                candidates) {
            sourceFolders.add(new File(Environment.getExternalStorageDirectory(), candidate));
        }
        return sourceFolders;
    }
}
