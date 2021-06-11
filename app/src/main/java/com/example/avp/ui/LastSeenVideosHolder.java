package com.example.avp.ui;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.example.avp.model.LastSeenLinkModel;

public class LastSeenVideosHolder implements Serializable {
    private Set<String> lastSeenLinks = new HashSet<>();
    private LinkedList<LastSeenLinkModel> lastSeenLinkModelList = new LinkedList<>();

    public void addVideo(String link) {
        LastSeenLinkModel model = new LastSeenLinkModel(link);
        if (lastSeenLinks.contains(link)) {
            lastSeenLinkModelList.remove(model);
        } else {
            lastSeenLinks.add(link);
        }
        lastSeenLinkModelList.addFirst(model);
    }

    @NonNull
    public ArrayList<LastSeenLinkModel> getLastSeenLinkModelList() {
        return new ArrayList<>(lastSeenLinkModelList);
    }
}
