package com.example.avp.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.avp.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class ExoPlayerActivity extends AppCompatActivity {
    PlayerView playerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        // this code is copied from VideoViewActivity, but we will VideoViewActivity in the future
        Intent intent = getIntent();
        String linkOnVideo = intent.getStringExtra("linkOnVideo");
        if (linkOnVideo.isEmpty()) {
            linkOnVideo = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        }
        createSimpleExoPlayerAndPlayVideoByLink(linkOnVideo);
    }

    private void createSimpleExoPlayerAndPlayVideoByLink(String linkOnVideo) {
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
        playerView = (PlayerView) findViewById(R.id.playerView);

        playerView.setPlayer(player);
        MediaItem mediaItem = null;
        try {
            mediaItem = MediaItem.fromUri(String.valueOf((new URL(linkOnVideo)).toURI()));
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // hide title bar
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        // hide notification bar and set full screen
        ExoPlayerActivity.this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | // hide navigation line on the bottom of screen (that helps go home)
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY /* hide notification bar and show it only after sliding down,
                                                                                         and hide again in some seconds */
        );

        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerView.getPlayer().release();
    }
}