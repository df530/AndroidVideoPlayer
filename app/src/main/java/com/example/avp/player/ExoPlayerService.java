package com.example.avp.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.avp.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ExoPlayerService extends Service {
    public static final String CHANNEL_ID = "player_channel";
    public static final int NOTIFICATION_ID = 2;
    private PlayerNotificationManager playerNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Bitmap getBitmapFromURL(String src) {
        /*
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
        */
        return null;
    }

    public static  InputStream getHttpConnection(String urlString)  throws IOException {

        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("downloadImage" + ex.toString());
        }
        return stream;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(playerNotificationManager == null) {
            playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                    this,
                    CHANNEL_ID,
                    R.string.app_name,
                    R.string.app_description,
                    NOTIFICATION_ID,
                    new PlayerNotificationManager.MediaDescriptionAdapter() {
                        @NonNull
                        @Override
                        public CharSequence getCurrentContentTitle(@NonNull Player player) {
                            if (player.getCurrentMediaItem() == null)
                                return "No media";
                            AVPMediaMetaData meta = (AVPMediaMetaData)player.getCurrentMediaItem().playbackProperties.tag;
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
                            AVPMediaMetaData meta = (AVPMediaMetaData)player.getCurrentMediaItem().playbackProperties.tag;
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
                            AVPMediaMetaData meta = (AVPMediaMetaData)player.getCurrentMediaItem().playbackProperties.tag;
                            if (meta == null || meta.getPreviewURL() == null)
                                return null;
                            return getBitmapFromURL(meta.getPreviewURL());
                        }
                    },
                    new PlayerNotificationManager.NotificationListener() {
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
            );

            playerNotificationManager.setPlayer(ExoPlayerActivity.player);

            // Customization
            playerNotificationManager.setUsePreviousAction(false);
            playerNotificationManager.setUseNextAction(false);
            playerNotificationManager.setColorized(true);
            playerNotificationManager.setUseChronometer(true);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (playerNotificationManager != null) {
            playerNotificationManager.setPlayer(null);
            playerNotificationManager = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
