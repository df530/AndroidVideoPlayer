package com.example.avp.utils;

import com.github.vkay94.dtpv.DoubleTapPlayerView;

import java.io.File;

public class AVPUtils {
    public static String getFileSizeMegaBytes(String path) {
        File file = new File(path);
        Float size = (float) file.length() / (1024 * 1024);
        return String.format("%.2f", size) + " mb";
    }

    public static String getVideoName(String link) {
        String[] parts = link.split(File.separator);
        return parts[parts.length - 1];
    }
}
