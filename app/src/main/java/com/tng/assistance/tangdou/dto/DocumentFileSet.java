package com.tng.assistance.tangdou.dto;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public final class DocumentFileSet {
    private final DocumentFile baseDir;
    private final List<DocumentFile> files;
    private boolean isTargetFiles;

    public DocumentFileSet(DocumentFile baseDir, DocumentFile... files) {
        this.baseDir = baseDir;
        this.files = Arrays.asList(files);
    }

    public DocumentFileSet(DocumentFile baseDir) {
        this.baseDir = baseDir;
        this.files = new ArrayList<>();
    }

    public List<DocumentFile> getFiles() {
        if (files.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(files);
    }
    public boolean isEmpty() {
        return files.isEmpty();
    }


    public static DocumentFileSet EMPTY = new DocumentFileSet(null);
}
