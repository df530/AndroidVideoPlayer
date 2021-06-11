package com.example.avp.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.avp.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.util.Objects;

import lombok.Getter;

public class ExoPlayerService extends Service {
    public static final String CHANNEL_ID = "player_channel";
    public static final int NOTIFICATION_ID = 1;
    private PlayerNotificationManager playerNotificationManager;
    @Getter
    private static boolean notificationServiceDestroyed = false;

    private static class NotificationMediaDescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {
        @NonNull
        @Override
        public CharSequence getCurrentContentTitle(@NonNull Player player) {
            if (player.getCurrentMediaItem() == null)
                return "No media";
            AVPMediaMetaData meta = (AVPMediaMetaData) Objects.requireNonNull(player.getCurrentMediaItem().playbackProperties).tag;
            if (meta == null)
                return "No meta";
            if (meta.getTitle() == null)
                return "No title";
            return meta.getTitle();
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(@NonNull Player player) {
            return null;
        }

        @Override
        public CharSequence getCurrentContentText(@NonNull Player player) {
            if (player.getCurrentMediaItem() == null)
                return "No media";
            AVPMediaMetaData meta = (AVPMediaMetaData) Objects.requireNonNull(player.getCurrentMediaItem().playbackProperties).tag;
            if (meta == null)
                return "No meta";
            if (meta.getAuthor() == null) {
                if (meta.getUri() != null)
                    return meta.getUri();
                return "No author and URI";
            }
            return meta.getAuthor();
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(
                @NonNull Player player,
                @NonNull PlayerNotificationManager.BitmapCallback callback) {
            if (player.getCurrentMediaItem() == null)
                return null;
            AVPMediaMetaData meta = (AVPMediaMetaData) Objects.requireNonNull(player.getCurrentMediaItem().playbackProperties).tag;
            if (meta == null || meta.getPreviewBM() == null)
                return null;
            Bitmap previewBM = meta.getPreviewBM();
            callback.onBitmap(previewBM);
            return previewBM;
        }
    }

    private class NotificationListener implements PlayerNotificationManager.NotificationListener {
        @Override
        public void onNotificationPosted(
                int notificationId,
                @NonNull Notification notification,
                boolean ongoing) {

            if (!ongoing) {
                stopForeground(false);
            } else {
                startForeground(notificationId, notification);
            }
        }

        @Override
        public void onNotificationCancelled(int notificationId,
                                            boolean dismissedByUser) {

            if (dismissedByUser) {
                stopForeground(true);
            }
            stopSelf();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playerNotificationManager = new PlayerNotificationManager.Builder(
                this,
                NOTIFICATION_ID,
                CHANNEL_ID,
                new NotificationMediaDescriptionAdapter()
        )
                .setChannelDescriptionResourceId(R.string.app_description)
                .setChannelNameResourceId(R.string.app_name)
                .setNotificationListener(new NotificationListener())
                .build();

        playerNotificationManager.setPlayer(ExoPlayerActivity.player);

        // Customization
        playerNotificationManager.setUsePreviousAction(false);
        playerNotificationManager.setUseNextAction(false);

        /* PlayerNotificationManager.setColor + setColorized have to change background color, but they didn't it.
         * I want to make background transparent. Moreover, we can find in the source code, that
         * default color is transparent. In real it is always white. (not depend on calling setColor)
         * In the video, where exoplayer developers tell about notification service, they don't change
         * color and there background color is white (I can't understand why).
         *
         * I created crutch (noticed it in video). When exoplayer developers created mediaSession,
         * background became transparent. I can't understand why, but it works for me too.
         *
         * But it isn't solve the problem of chronometer. If I understand right, chronometer ==
         * time seek bar. And setUseChronometer(true) have to make it visible. Moreover, it have
         * to be visible by default (I see it in the source code). But it is invisible and by
         * default, and after setUseChronometer(true). In the video it is invisible by default
         * too.
         * Unfortunately, I don't know crutch for chronometer.
         *
         * UPD: THE EXOPLAYER DOCS, GOOGLE BROWSER, GIT AND ETC ARE PIECE OF SOMETHING NOT VERY
         * GOOD BECAUSE I SPEND 7+ OURS TO FIND THE ISSUE, WHERE THE TELL, HOW ADD SEEK BAR TO THE
         * NOTIFICATION. AND YES, THEY TELL ABOUT MEDIASESSION. BUT I THOUGHT THAT IT HELPS ONLY
         * WITH GOOGLE ASSISTANT, SMART WATCHES, ANDROID CAR PLAY ETC.
         */

        MediaSessionCompat mediaSession = new MediaSessionCompat(this, MEDIA_SESSION_SERVICE);
        mediaSession.setActive(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        MediaSessionConnector mediaSessionConnector =
                new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(ExoPlayerActivity.player);

        notificationServiceDestroyed = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (playerNotificationManager != null) {
            playerNotificationManager.setPlayer(null);
            playerNotificationManager = null;
        }
        notificationServiceDestroyed = true;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
