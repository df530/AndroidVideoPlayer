package com.example.avp
        ;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

// extends from Activity because I want to use Theme.NoTitleBar
public class VideoActivity extends Activity {
    VideoView videoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        videoPlayer =  (VideoView)findViewById(R.id.videoPlayer);
        if (message.isEmpty()) {
            message = "android.resource://" + getPackageName() + "/" + R.raw.videoplayback;
        }
        /* There is a problem with links, which are use 'http' protocol (they aren't played).
         * 'https' protocol works correct (tested by one video from: https://techslides.com/demos/sample-videos/small.mp4)
         */
        //NOT WORK: videoPlayer.setVideoPath("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        //videoPlayer.setVideoPath("https://techslides.com/demos/sample-videos/small.mp4");

        videoPlayer.setVideoPath(message);

        MediaController mediaController = new MediaController(this);
        videoPlayer.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoPlayer);

        videoPlayer.start();
    }
}