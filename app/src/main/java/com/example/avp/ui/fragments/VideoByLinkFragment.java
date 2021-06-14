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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.model.Model;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.player.ExoPlayerActivity;

import com.example.avp.adapter.LastSeenMetaDataAdapter;
import com.example.avp.player.ExoPlayerModel;

import static android.app.Activity.RESULT_OK;

public class VideoByLinkFragment extends Fragment {

    private VideoByLinkViewModel mViewModel;
    private Button playButton;
    private EditText link;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private static Model model;

    public VideoByLinkFragment(Model model) {
        this.model = model;
    }

    public static VideoByLinkFragment newInstance() {
        return new VideoByLinkFragment(model);
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

            intent.putExtra("linkOnVideo", linkOnVideo);
            startActivityForResult(intent, 1);
        });

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
                    model.addRecentVideo(metaData);
                    update();
                }
            }
        }
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
        LastSeenMetaDataAdapter adapter = new LastSeenMetaDataAdapter(model, this);
        recyclerView.setAdapter(adapter);
    }
}