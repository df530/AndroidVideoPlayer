package com.gdrive;

import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;

public interface FileDownloader {
    Task<byte[]> downloadFullFile(String fileURL);
    Task<InputStream> downloadPartOfFile(String fileURL, long leftByteBound, long rightByteBound);
}
