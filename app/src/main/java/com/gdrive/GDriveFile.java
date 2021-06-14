package com.gdrive;

import java.io.InputStream;

import lombok.Getter;
import lombok.Setter;

public class GDriveFile {
    @Getter @Setter
    private InputStream stream;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String author;
    @Getter @Setter
    private String previewURL;
    @Getter @Setter
    private long size;

    public GDriveFile(InputStream stream, String title, String author, String previewURL, long size) {
        this.stream = stream;
        this.title = title;
        this.author = author;
        this.previewURL = previewURL;
        this.size = size;
    }
}
