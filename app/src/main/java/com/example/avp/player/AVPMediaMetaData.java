package com.example.avp.player;

public class AVPMediaMetaData {
    private final String title;
    private final String author;
    private final String uri;
    private final String previewURL;

    public AVPMediaMetaData(String title, String author, String uri, String previewURL) {
        this.title = title;
        this.author = author;
        this.uri = uri;
        this.previewURL = previewURL;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getUri() {
        return uri;
    }

    public String getPreviewURL() {
        return previewURL;
    }
}
