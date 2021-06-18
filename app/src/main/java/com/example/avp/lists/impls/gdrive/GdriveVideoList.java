package com.example.avp.lists.impls.gdrive;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.lists.VideoAdapter;
import com.example.avp.lists.VideoList;
import com.example.avp.lists.VideoListSettings;
import com.example.avp.lists.impls.device.DeviceVideoAdapter;
import com.example.avp.lists.menu.CustomPopupMenuBuilder;
import com.example.avp.lists.menu.InfoMenuItem;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;
import com.example.avp.utils.AVPUtils;
import com.example.avp.utils.StateSaveLoader;
import com.gdrive.GDriveService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.List;
import java.util.Set;

import lombok.NonNull;

public class GdriveVideoList extends VideoList {
    private GoogleSignInAccount account;
    private GDriveService gDriveService;
    private View parentView;

    public GdriveVideoList(RecyclerView videoListRV, GDriveVideosHolder videosHolder, VideoListSettings listSettings,
                           Set<Constants.DisplayMode> possibleDisplayModes, Fragment parentFragment, Context context,
                           View parentView, GoogleSignInAccount account) {
        super(videoListRV, videosHolder, listSettings, possibleDisplayModes, parentFragment, context);
        this.account = account;
        gDriveService = new GDriveService(parentFragment.getActivity().getApplicationContext(), account);
        this.parentView = parentView;
    }

    @Override
    protected VideoAdapter createVideoAdapter() {
        List<InfoMenuItem.InfoElement> infoElements = List.of(
                new InfoMenuItem.InfoElement("Name", AVPMediaMetaData::getTitle),
                new InfoMenuItem.InfoElement("Link", AVPMediaMetaData::getLink),
                //new InfoMenuItem.InfoElement("Path", AVPMediaMetaData::getPath),
                /*new InfoMenuItem.InfoElement("Size", meta ->
                        AVPUtils.sizeInMBToString(
                                AVPUtils.sizeFromBytesToMB(
                                        gDriveService.getFile(me)
                                                meta.getLink()
                                        )
                                )
                        )
                ),*/
                new InfoMenuItem.InfoElement("Duration", AVPMediaMetaData::getDurationString),
                new InfoMenuItem.InfoElement("Date taken", AVPMediaMetaData::getDateTakenString)
        );
        InfoMenuItem infoMenuItem = new InfoMenuItem(infoElements, parentView);
        return new DeviceVideoAdapter(listSettings.displayMode,
                new CustomPopupMenuBuilder(List.of(infoMenuItem)),
                videosHolder,
                parentFragment,
                context
        );
    }

    // use in current thread, it will be called not in main thread
    @Override
    protected @NonNull void fetchVideos() {
        List<AVPMediaMetaData> videoList = gDriveService.getUserVideosList();
        videosHolder.addAll(videoList);
    }

    @Override
    public void loadSavedState(StateSaveLoader saveLoader) {
    }

    @Override
    public void saveState(StateSaveLoader saveLoader) {
    }
}
