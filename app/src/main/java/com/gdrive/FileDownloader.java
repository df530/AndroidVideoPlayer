package com.gdrive;

import java.io.IOException;
import java.io.OutputStream;

public interface FileDownloader {
    OutputStream downloadFullFile(String fileURL) throws IOException;
    OutputStream downloadPartOfFile(String fileURL, long leftByteBound, long rightByteBound) throws IOException;
}
