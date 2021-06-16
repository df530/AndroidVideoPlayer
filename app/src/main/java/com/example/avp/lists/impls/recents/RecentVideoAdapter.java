package com.example.avp.lists.impls.recents;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.avp.lists.VideoAdapter;
import com.example.avp.lists.VideosHolder;
import com.example.avp.lists.menu.CustomPopupMenuBuilder;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.player.ExoPlayerActivity;
import com.example.avp.ui.Constants;

public class RecentVideoAdapter extends VideoAdapter {
    private final Fragment parentFragment;

    public RecentVideoAdapter(Constants.DisplayMode displayMode, CustomPopupMenuBuilder popupMenuBuilder, VideosHolder videosHolder, Fragment parentFragment) {
        super(displayMode, popupMenuBuilder, videosHolder);
        this.parentFragment = parentFragment;
    }

    @Override
    protected void onClickItemListener(View v, AVPMediaMetaData metaData) {
        ExoPlayerActivity.startExoPlayerFromFragmentForResult(parentFragment, metaData.getLink(), 1);
    }

    @Override
    protected String getTextForPathTV(AVPMediaMetaData mediaMetaData) {
        return null;
    }

    @Override
    protected String getTextForLinkTV(AVPMediaMetaData mediaMetaData) {
        return mediaMetaData.getLink();
    }
}
