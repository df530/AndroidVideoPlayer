package com.example.avp.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.lists.VideoList;
import com.example.avp.lists.impls.recents.RecentVideosHolder;
import com.example.avp.lists.impls.recents.RecentVideoList;
import com.example.avp.model.Model;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.player.ExoPlayerActivity;

import com.example.avp.player.ExoPlayerModel;
import com.example.avp.ui.Constants;

import java.util.Set;

public class VideoByLinkFragment extends Fragment {
    private static VideoList recentVideoList = null; // this helps not make reload
    private static Model model;

    public VideoByLinkFragment(Model model) {
        this.model = model;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_by_link_fragment, container, false);
        Button playButton = view.findViewById(R.id.play_button);
        EditText link = view.findViewById(R.id.edit_text_link);

        playButton.setOnClickListener(v ->
                ExoPlayerActivity.startExoPlayerFromFragmentForResult(model.getContext(),this, link.getText().toString(), 1));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == ExoPlayerModel.LINK_INCORRECT) {
            Toast.makeText(getContext(), "Can't load this link", Toast.LENGTH_LONG).show();
        }
        else {
            if (data != null) {
                AVPMediaMetaData metaData = (AVPMediaMetaData)data.getSerializableExtra("Metadata");
                if (metaData != null) {
                    recentVideoList.add(metaData, true);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView recentVideoRV = (RecyclerView) getActivity().findViewById(R.id.rv_recent_videos);
        if (recentVideoList == null) {
            recentVideoList = new RecentVideoList(
                    recentVideoRV,
                    RecentVideosHolder.getInstance(),
                    model.getVideoListSettings(),
                    Set.of(Constants.DisplayMode.LIST),
                    this,
                    model.getContext()
            );

            model.addVideoList(recentVideoList);
            recentVideoList.loadSavedState(model.getStateSaveLoader());
        }
        else {
            recentVideoList.setVideoListRV(recentVideoRV);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        recentVideoList.saveState(model.getStateSaveLoader());
    }
}