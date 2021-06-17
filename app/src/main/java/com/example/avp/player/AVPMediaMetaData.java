package com.example.avp.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import kotlin.time.Duration;
import lombok.Getter;
import lombok.Setter;

public class AVPMediaMetaData implements Serializable {
    @Getter
    private final String title;
    @Getter
    private final String author;
    @Getter
    private final String link;
    @Getter
    private final String previewURL;
    @Getter
    private final String path; // on device or gdrive
    @Getter
    @Setter
    private Long duration;
    @Getter
    private final Date dateTaken;

    @Setter
    private transient Bitmap previewBM;


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof AVPMediaMetaData) {
            AVPMediaMetaData other = (AVPMediaMetaData) obj;
            return Objects.equals(author, other.author) && Objects.equals(title, other.title) && Objects.equals(link, other.link);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, link);
    }

    public AVPMediaMetaData(String title, String author, String link, String previewURL, String path, Long durationMillis, Date dateTaken) {
        this.title = title;
        this.author = author;
        this.link = link;
        this.previewURL = previewURL;
        this.path = path;
        this.duration = durationMillis;
        this.dateTaken = dateTaken;
    }

    public Bitmap getPreviewBitmap() {
        if (previewBM == null) {
            if (previewURL == null) {
                // works for local video
                Bitmap res = ThumbnailUtils.createVideoThumbnail(link, MediaStore.Video.Thumbnails.MINI_KIND);
                if (res != null) {
                    previewBM = res;
                } else { // video isn't local
                    try {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(link, new HashMap<String, String>());
                        // this gets frame at 2nd second
                        previewBM = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                previewBM = loadImageFromURL(previewURL);
            }
        }

        return previewBM;
    }

    private static class LoadBitmap extends AsyncTask<String, Void, Bitmap> {
        private final String mUrl;

        public LoadBitmap(String url) {
            super();
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(mUrl);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                return null;
            }
        }
    }

    private static Bitmap loadImageFromURL(String urlString) {
        LoadBitmap loadBitmap = new LoadBitmap(urlString);
        try {
            return loadBitmap.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDurationString() {
        if (duration != null) {
            String format;
            if (duration >= 60 * 60 * 1000) {
                format = "HH:mm:ss";
            } else {
                format = "mm:ss";
            }
            return DurationFormatUtils.formatDuration(duration, format, true);
        }
        return null;
    }

    public String getDateTakenString() {
        if (dateTaken != null)
            return dateTaken.toString();
        else
            return null;
    }
}
