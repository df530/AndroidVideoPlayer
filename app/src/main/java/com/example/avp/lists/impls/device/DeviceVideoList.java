package com.example.avp.lists.impls.device;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.lists.VideoAdapter;
import com.example.avp.lists.VideoList;
import com.example.avp.lists.VideoListSettings;
import com.example.avp.lists.VideosHolder;
import com.example.avp.lists.menu.CustomPopupMenuBuilder;
import com.example.avp.lists.menu.InfoMenuItem;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;
import com.example.avp.utils.AVPUtils;
import com.example.avp.utils.StateSaveLoader;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import lombok.NonNull;

public class DeviceVideoList extends VideoList {
    private final View parentView;
    public DeviceVideoList(RecyclerView videoListRV, VideosHolder videosHolder, VideoListSettings listSettings,
                           Set<Constants.DisplayMode> possibleDisplayModes, Fragment parentFragment, Context context,
                           View parentView) {
        super(videoListRV, videosHolder, listSettings, possibleDisplayModes, parentFragment, context);
        this.parentView = parentView;
    }

    @Override
    protected VideoAdapter createVideoAdapter() {
        List<InfoMenuItem.InfoElement> infoElements = List.of(
                new InfoMenuItem.InfoElement("Name", AVPMediaMetaData::getTitle),
                new InfoMenuItem.InfoElement("Path", AVPMediaMetaData::getPath),
                new InfoMenuItem.InfoElement("Size", meta -> AVPUtils.getFileSizeMegaBytes(meta.getPath())),
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

    @Override
    protected @NonNull void fetchVideos() {
        String absolutePath;

        String[] projection = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_TAKEN
        };

        Cursor cursorMedia = parentFragment.getActivity().getApplicationContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                listSettings.sortParam.getForDeviceSort() //+ "DESC"
        );

        int columnIndexData = cursorMedia.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        int durationIndex = cursorMedia.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        int dateTakenIndex = cursorMedia.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);

        while (cursorMedia.moveToNext()) {
            try {
                absolutePath = cursorMedia.getString(columnIndexData);
                String dateTakenString = cursorMedia.getString(dateTakenIndex);
                Date dateTaken = new Date(Long.parseLong(dateTakenString));

                AVPMediaMetaData metaData = new AVPMediaMetaData(
                        AVPUtils.getVideoName(cursorMedia.getString(columnIndexData)),
                        null,
                        absolutePath,
                        null,
                        absolutePath,
                        Long.valueOf(cursorMedia.getString(durationIndex)),
                        dateTaken
                );
                videosHolder.add(metaData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void loadSavedState(StateSaveLoader saveLoader) {
    }

    @Override
    public void saveState(StateSaveLoader saveLoader) {
    }
}
