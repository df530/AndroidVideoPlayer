package com.example.avp.ui;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.example.avp.model.LastSeenMetaDataModel;
import com.example.avp.player.AVPMediaMetaData;

import lombok.Getter;

public class LastSeenVideosHolder {
    private final Set<AVPMediaMetaData> lastSeenLinks = new HashSet<>();
    private final LinkedList<LastSeenMetaDataModel> lastSeenMetaDataModelList = new LinkedList<>();

    public void addVideo(AVPMediaMetaData metaData) {
        LastSeenMetaDataModel model = new LastSeenMetaDataModel(metaData);
        if (lastSeenLinks.contains(metaData)) {
            lastSeenMetaDataModelList.remove(model);
        } else {
            lastSeenLinks.add(metaData);
        }
        lastSeenMetaDataModelList.addFirst(model);
    }

    @NonNull
    public ArrayList<LastSeenMetaDataModel> getLastSeenMetaDataModelList() {
        return new ArrayList<>(lastSeenMetaDataModelList);
    }
}
