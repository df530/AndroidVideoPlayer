package com.example.avp.lists.impls.device;

import androidx.annotation.ColorLong;

import com.example.avp.lists.VideosHolder;
import com.example.avp.lists.impls.SimpleVideoHolder;
import com.example.avp.lists.impls.recents.RecentVideosHolder;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DeviceVideosHolder extends SimpleVideoHolder {
    // singletone implementation
    private static final DeviceVideosHolder instance = new DeviceVideosHolder();

    private DeviceVideosHolder() {
    }

    public static DeviceVideosHolder getInstance() {
        return instance;
    }
}
