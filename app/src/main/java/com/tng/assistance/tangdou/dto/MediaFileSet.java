package com.tng.assistance.tangdou.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public final class MediaFileSet {
    private final File baseDir;
    private final List<File> files;
    private boolean isTargetFiles;

    public MediaFileSet(File baseDir) {
        this.baseDir = baseDir;
        this.files = new ArrayList<>();
    }

    public List<File> getFiles() {
        if (files.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(files);
    }
    public boolean isEmpty() {
        return files.isEmpty();
    }
}
