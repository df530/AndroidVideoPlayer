package com.example.avp.model;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.avp.ui.LastSeenVideosHolder;
import com.example.avp.ui.VideoListSettings;

import java.util.ArrayList;
import java.util.Collections;

import lombok.Getter;

public class Model {
    private VideoListSettings videoListSettings;
    private LastSeenVideosHolder lastSeenVideosHolder;
    @Getter
    private ArrayList<VideoModel> arrayListVideos;
    private final Activity activity;
    private String currentVideoLink;

    public Model(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        fetchVideosFromGallery();
        if (videoListSettings.reversedOrder) {
            reverseVideoList();
        }
    }


    public void addRecentVideo(String linkOnVideo) {
        lastSeenVideosHolder.addVideo(linkOnVideo);
    }

    public String getVideoPath(int i) {
        return arrayListVideos.get(i).getPath();
    }

    private void reverseVideoList() {
        Collections.reverse(arrayListVideos);
    }

//    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void fetchVideosFromGallery() {
        int column_index_data, thum;
        String absolutePathImage;

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA
        };

        String sortOrder = videoListSettings.sortedBy;

        Cursor cursor = activity.getApplicationContext().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                sortOrder //+ "DESC"
        );

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathImage = cursor.getString(column_index_data);

            VideoModel videoModel = new VideoModel();
            videoModel.setBooleanSelected(false);
            videoModel.setPath(absolutePathImage);
            videoModel.setThumb(cursor.getString(thum));

            arrayListVideos.add(videoModel);
        }
    }

    public String getVideListDisplayMode() {
        return videoListSettings.displayMode;
    }

    public String getVideoThumb(int i) {
        return arrayListVideos.get(i).getThumb();
    }

    public void setCurrentVideFromList(int position) {
        currentVideoLink = getVideoPath(position);
    }

    public String getRecentLink(int position) {
        return lastSeenVideosHolder.getLastSeenLinkModelList().get(position);
    }

    public int getLastSeenLinksCount() {
        return lastSeenVideosHolder.getLastSeenLinkModelList().size();
    }

    public void updateVideoListColumnsNum(int columnsNum) {
        videoListSettings.columnsNum = columnsNum;
    }

    public void updateVideoListSortedBy(String sortedBy) {
        videoListSettings.sortedBy = sortedBy;
    }

    public void updateVideoListDisplayMode(String displayMode) {
        videoListSettings.displayMode = displayMode;
    }

    public void setVideoListViewList() {
        updateVideoListColumnsNum(1);
        videoListSettings.displayMode = "list";
    }

    public void setVideoListViewGallery() {
        updateVideoListColumnsNum(2);
        videoListSettings.displayMode = "gallery";
    }

    public boolean getVideoListReversed() {
        return videoListSettings.reversedOrder;
    }

    public void toggleVideoListOrder() {
        videoListSettings.reversedOrder = !videoListSettings.reversedOrder;
    }

    public int getVideoListColumnsNum() {
        return videoListSettings.columnsNum;
    }
}
