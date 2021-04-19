package com.example.videoplayertest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;

import Adapter.VideoAdapter;
import Model.VideoModel;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    ArrayList<VideoModel> arrayListVideos;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewVideo);
        recyclerViewLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        arrayListVideos = new ArrayList<>();
        fetchVideosFromGallery();
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void fetchVideosFromGallery() {
        int column_index_data, column_index_folder_name, column_id, thum;
        String absolutePathImage;

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA
        };
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN;

        Cursor cursor = getApplicationContext().getContentResolver().query(
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

        VideoAdapter videoAdapter = new VideoAdapter(getApplicationContext(), arrayListVideos, MainActivity.this);
        recyclerView.setAdapter(videoAdapter);
    }
}