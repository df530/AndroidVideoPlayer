package com.example.avp.lists.impls;

import com.example.avp.lists.VideosHolder;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

public class SimpleVideoHolder implements VideosHolder {
    private LinkedList<AVPMediaMetaData> deviceVideoList = new LinkedList<>();

    @Override
    public AVPMediaMetaData getVideoMetaDataByPositionInList(int position) {
        return deviceVideoList.get(position);
    }

    @Override
    public int getSize() {
        return deviceVideoList.size();
    }

    @Override
    public void add(AVPMediaMetaData metaData) {
        deviceVideoList.addFirst(metaData);
    }

    @Override
    public boolean reverse() {
        if (getSize() > 1) {
            Collections.reverse(deviceVideoList);
            return true;
        }
        return false;
    }

    @Override
    public boolean sortBy(Constants.SortParam sortParam) {
        if (sortParam == Constants.SortParam.DATE_TAKEN) {
            Collections.sort(deviceVideoList, (a, b) -> Objects.compare(a.getDateTaken(), b.getDateTaken(), Date::compareTo));
        }
        else if (sortParam == Constants.SortParam.NAME) {
            Collections.sort(deviceVideoList, (a, b) -> Objects.compare(a.getTitle(), b.getTitle(), String::compareTo));
        }
        return true;
    }
}
