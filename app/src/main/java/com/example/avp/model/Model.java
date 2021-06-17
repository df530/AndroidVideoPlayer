package com.example.avp.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.adapter.VideoAdapter;
import com.example.avp.lists.VideoList;
import com.example.avp.ui.Constants;
import com.example.avp.lists.VideoListSettings;
import com.example.avp.utils.JsonStateSaveLoader;
import com.example.avp.utils.StateSaveLoader;
import com.gdrive.GDriveService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import static com.example.avp.ui.Constants.videoListSettingsVariableKey;

public class Model implements Serializable {
    @Getter
    @Setter
    private VideoListSettings videoListSettings;
    @Getter
    @Setter
    private ArrayList<VideoModel> arrayListVideos;
    @Getter
    private final transient StateSaveLoader stateSaveLoader;
    @Getter@Setter
    private transient Activity activity;
    private transient List<VideoList> videoLists;

    public Model(Activity activity) {
        videoListSettings = new VideoListSettings();
        this.activity = activity;
        videoLists = new ArrayList<>();
        stateSaveLoader = new JsonStateSaveLoader(activity);
    }

    public void initTransientFields(Activity activity) {
        this.activity = activity;
        videoLists = new ArrayList<>();
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
    public void updateGDriveVideoList(RecyclerView recyclerView, GoogleSignInAccount account) {
        fetchVideosFromGDrive(recyclerView, account)
                .addOnSuccessListener(newArrayListVideos -> {
                    VideoAdapter videoAdapter = new VideoAdapter(this);
                    recyclerView.setAdapter(videoAdapter);
                    setArrayListVideos(newArrayListVideos);
                    if (getVideoListSettings().reversedOrder) {
                        reverseVideoList();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Task<ArrayList<VideoModel>> fetchVideosFromGDrive(RecyclerView recyclerView, GoogleSignInAccount account) {
        GDriveService driveService = new GDriveService(getActivity().getApplicationContext(), account);
        return driveService.getUsersVideosList();
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

        Cursor cursor = activity.getApplicationContext().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                getVideoListSettings().sortParam.getForDeviceSort() //+ "DESC"
        );

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathImage = cursor.getString(columnIndexData);

            VideoModel videoModel = new VideoModel();
            videoModel.setBooleanSelected(false);
            videoModel.setGDriveFile(false);
            videoModel.setStrPath(absolutePathImage);
            videoModel.setStrThumb(cursor.getString(thum));

            newArrayListVideos.add(videoModel);
        }

        //call the com.example.avp.adapter class and set it to recyclerview

        VideoAdapter videoAdapter = new VideoAdapter(this);
        recyclerView.setAdapter(videoAdapter);
    }

    public Constants.DisplayMode getVideoListDisplayMode() {
        return videoListSettings.displayMode;
    }

    public int getArrayListVideosSize() {
        return arrayListVideos.size();
    }

    public String getVideoPath(int i) {
        return arrayListVideos.get(i).getStrPath();
    }

    public String getVideoThumb(int i) {
        VideoModel video = arrayListVideos.get(i);
        if (video.isGDriveFile()) {
            return video.getStrThumb();
        }
        return "file://" + video.getStrThumb();
    }

    public Context getContext() {
        return activity.getApplicationContext();
    }

    public String getFileSizeMegaBytes(String path) {
        File file = new File(path);
        return (double) file.length() / (1024 * 1024) + " mb";
    }

    public String getVideoNameByPosition(int i) {
        VideoModel video = arrayListVideos.get(i);
        if (video.isGDriveFile()) {
            return video.getName();
        }
        String[] parts = video.getStrPath().split(File.separator);
        return parts[parts.length - 1];
    }

    public String getVideoName(String link) {
        String[] parts = link.split(File.separator);
        return parts[parts.length - 1];
    }

    public String getVideoDuration(String path) {
        Uri uri = Uri.parse(path);
        MediaPlayer mp = MediaPlayer.create(activity.getApplicationContext(), uri);
        int duration = mp.getDuration();
        mp.release();
        //TODO: move formating to a separate function, add hours
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    public void addVideoList(VideoList videoList) {
        videoLists.add(videoList);
    }
}
