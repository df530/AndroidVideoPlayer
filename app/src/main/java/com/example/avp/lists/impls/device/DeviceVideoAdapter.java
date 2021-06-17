package com.example.avp.lists.impls.device;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.avp.lists.VideoAdapter;
import com.example.avp.lists.VideosHolder;
import com.example.avp.lists.menu.CustomPopupMenuBuilder;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.player.ExoPlayerActivity;
import com.example.avp.ui.Constants;

public class DeviceVideoAdapter extends VideoAdapter {
    private final Fragment parentFragment;

    public DeviceVideoAdapter(Constants.DisplayMode displayMode, CustomPopupMenuBuilder popupMenuBuilder, VideosHolder videosHolder,
                              Fragment parentFragment) {
        super(displayMode, popupMenuBuilder, videosHolder);
        this.parentFragment = parentFragment;
    }

    @Override
    protected void onClickItemListener(View v, AVPMediaMetaData metaData) {
        ExoPlayerActivity.startExoPlayerFromFragment(parentFragment, metaData.getLink());
    }

    @Override
    protected String getTextForPathTV(AVPMediaMetaData metaData) {
        return metaData.getPath();
    }

    @Override
    protected String getTextForLinkTV(AVPMediaMetaData metaData) {
        return null;
    }
}