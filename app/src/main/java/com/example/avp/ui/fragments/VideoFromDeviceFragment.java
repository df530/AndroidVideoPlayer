package com.example.avp.ui.fragments;

import android.os.Build;
import android.os.Bundle;
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
import com.example.avp.lists.VideoList;
import com.example.avp.lists.impls.device.DeviceVideoList;
import com.example.avp.lists.impls.device.DeviceVideosHolder;
import com.example.avp.lists.impls.recents.RecentVideoList;
import com.example.avp.lists.impls.recents.RecentVideosHolder;
import com.example.avp.model.Model;
import com.example.avp.ui.Constants;

import java.util.Set;

public class VideoFromDeviceFragment extends Fragment {
    private static VideoList deviceVideoList = null;
    private static Model model;

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

        RecyclerView deviceVideoRV = getActivity().findViewById(R.id.rv_device_videos);
        if (deviceVideoList == null) {
            deviceVideoList = new DeviceVideoList(
                    deviceVideoRV,
                    DeviceVideosHolder.getInstance(),
                    model.getVideoListSettings(),
                    Set.of(Constants.DisplayMode.GALLERY, Constants.DisplayMode.LIST),
                    this,
                    model.getContext(),
                    getView());

            model.addVideoList(deviceVideoList);
            deviceVideoList.fetchVideosAndUpdate();
        }
        else {
            deviceVideoList.setVideoListRV(deviceVideoRV);
        }
    }
}