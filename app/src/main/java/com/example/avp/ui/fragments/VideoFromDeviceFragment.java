package com.example.avp.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.ui.VideoListSettings;

import java.util.ArrayList;
import java.util.Arrays;

import Adapter.VideoAdapter;
import Model.VideoModel;

public class VideoFromDeviceFragment extends Fragment {

    private VideoFromDeviceViewModel mViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<VideoModel> arrayListVideos;
    private static VideoListSettings currentSettings;

    public VideoFromDeviceFragment(VideoListSettings settings) {
        currentSettings = settings;
    }

    public static VideoFromDeviceFragment newInstance() {
        return new VideoFromDeviceFragment(currentSettings);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.video_from_device_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VideoFromDeviceViewModel.class);
        // TODO: Use the ViewModel

        init();
    }

    private void reverseVideoList() {
        ArrayList<VideoModel> newVideoList = new ArrayList<>();
        for (int i = arrayListVideos.size() - 1; i >= 0; i--) {
            newVideoList.add(arrayListVideos.get(i));
        }
        arrayListVideos.clear();
        arrayListVideos.addAll(newVideoList);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void init() {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerviewVideo);
        recyclerViewLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), currentSettings.columnsNum);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        arrayListVideos = new ArrayList<>();
        fetchVideosFromGallery();

        if (currentSettings.reversedOrder) {
            reverseVideoList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
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

        String sortOrder = currentSettings.sortedBy;

        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
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
            videoModel.setBoolean_selected(false);
            videoModel.setStr_path(absolutePathImage);
            videoModel.setStr_thumb(cursor.getString(thum));

            arrayListVideos.add(videoModel);
        }

        //call the adapter class and set it to recyclerview

        VideoAdapter videoAdapter = new VideoAdapter(getActivity().getApplicationContext(),
                arrayListVideos, getActivity(), currentSettings.displayMode);
        recyclerView.setAdapter(videoAdapter);
    }

}