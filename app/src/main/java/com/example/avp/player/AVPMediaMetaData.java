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
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import lombok.Getter;

public class AVPMediaMetaData implements Parcelable {
    @Getter
    private final String title;
    @Getter
    private final String author;
    @Getter
    private final String link;
    @Getter
    private final Bitmap previewBM;


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof AVPMediaMetaData) {
            AVPMediaMetaData other = (AVPMediaMetaData) obj;
            return (author == null && other.author == null || author.equals(other.author)) &&
                    (title == null && other.title == null || title.equals(other.title)) &&
                    (link == null && other.link == null || link.equals(other.link));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, link);
    }

    private AVPMediaMetaData(String title, String author, String link, Bitmap previewBM) {
        this.title = title;
        this.author = author;
        this.link = link;
        this.previewBM = previewBM;
    }

    public AVPMediaMetaData(String title, String author, String link, String previewURL) {
        this.title = title;
        this.author = author;
        this.link = link;
        if (previewURL == null) {
            // works for local video
            Bitmap res = ThumbnailUtils.createVideoThumbnail(link, MediaStore.Video.Thumbnails.MINI_KIND);
            if (res != null) {
                this.previewBM = res;
            } else { // video isn't local
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(link, new HashMap<String, String>());
                // this gets frame at 2nd second
                this.previewBM = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST);
            }
        } else {
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


    // Parsable implementation
    public static final Creator<AVPMediaMetaData> CREATOR = new Creator<AVPMediaMetaData>() {
        @Override
        public AVPMediaMetaData createFromParcel(Parcel source) {
            String title = source.readString();
            String author = source.readString();
            String uri = source.readString();
            byte[] bytes = new byte[source.readInt()];
            source.readByteArray(bytes);
            Bitmap previewBM = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return new AVPMediaMetaData(title, author, uri, previewBM);
        }

        @Override
        public AVPMediaMetaData[] newArray(int size) {
            return new AVPMediaMetaData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(link);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        previewBM.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        dest.writeInt(bytes.length);
        dest.writeByteArray(bytes);
    }
}
