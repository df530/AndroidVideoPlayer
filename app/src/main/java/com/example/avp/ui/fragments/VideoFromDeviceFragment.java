package com.example.avp.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.model.Model;
import com.example.avp.ui.VideoListSettings;

import java.util.ArrayList;

import com.example.avp.adapter.VideoAdapter;
import com.example.avp.model.VideoModel;

public class VideoFromDeviceFragment extends Fragment {

    private VideoFromDeviceViewModel mViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private final Model model;

    public VideoFromDeviceFragment(Model model) {
        this.model = model;
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void init() {
        recyclerView = getActivity().findViewById(R.id.recyclerviewVideo);
        recyclerViewLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),
                model.getVideoListColumnsNum());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        VideoAdapter videoAdapter = new VideoAdapter(model, getActivity());
        recyclerView.setAdapter(videoAdapter);
    }

}