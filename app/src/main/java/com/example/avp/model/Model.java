package com.example.avp.model;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.adapter.VideoAdapter;
import com.example.avp.ui.LastSeenVideosHolder;
import com.example.avp.ui.VideoListSettings;

import java.util.ArrayList;
import java.util.Collections;

import lombok.Getter;
import lombok.Setter;

public class Model {
    @Getter
    private VideoListSettings videoListSettings;
    @Getter
    private LastSeenVideosHolder lastSeenVideosHolder;
    @Getter
    @Setter
    private ArrayList<VideoModel> arrayListVideos;
    private Activity activity;

    public Model(Activity activity) {
        videoListSettings = new VideoListSettings();
        lastSeenVideosHolder = new LastSeenVideosHolder();
        this.activity = activity;
    }

    public void updateVideoListSettings(int newColumnsNum, String newSortedBy, boolean newReversedOrder) {
        if (newColumnsNum == videoListSettings.columnsNum
                && newSortedBy.equals(videoListSettings.sortedBy)
                && newReversedOrder == videoListSettings.reversedOrder) {
            return;
        }
        videoListSettings.columnsNum = newColumnsNum;
        videoListSettings.sortedBy = newSortedBy;
        videoListSettings.reversedOrder = newReversedOrder;
    }

    public void addRecentVideo(String linkOnVideo) {
        lastSeenVideosHolder.addVideo(linkOnVideo);
    }

    private void reverseVideoList() {
        Collections.reverse(getArrayListVideos());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void updateVideoList(RecyclerView recyclerView) {
        ArrayList<VideoModel> newArrayListVideos = new ArrayList<>();
        fetchVideosFromGallery(newArrayListVideos, recyclerView);
        setArrayListVideos(newArrayListVideos);
        if (getVideoListSettings().reversedOrder) {
            reverseVideoList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void fetchVideosFromGallery(ArrayList<VideoModel> newArrayListVideos, RecyclerView recyclerView) {
        int columnIndexData, thum;
        String absolutePathImage;

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA
        };

        String sortOrder = getVideoListSettings().sortedBy;

        Cursor cursor = activity.getApplicationContext().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                sortOrder //+ "DESC"
        );

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathImage = cursor.getString(columnIndexData);

            VideoModel videoModel = new VideoModel();
            videoModel.setBooleanSelected(false);
            videoModel.setStrPath(absolutePathImage);
            videoModel.setStrThumb(cursor.getString(thum));

            newArrayListVideos.add(videoModel);
        }

        //call the com.example.avp.adapter class and set it to recyclerview

        VideoAdapter videoAdapter = new VideoAdapter(activity.getApplicationContext(),
                newArrayListVideos, activity, getVideoListSettings().displayMode);
        recyclerView.setAdapter(videoAdapter);
    }
}
