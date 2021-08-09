package com.tng.assistance.tangdou.services;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.tng.assistance.tangdou.dto.DocumentFileSet;

import java.util.List;

public interface FileScanService {

    List<DocumentFile> scan(@NonNull DocumentFile baseDir);
}
