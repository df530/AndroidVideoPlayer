package com.gdrive;

import java.io.IOException;
import java.io.InputStream;

public interface FileDownloader {
    InputStream downloadFullFile(String fileURL) throws IOException;
    InputStream downloadPartOfFile(String fileURL, long leftByteBound, long rightByteBound) throws IOException;
}
