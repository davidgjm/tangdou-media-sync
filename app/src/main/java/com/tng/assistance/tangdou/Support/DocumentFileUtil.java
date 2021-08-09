package com.tng.assistance.tangdou.Support;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public final class DocumentFileUtil {
    public static final String TAG = DocumentFileUtil.class.getSimpleName();
    private DocumentFileUtil() {
        throw new AssertionError("Unexpected call to default constructor");
    }


    public static DocumentFile copyToDirectory(Context context, DocumentFile file, DocumentFile dir) {
        Objects.requireNonNull(file, "File cannot be null!");
        Objects.requireNonNull(dir, "Dir cannot be null!");
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            Log.e(TAG, "copyToDirectory: file is invalid!");
            return null;
        }
        if (!dir.exists() || !dir.isDirectory()) {
            Log.e(TAG, "copyToDirectory: dir invalid");
            return null;
        }
        Uri uri = file.getUri();
        String fileName = file.getName();
        String mimeType = file.getType();


        DocumentFile replica = dir.findFile(fileName);
        if (replica != null) {
            Log.w(TAG, "copyToDirectory: target file already exists! " + replica.getName() );
            return null;
        }

        replica = dir.createFile(mimeType, fileName);
        Log.i(TAG, String.format("copyToDirectory: Copying file %s to %s", replica.getUri(), dir.getUri()));
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            fis = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            os = context.getContentResolver().openOutputStream(replica.getUri());
            int bytes = IOUtils.copy(fis, os);
            Log.d(TAG, "copyToDirectory: bytes copied " + bytes);

            fis.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "copyToDirectory: ");
        }

        return replica;
    }
}
