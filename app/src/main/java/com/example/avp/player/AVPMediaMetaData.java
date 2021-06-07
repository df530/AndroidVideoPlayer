package com.example.avp.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AVPMediaMetaData {
    private final String title;
    private final String author;
    private final String uri;
    private final Bitmap previewBM;

    public AVPMediaMetaData(String title, String author, String uri, String previewURL) {
        this.title = title;
        this.author = author;
        this.uri = uri;
        if (previewURL == null) {
            // works for local video
            Bitmap res = ThumbnailUtils.createVideoThumbnail(uri, MediaStore.Video.Thumbnails.MINI_KIND);
            if (res != null) {
                this.previewBM = res;
            }
            else { // video isn't local
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(uri, new HashMap<String, String>());
                // this gets frame at 2nd second
                this.previewBM = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST);
            }
        }
        else {
            this.previewBM = loadImageFromURL(previewURL);
        }
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

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getUri() {
        return uri;
    }

    public Bitmap getPreviewBM() {
        return previewBM;
    }
}
