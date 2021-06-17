package com.example.avp.utils;

import com.github.vkay94.dtpv.DoubleTapPlayerView;

import java.io.File;

public class AVPUtils {
    public static String getFileSizeMegaBytes(String path) {
        File file = new File(path);
        Float size = sizeFromBytesToMB(file.length());
        return sizeInMBToString(size);
    }

    public static Float sizeFromBytesToMB(Long bytes) {
        return (float) bytes / (1024 * 1024);
    }

    public static String sizeInMBToString(Float sizeMB) {
        return String.format("%.2f", sizeMB) + " mb";
    }
    public static String getVideoName(String link) {
        String[] parts = link.split(File.separator);
        return parts[parts.length - 1];
    }
}
