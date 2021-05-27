package com.example.avp.ui;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;


public class LastSeenVideosHolder {
    private final Set<String> lastSeenLinks;
    private final LinkedList<String> lastSeenLinkModelList;

    public LastSeenVideosHolder(LinkedList<String> lastSeenLinkModelList) {
        this.lastSeenLinks = new HashSet<>(lastSeenLinkModelList);
        this.lastSeenLinkModelList = lastSeenLinkModelList;
    }

    public void addVideo(String link) {
        if (lastSeenLinks.contains(link)) {
            lastSeenLinkModelList.remove(link);
        } else {
            lastSeenLinks.add(link);
        }
        lastSeenLinkModelList.addFirst(link);
    }

    @NonNull
    public ArrayList<String> getLastSeenLinkModelList() {
        return new ArrayList<>(lastSeenLinkModelList);
    }
}
