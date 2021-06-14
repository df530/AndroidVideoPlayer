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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import lombok.Getter;

public class AVPMediaMetaData implements Serializable {
    @Getter
    private final String title;
    @Getter
    private final String author;
    @Getter
    private final String link;
    @Getter
    private final String previewURL;

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

    public AVPMediaMetaData(String title, String author, String link, String previewURL) {
        this.title = title;
        this.author = author;
        this.link = link;
        this.previewURL = previewURL;
        this.previewBM = getPreviewBitmap();
    }

    public Bitmap getPreviewBitmap() {
        if (previewBM == null) {
            if (previewURL == null) {
                // works for local video
                Bitmap res = ThumbnailUtils.createVideoThumbnail(link, MediaStore.Video.Thumbnails.MINI_KIND);
                if (res != null) {
                    previewBM = res;
                } else { // video isn't local
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(link, new HashMap<String, String>());
                    // this gets frame at 2nd second
                    previewBM = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST);
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
}
