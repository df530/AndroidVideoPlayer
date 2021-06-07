package com.example.avp.model;

import com.example.avp.ui.VideoListSettings;

public class Model {
    public VideoListSettings videoListSettings;

    public Model() {
        videoListSettings = new VideoListSettings();
    }

    public VideoListSettings getVideoListSettings() {
        return videoListSettings;
    }
}
