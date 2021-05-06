package com.example.avp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.example.avp.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private ImageView imageView;
    private SeekBar seekBar;
    private String strVideoUrl;
    private boolean isPlaying = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        init();
    }

    private void init() {
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.toggleButton);
        seekBar = findViewById(R.id.seekBar);
        strVideoUrl = getIntent().getStringExtra("video");
        videoView.setVideoPath(strVideoUrl);
        handler = new Handler();

        videoView.start();
        isPlaying = true;
        imageView.setImageResource(R.drawable.pause_button);
        updateSeekBar();
    }

    private void updateSeekBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    public Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(videoView.getCurrentPosition());
            seekBar.setMax(videoView.getDuration());
            handler.postDelayed(this, 100);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    handler.removeCallbacks(updateTimeTask);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    handler.removeCallbacks(updateTimeTask);
                    videoView.seekTo(seekBar.getProgress());
                    updateSeekBar();
                }
            });
        }
    };

    public void toggle_method(View v) {
        if (isPlaying) {
            videoView.pause();
            isPlaying = false;
            imageView.setImageResource(R.drawable.play_button);
        } else {
            videoView.start();
            updateSeekBar();
            isPlaying = true;
            imageView.setImageResource(R.drawable.pause_button);
        }
    }
}