package com.example.avp.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaDataSource;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.avp.R;
import com.gdrive.GDriveFileDownloader;
import com.gdrive.GDriveWrapper;
import com.gdrive.GoogleAccountHolder;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
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

public class ExoPlayerActivity extends AppCompatActivity {
    PlayerView playerView;
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
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
        playerView = (PlayerView) findViewById(R.id.playerView);
        playerView.setPlayer(player);

        // hide title bar
        if (getSupportActionBar()!= null) {
            getSupportActionBar().hide();
        }

        // hide notification bar and set full screen
        playerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | // hide navigation line on the bottom of screen (that helps go home)
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY /* hide notification bar and show it only after sliding down,
                                                                                         and hide again in some seconds */
        );

        if (GDriveFileDownloader.isGDriveURL(linkOnVideo)) {
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        Objects.requireNonNull(playerView.getPlayer()).pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /* hide notification bar and set full screen. Make it again, because this settings reset
         * after switching apps
         */
        playerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | // hide navigation line on the bottom of screen (that helps go home)
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY /* hide notification bar and show it only after sliding down,
                                                                                         and hide again in some seconds */
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerView.getPlayer().release();
    }
}