package com.example.avp.model;

import com.example.avp.ui.LastSeenVideosHolder;
import com.example.avp.ui.VideoListSettings;

import lombok.Getter;

public class Model {
    @Getter
    private VideoListSettings videoListSettings;
    @Getter
    private LastSeenVideosHolder lastSeenVideosHolder;

    public Model() {
        videoListSettings = new VideoListSettings();
        lastSeenVideosHolder = new LastSeenVideosHolder();
    }

    public void updateVideoListSettings(int newColumnsNum, String newSortedBy, boolean newReversedOrder) {
        videoListSettings.columnsNum = newColumnsNum;
        videoListSettings.sortedBy = newSortedBy;
        videoListSettings.reversedOrder = newReversedOrder;
    }

    public void addRecentVideo(String linkOnVideo) {
        lastSeenVideosHolder.addVideo(linkOnVideo);
    }

    /*public VideoListSettings getVideoListSettings() {
        return videoListSettings;
    }*/
}
