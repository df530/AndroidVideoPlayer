package com.example.avp.player;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bosphere.verticalslider.VerticalSlider;
import com.example.avp.R;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class ExoPlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ProgressBar progressBar;
    private ImageView lockRotationButton;
    private SimpleExoPlayer player;
    private TextView speedValueTV;
    private LinearLayout speedLL;
    private VerticalSlider speedVS;

    static final Handler handler = new Handler(); // for making delay for set visibility of progressBar etc

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

    public static boolean isYoutubeUrl(String youTubeURl) {
        String pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+";
        return !youTubeURl.isEmpty() && youTubeURl.matches(pattern);
    }

    private void createSimpleExoPlayerAndPlayVideoByLink(String linkOnVideo) {
        // assign variables
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progress_bar);
        lockRotationButton = findViewById(R.id.lock_rotation);
        speedValueTV = findViewById(R.id.speed_value);
        speedLL = findViewById(R.id.speed_linear_layout);
        speedVS = findViewById(R.id.speed_vertical_slide);

        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

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

        setPlayerMediaByLink(linkOnVideo);

        initControllerElements();

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

        speedValueTV.setOnClickListener(new View.OnClickListener() {
            //boolean isSpeedIncremented = false;
            @Override
            public void onClick(View v) {
                if (speedLL.getVisibility() == View.INVISIBLE) {
                    speedLL.setVisibility(View.VISIBLE);
                }
                else {
                    speedLL.setVisibility(View.INVISIBLE);
                }
                /*
                if (isSpeedIncremented) {
                    player.setPlaybackParameters(new PlaybackParameters(1));
                    speedIncrementButton.setColorFilter(Color.WHITE);
                } else {
                    player.setPlaybackParameters(new PlaybackParameters(2));
                    speedIncrementButton.setColorFilter(R.color.purple_200);
                }
                isSpeedIncremented = !isSpeedIncremented;
                */
            }
        });

        speedVS.setProgress(0.5f);
        speedValueTV.setText("1x");
        speedVS.setOnSliderProgressChangeListener(new VerticalSlider.OnProgressChangeListener() {
            @Override
            public void onProgress(float progress) {
                float curSpeed;
                if (progress <= 0.5f) {
                    curSpeed = 0.5f + progress / 0.5f * 0.5f;
                }
                else {
                    curSpeed = 1f + (progress - 0.5f) / 0.5f;
                }
                speedValueTV.setText((new DecimalFormat("#.##x").format(curSpeed)));
                player.setPlaybackParameters(new PlaybackParameters(curSpeed));
            }
        });
    }


    private void setPlayerMediaByLink(String linkOnVideo) {
        // check for youtube
        if (isYoutubeUrl(linkOnVideo)) {
            playVideoFromYouTube(linkOnVideo);
        }
        /* TODO
         else if (isLinkOnGoogleDrive(linkOnVideo)) {
            create mediaSource or mediaItem
            player.setMedia*(media*);
         }
         */
        else {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(linkOnVideo)));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void playVideoFromYouTube(String linkOnVideo) {
        new YouTubeExtractor(this) {
            private int getBestPossibleVideoTag(SparseArray<YtFile> ytFiles) {
                List<Integer> videoTags = Arrays.asList(
                        266, // 2160p
                        264, // 1440p
                        299, // 1080p60
                        137, // 1080p
                        298, // 720p60
                        136, // 720p
                        135, // 480p
                        134, // 360p
                        133, // 240p
                        160  // 144p
                );
                for (Integer videoTag : videoTags) {
                    YtFile ytFile = ytFiles.get(videoTag);
                    if (ytFile != null) {
                        return videoTag;
                    }
                }
                throw new RuntimeException("No video in ytFiles");
            }

            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                if (ytFiles != null) {
                    // 720, 1080, 480
                    int videoTag = getBestPossibleVideoTag(ytFiles);
                    int audioTag = 140; // audio tag for m4a
                    MediaSource audioSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(audioTag).getUrl()));
                    MediaSource videoSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(videoTag).getUrl()));
                    player.setMediaSource(new MergingMediaSource(true, videoSource, audioSource), true);
                }
            }
        }.extract(linkOnVideo, true, true);
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
        playerView.getPlayer().release();
    }
}