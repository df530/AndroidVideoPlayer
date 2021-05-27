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

import com.example.avp.model.Model;
import com.example.avp.R;
import com.example.avp.player.ExoPlayerActivity;

import com.example.avp.adapter.LastSeenLinksAdapter;

public class VideoByLinkFragment extends Fragment {

    private VideoByLinkViewModel mViewModel;
    private Button playButton;
    private EditText link;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private final Model model;

    public VideoByLinkFragment(Model model) {
        this.model = model;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_by_link_fragment, container, false);
        playButton = view.findViewById(R.id.play_button);
        link = view.findViewById(R.id.edit_text_link);
        playButton.setOnClickListener(this::playVideo);
        return view;
    }

    private void playVideo(View v) {
        Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
        String linkOnVideo = link.getText().toString();
        model.addRecentVideo(linkOnVideo);
        intent.putExtra("linkOnVideo", linkOnVideo);
        startActivity(intent);
        update();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VideoByLinkViewModel.class);

        init();
    }

    private void init() {
        recyclerView = getActivity().findViewById(R.id.recyclerviewLastSeen);
        recyclerViewLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        update();
    }

    private void update() {
        LastSeenLinksAdapter adapter = new LastSeenLinksAdapter(model, getActivity());
        recyclerView.setAdapter(adapter);
    }
}