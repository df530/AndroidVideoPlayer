package com.example.avp.lists.impls.gdrive;

import com.example.avp.lists.impls.SimpleVideoHolder;

public class GDriveVideosHolder extends SimpleVideoHolder {
    // singletone implementation
    private static final GDriveVideosHolder instance = new GDriveVideosHolder();
    private GDriveVideosHolder() {
    }
    public static GDriveVideosHolder getInstance() {
        return instance;
    }
}