package com.example.avp.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.avp.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Objects;

public class ExoPlayerActivity extends AppCompatActivity {
    PlayerView playerView;
    ProgressBar progressBar;
    LinearLayout centerPlayerControl;            // pause, play buttons
    ImageView lockRotationButton;
    SimpleExoPlayer player;

    static final Handler handler = new Handler(); // for making delay for set visibility of progressBar etc
    static final int hideBarsFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        Intent intent = getIntent();
        String linkOnVideo = intent.getStringExtra("linkOnVideo");
        if (linkOnVideo.isEmpty()) {
            linkOnVideo = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        }

        createSimpleExoPlayerAndPlayVideoByLink(linkOnVideo);
    }

    private void createSimpleExoPlayerAndPlayVideoByLink(String linkOnVideo) {
        // assign variables
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progress_bar);
        centerPlayerControl = findViewById(R.id.center_player_control);
        lockRotationButton = findViewById(R.id.lock_rotation);

        // make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // hide notification bar and set full screen
        playerView.setSystemUiVisibility(hideBarsFlags);

        /* TODO
         if (isLinkOnGoogleDrive(linkOnVideo)) {
            create mediaSource or mediaItem
            player.setMedia*(media*);
         }
         else {
             player.setMediaItem(MediaItem.fromUri(Uri.parse(linkOnVideo)));
         }
         */
        player.setMediaItem(MediaItem.fromUri(Uri.parse(linkOnVideo)));

        player.prepare();

        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            if (player.getPlaybackState() == Player.STATE_BUFFERING) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 500);
                } else if (state == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        lockRotationButton.setOnClickListener(new View.OnClickListener() {
            boolean isScreenLocked = false;

            @Override
            public void onClick(View v) {
                if (isScreenLocked) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    lockRotationButton.setColorFilter(getResources().getColor(R.color.white));
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    lockRotationButton.setColorFilter(getResources().getColor(R.color.purple_200));
                }
                isScreenLocked = !isScreenLocked;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /* hide notification bar and set full screen. Make it again, because this settings reset
         * after switching focus
         */
        if (hasFocus) {
            playerView.setSystemUiVisibility(hideBarsFlags);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Objects.requireNonNull(playerView.getPlayer()).pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerView.getPlayer().release();
    }
}