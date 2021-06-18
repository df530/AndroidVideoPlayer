package com.example.avp.model;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.avp.lists.VideoList;
import com.example.avp.ui.Constants;
import com.example.avp.lists.VideoListSettings;
import com.example.avp.utils.JsonStateSaveLoader;
import com.example.avp.utils.StateSaveLoader;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.example.avp.ui.Constants.videoListSettingsVariableKey;

public class Model implements Serializable {
    @Getter
    @Setter
    private VideoListSettings videoListSettings;
    @Getter
    private final transient StateSaveLoader stateSaveLoader;
    @Getter@Setter
    private transient Activity activity;
    private final transient List<VideoList> videoLists;

    public Model(Activity activity) {
        videoListSettings = new VideoListSettings();
        this.activity = activity;
        videoLists = new ArrayList<>();
        stateSaveLoader = new JsonStateSaveLoader(activity);
    }

    public void updateVideoListSettings(@NonNull Constants.DisplayMode displayMode, @NonNull Constants.SortParam newSortParam, boolean newReversedOrder) {
        // need refactoring
        if (displayMode != videoListSettings.displayMode) {
            videoLists.forEach(vl -> vl.onDisplayModeChanged(displayMode));
            videoListSettings.displayMode = displayMode;
        }
        if (newReversedOrder != videoListSettings.reversedOrder) {
            videoLists.forEach(vl -> vl.onReverseOrderChange(newReversedOrder));
            videoListSettings.reversedOrder = newReversedOrder;
        }
        if (newSortParam != videoListSettings.sortParam) {
            videoLists.forEach(vl -> vl.onSortedByChange(newSortParam));
            videoListSettings.sortParam = newSortParam;
        }
    }

    public void saveState(StateSaveLoader stateSaveLoader) {
        videoLists.forEach(vl -> vl.saveState(stateSaveLoader));
        stateSaveLoader.writeSerializable(videoListSettingsVariableKey, videoListSettings);
    }

    public void loadSavedState(StateSaveLoader stateSaveLoader) {
        if (!stateSaveLoader.isSavedStateExist()) {
            return;
        }
        videoLists.forEach(vl -> vl.loadSavedState(stateSaveLoader));
        videoListSettings = (VideoListSettings)stateSaveLoader.readSerializable(videoListSettingsVariableKey, VideoListSettings.class);
    }

    public void addVideoList(VideoList videoList) {
        videoLists.add(videoList);
    }

    public Context getContext() {
        return getActivity();
    }
}
