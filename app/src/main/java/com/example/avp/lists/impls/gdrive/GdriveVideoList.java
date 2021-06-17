package com.example.avp.lists.impls.gdrive;

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

    public GdriveVideoList(RecyclerView videoListRV, GDriveVideosHolder videosHolder, VideoListSettings listSettings,
                           Set<Constants.DisplayMode> possibleDisplayModes, Fragment parentFragment, GoogleSignInAccount account) {
        super(videoListRV, videosHolder, listSettings, possibleDisplayModes, parentFragment);
        this.account = account;
        gDriveService = new GDriveService(parentFragment.getActivity().getApplicationContext(), account);
    }

    @Override
    protected VideoAdapter createVideoAdapter() {
        List<InfoMenuItem.InfoElement> infoElements = List.of(
                new InfoMenuItem.InfoElement("Name", AVPMediaMetaData::getTitle),
                new InfoMenuItem.InfoElement("Link", AVPMediaMetaData::getLink),
                new InfoMenuItem.InfoElement("Path", AVPMediaMetaData::getPath),
                new InfoMenuItem.InfoElement("Size", meta ->
                        AVPUtils.sizeInMBToString(
                                AVPUtils.sizeFromBytesToMB(
                                        gDriveService.getSizeOfFile(
                                                meta.getLink()
                                        )
                                )
                        )
                ),
                new InfoMenuItem.InfoElement("Duration", AVPMediaMetaData::getDurationString),
                new InfoMenuItem.InfoElement("Date taken", AVPMediaMetaData::getDurationString)
        );
        InfoMenuItem infoMenuItem = new InfoMenuItem(infoElements, parentFragment);
        return new DeviceVideoAdapter(listSettings.displayMode,
                new CustomPopupMenuBuilder(List.of(infoMenuItem)),
                videosHolder,
                parentFragment
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
