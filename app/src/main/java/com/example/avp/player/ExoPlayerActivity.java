package com.example.avp.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bosphere.verticalslider.VerticalSlider;
import com.example.avp.R;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;

public class ExoPlayerActivity extends AppCompatActivity {
    private ExoPlayerModel playerModel;

    private PlayerView playerView;
    private ProgressBar progressBar;
    private ImageView lockRotationButton;
    private SimpleExoPlayer player;
    private TextView speedValueTV;
    private LinearLayout speedLL;
    private VerticalSlider speedVS;

    private static final Handler handler = new Handler(); // for making delay for set visibility of progressBar etc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        Intent intent = getIntent();
        playerModel = new ExoPlayerModel(this, intent.getStringExtra("linkOnVideo"));

        createSimpleExoPlayerAndPlayVideoByLink();
    }

    private void createSimpleExoPlayer() {
        // Change buffer parameters to decrease loading time
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(
                15000, 50000, 2500, 5000).build();

        // Set audio attributes to make pause when another app start sound (music, for example)
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build();

        player = new SimpleExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .setHandleAudioBecomingNoisy(true) // make pause when headphones disconnected
                .setAudioAttributes(audioAttributes, true)
                .build();

        playerView.setPlayer(player);
    }

    private void createSimpleExoPlayerAndPlayVideoByLink() {
        // assign variables
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progress_bar);
        lockRotationButton = findViewById(R.id.lock_rotation);
        speedValueTV = findViewById(R.id.speed_value);
        speedLL = findViewById(R.id.speed_linear_layout);
        speedVS = findViewById(R.id.speed_vertical_slide);

        createSimpleExoPlayer();

        // Make speed TextView visibility like controller visibility
        playerView.setControllerVisibilityListener(visibility -> speedValueTV.setVisibility(visibility));

        // activate double tap rewind and fast forward increment
        YouTubeOverlay youTubeOverlay = findViewById(R.id.youtube_overlay);
        youTubeOverlay
                .performListener(new YouTubeOverlay.PerformListener() {
                    @Override
                    public void onAnimationStart() {
                        youTubeOverlay.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd() {
                        youTubeOverlay.setVisibility(View.GONE);
                    }
                });
        youTubeOverlay.player(player);

        // Creating broadcast receiver to making pause/play from headphones
        HeadsetButtonReceiver hbr = new HeadsetButtonReceiver(this);
        hbr.setOnHeadsetListener(() -> {
            if (player.isPlaying()) {
                player.pause();
            }
            else {
                player.play();
            }
        });

        initControllerElements();

        playerModel.getMediaSource().subscribe(player::setMediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    private void hideAllSystemElements() {
        // make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // hide notification bar and set full screen
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void initControllerElements() {
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) {
                    handler.postDelayed(() -> {
                        if (player.getPlaybackState() == Player.STATE_BUFFERING) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                } else if (state == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlaybackParametersChanged(@NotNull PlaybackParameters playbackParameters) {
                speedValueTV.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> {
                    if (!playerView.isControllerVisible()) {
                        speedValueTV.setVisibility(View.INVISIBLE);
                    }
                }, 1000);
            }
        });

        lockRotationButton.setOnClickListener(new View.OnClickListener() {
            boolean isScreenLocked = false;

            @Override
            public void onClick(View v) {
                if (isScreenLocked) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    lockRotationButton.setColorFilter(Color.WHITE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    lockRotationButton.setColorFilter(R.color.purple_200);
                }
                isScreenLocked = !isScreenLocked;
            }
        });

        speedValueTV.setOnClickListener(v -> {
            if (speedLL.getVisibility() == View.INVISIBLE) {
                speedLL.setVisibility(View.VISIBLE);
            }
            else {
                speedLL.setVisibility(View.INVISIBLE);
            }
        });

        speedVS.setProgress(0.5f);
        speedValueTV.setText("1x");
        speedVS.setOnSliderProgressChangeListener(progress -> {
            float curSpeed;
            if (progress <= 0.5f) {
                curSpeed = 0.5f + progress / 0.5f * 0.5f;
            }
            else {
                curSpeed = 1f + (progress - 0.5f) / 0.5f;
            }
            speedValueTV.setText((new DecimalFormat("#.##x").format(curSpeed)));
            player.setPlaybackParameters(new PlaybackParameters(curSpeed));
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /* hide notification bar and set full screen. Make it again, because this settings reset
         * after switching focus
         */
        hideAllSystemElements();
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
        Objects.requireNonNull(playerView.getPlayer()).release();
    }
}