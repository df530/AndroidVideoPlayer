package com.example.avp.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaDataSource;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bosphere.verticalslider.VerticalSlider;
import com.example.avp.R;
import com.gdrive.GDriveFileDownloader;
import com.gdrive.GDriveWrapper;
import com.gdrive.GoogleAccountHolder;
import com.google.android.exoplayer2.MediaItem;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import static com.google.android.exoplayer2.C.WAKE_MODE_NETWORK;

public class ExoPlayerActivity extends AppCompatActivity {
    private ExoPlayerModel playerModel;

    public static SimpleExoPlayer player;

    private PlayerView playerView;
    private ProgressBar progressBar;
    private ImageView lockRotationButton;
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
                15000, 120000, 2500, 5000).build();

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

        player.setWakeMode(WAKE_MODE_NETWORK);
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
            } else {
                player.play();
            }
        });

        initControllerElements();

        playerModel.getMediaSource().subscribe(ms -> {
            player.setMediaSource(ms);
            player.prepare();
        });
        player.setPlayWhenReady(true);

        startService(new Intent(this, ExoPlayerService.class));
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

    private class ExoPlayerEventListener implements Player.EventListener {
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
        /*if (GDriveFileDownloader.isGDriveURL(linkOnVideo)) {
            handleGDriveVideo(player, linkOnVideo);
        } else {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(linkOnVideo)));
            player.prepare();
            player.play();
        }
    }

    private void handleGDriveVideo(SimpleExoPlayer player, String linkOnVideo) {
        GoogleSignInAccount account = GoogleAccountHolder.getInstance().getAccount();
        if (account == null) {
            throw new IllegalStateException("User is not logged in to the google account.");
        }
        GDriveWrapper gDriveWrapper = new GDriveWrapper(this, account);
        Task<byte[]> fileTask = gDriveWrapper.getFile(GDriveFileDownloader.getGDriveFileIDFromURL(linkOnVideo));
*/
        @Override
        public void onPlaybackParametersChanged(@NotNull PlaybackParameters playbackParameters) {
            speedValueTV.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> {
                if (!playerView.isControllerVisible()) {
                    speedValueTV.setVisibility(View.INVISIBLE);
                }
            }, 1000);
        }
        /*
        fileTask.addOnSuccessListener(fileBytes -> {
            ByteArrayDataSource dataSource = new ByteArrayDataSource(fileBytes);
            try { // Magic part, exoplayer needs Uri :(
                dataSource.open(new DataSpec(Uri.fromFile(Environment.getExternalStorageDirectory())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            DataSource.Factory factory = () -> dataSource;
            MediaSource fileSource = new ExtractorMediaSource(dataSource.getUri(),
                    factory, new DefaultExtractorsFactory(), null, null);
            player.setMediaSource(fileSource);
            player.prepare();
            player.play();
        });
    }*/

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (isPlaying) {
                // start service if it was destroyed
                if (ExoPlayerService.isNotificationServiceDestroyed()) {
                    startService(new Intent(ExoPlayerActivity.this, ExoPlayerService.class));
                }
            }
        }
    }

    private class LockRotationClickListener implements View.OnClickListener {
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
    }

    private class SpeedValueTextViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (speedLL.getVisibility() == View.INVISIBLE) {
                speedLL.setVisibility(View.VISIBLE);
            } else {
                speedLL.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class VerticalSliderProgressChangeListener implements VerticalSlider.OnProgressChangeListener {
        @Override
        public void onProgress(float progress) {
            float curSpeed;
            if (progress <= 0.5f) {
                curSpeed = 0.5f + progress / 0.5f * 0.5f;
            } else {
                curSpeed = 1f + (progress - 0.5f) / 0.5f;
            }
            speedValueTV.setText((new DecimalFormat("#.##x").format(curSpeed)));
            player.setPlaybackParameters(new PlaybackParameters(curSpeed));
        }
    }

    private void initControllerElements() {
        player.addListener(new ExoPlayerEventListener());

        lockRotationButton.setOnClickListener(new LockRotationClickListener());

        speedValueTV.setText("1x");
        speedValueTV.setOnClickListener(new SpeedValueTextViewClickListener());

        speedVS.setProgress(0.5f);
        speedVS.setOnSliderProgressChangeListener(new VerticalSliderProgressChangeListener());
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
    protected void onDestroy() {
        player.release();
        player = null;
        stopService(new Intent(this, ExoPlayerService.class));

        super.onDestroy();
    }
}