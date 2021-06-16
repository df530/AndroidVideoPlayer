package com.example.avp.lists.impls.recents;

import com.example.avp.lists.VideosHolder;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/* if we will add removing from list, we will have to make some cardinal changes
 * may be it works long in some moments, we will change it in the future
 */
public class RecentVideosHolder implements VideosHolder {
    // singletone
    private static final RecentVideosHolder instance = new RecentVideosHolder();
    private RecentVideosHolder() {};
    public static RecentVideosHolder getInstance() {
        return instance;
    }

    private final Set<AVPMediaMetaData> recentVideosSet = new HashSet<>();
    private final LinkedList<AVPMediaMetaData> recentVideoLinkedList = new LinkedList<>();

    @Override
    public AVPMediaMetaData getVideoMetaDataByPositionInList(int position) {
        return recentVideoLinkedList.get(recentVideoLinkedList.size() - 1 - position);
    }

    @Override
    public int getSize() {
        return recentVideoLinkedList.size();
    }

    @Override
    public void add(AVPMediaMetaData metaData) {
        if (!recentVideosSet.add(metaData)) {
            recentVideoLinkedList.remove(metaData);
        }
        recentVideoLinkedList.addFirst(metaData);
    }

    @Override
    public boolean reverse() {
        return false;
    }

    @Override
    public boolean sortBy(Constants.SortParam sortParam) {
        return false;
    }
}
