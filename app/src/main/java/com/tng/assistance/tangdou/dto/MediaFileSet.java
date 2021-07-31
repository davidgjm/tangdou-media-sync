package com.tng.assistance.tangdou.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public final class MediaFileSet {
    private final File baseDir;
    private final Set<File> files;


    public Set<File> getFiles() {
        return Collections.unmodifiableSet(files);
    }

    public List<File> asList() {
        if (files.isEmpty()) {
            return Collections.emptyList();
        }
        List<File> items = new ArrayList<>(files);
        Collections.sort(items);
        return Collections.unmodifiableList(items);
    }
}
