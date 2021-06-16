package com.example.avp.lists;

import com.example.avp.ui.Constants;

import java.io.Serializable;

public class VideoListSettings implements Serializable {
    public Constants.SortParam sortParam = Constants.SortParam.DATE_TAKEN;
    public Constants.DisplayMode displayMode = Constants.DisplayMode.GALLERY;
    public boolean reversedOrder = false;

    public VideoListSettings() {}

    public VideoListSettings(VideoListSettings another) {
        sortParam = another.sortParam;
        displayMode = another.displayMode;
        reversedOrder = another.reversedOrder;
    }
}
