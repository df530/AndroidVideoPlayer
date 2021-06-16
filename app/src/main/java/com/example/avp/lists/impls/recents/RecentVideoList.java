package com.example.avp.lists.impls.recents;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.lists.VideoAdapter;
import com.example.avp.lists.VideoList;
import com.example.avp.lists.VideoListSettings;
import com.example.avp.lists.VideosHolder;
import com.example.avp.lists.menu.CustomPopupMenuBuilder;
import com.example.avp.lists.menu.InfoMenuItem;
import com.example.avp.lists.menu.MenuItem;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;
import com.example.avp.utils.StateSaveLoader;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.core.Observable;
import lombok.NonNull;

import static com.example.avp.ui.Constants.videoListSettingsVariableKey;

public class RecentVideoList extends VideoList {
    private final Fragment parentFragment;

    public RecentVideoList(RecyclerView videoListRV, RecentVideosHolder videosHolder, VideoListSettings listSettings,
                           Set<Constants.DisplayMode> possibleDisplayModes, Fragment parentFragment) {
        super(videoListRV, videosHolder, parentFragment.getContext(), listSettings, possibleDisplayModes);
        this.parentFragment = parentFragment;
    }

    @Override
    protected VideoAdapter createVideoAdapter() {
        return new RecentVideoAdapter(listSettings.displayMode,
                new CustomPopupMenuBuilder(List.of(MenuItem.copyLink)),
                videosHolder,
                parentFragment
        );
    }

    @Override
    protected @NonNull Task<Void> fetchVideos() {
        return Tasks.forResult(null);
    }

    @Override
    public void loadSavedState(StateSaveLoader stateSaveLoader) {
        VideosHolder loadVideoHolder =
                (VideosHolder)stateSaveLoader.readSerializable(videosHolder.getSerializationKey(), RecentVideosHolder.class);
        if (loadVideoHolder != null) {
            videosHolder = loadVideoHolder;
            updateRecycleView();
        }
    }

    @Override
    public void saveState(StateSaveLoader stateSaveLoader) {
        stateSaveLoader.writeSerializable(videosHolder.getSerializationKey(), videosHolder);
    }
}
