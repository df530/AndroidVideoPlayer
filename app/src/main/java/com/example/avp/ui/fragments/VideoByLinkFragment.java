package com.example.avp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avp.R;
import com.example.avp.player.ExoPlayerActivity;

public class VideoByLinkFragment extends Fragment {

    private VideoByLinkViewModel mViewModel;
    private Button playButton;
    private EditText link;

    public static VideoByLinkFragment newInstance() {
        return new VideoByLinkFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_by_link_fragment, container, false);
        playButton = view.findViewById(R.id.play_button);
        link = view.findViewById(R.id.edit_text_link);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
                String linkOnVideo = link.getText().toString();

                intent.putExtra("linkOnVideo", linkOnVideo);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VideoByLinkViewModel.class);
        // TODO: Use the ViewModel
    }

}