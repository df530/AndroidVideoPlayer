package com.example.avp.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.player.ExoPlayerActivity;
import com.example.avp.ui.LastSeenVideosHolder;

import java.util.ArrayList;

import Adapter.LastSeenLinksAdapter;
import Adapter.VideoAdapter;
import Model.LastSeenLinkModel;

public class VideoByLinkFragment extends Fragment {

    private VideoByLinkViewModel mViewModel;
    private Button playButton;
    private EditText link;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private LastSeenVideosHolder linksHolder = new LastSeenVideosHolder();

    public static VideoByLinkFragment newInstance() {
        return new VideoByLinkFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_by_link_fragment, container, false);
        playButton = view.findViewById(R.id.play_button);
        link = view.findViewById(R.id.edit_text_link);

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
            String linkOnVideo = link.getText().toString();

            linksHolder.addVideo(linkOnVideo);
            //System.out.println("add link: " + linkOnVideo);
            //System.out.println(linksHolder.getLastSeenLinkModelList().size());
            //System.out.println("add link: " + linkOnVideo);
            //TODO: add link to linksHolder and update video_by_link_fragment R.id.recyclerviewLastSeen

            intent.putExtra("linkOnVideo", linkOnVideo);
            startActivity(intent);
            update();
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VideoByLinkViewModel.class);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void init() {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerviewLastSeen);
        recyclerViewLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        update();
    }

    private void update() {
        LastSeenLinksAdapter adapter = new LastSeenLinksAdapter(getActivity().getApplicationContext(),
                linksHolder.getLastSeenLinkModelList(), getActivity());
        recyclerView.setAdapter(adapter);
    }
}