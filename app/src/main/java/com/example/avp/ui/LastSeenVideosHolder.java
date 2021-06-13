package com.example.avp.ui;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.example.avp.player.AVPMediaMetaData;

public class LastSeenVideosHolder implements Serializable {
    private final Set<AVPMediaMetaData> lastSeenLinks;
    private final LinkedList<AVPMediaMetaData> lastSeenMetaDataModelList;

    public LastSeenVideosHolder() {
        lastSeenLinks = new HashSet<>();
        lastSeenMetaDataModelList = new LinkedList<>();
    }
    public void addVideo(AVPMediaMetaData metaData) {
        if (lastSeenLinks.contains(metaData)) {
            lastSeenMetaDataModelList.remove(metaData);
        } else {
            lastSeenLinks.add(metaData);
        }
        lastSeenMetaDataModelList.addFirst(metaData);
    }

    @NonNull
    public ArrayList<AVPMediaMetaData> getLastSeenMetaDataModelList() {
        return new ArrayList<>(lastSeenMetaDataModelList);
    }
}
