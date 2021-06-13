package com.example.avp.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.SparseArray;

import com.gdrive.GDriveFileDownloader;
import com.gdrive.GDriveWrapper;
import com.gdrive.GoogleAccountHolder;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ExoPlayerModel {
    private final Context context;
    private final String linkOnVideo;

    public static final int LINK_INCORRECT = 10;

    public ExoPlayerModel(Context context, String linkOnVideo) {
        this.context = context;
        this.linkOnVideo = linkOnVideo.isEmpty()
                ? "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                : linkOnVideo;
        // TODO: load state (speed, play in the background)
    }

    public Observable<MediaSource> getMediaSource() {
        Observable<MediaSource> res;
        try {
            if (isYoutubeUrl(linkOnVideo)) {
                res = getObservableMediaSourceFromYouTube();
            } else if (GDriveFileDownloader.isGDriveURL(linkOnVideo)) {
                res = getObservableMediaSourceFromGDrive();
            } else {
                res = getObservableMediaSourceFromUri();
            }
        } catch (Throwable e) {
            // I know, this is very big clutch, but this is the best solution, that I can do now.
            e.printStackTrace();
            res = Observable.fromCallable(() -> {
                throw e;
            });
        }

        return res;
    }

    private Observable<MediaSource> getObservableMediaSourceFromUri() {
        AVPMediaMetaData meta = new AVPMediaMetaData(new File(linkOnVideo).getName(), null, linkOnVideo, null);
        return Observable.just(new DefaultMediaSourceFactory(context)
                .createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(linkOnVideo))
                        .setTag(meta)
                        .build()));
    }

    private static boolean isYoutubeUrl(String youTubeURl) {
        String pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+";
        return !youTubeURl.isEmpty() && youTubeURl.matches(pattern);
    }

    private Observable<MediaSource> getObservableMediaSourceFromGDrive() {
        PublishSubject<MediaSource> resObservable = PublishSubject.create(); // create a list to save result MediaSource

        GoogleSignInAccount account = GoogleAccountHolder.getInstance().getAccount();
        if (account == null) {
            throw new IllegalStateException("User is not logged in to the google account.");
        }
        GDriveWrapper gDriveWrapper = new GDriveWrapper(context, account);

        final String fileID = GDriveFileDownloader.getGDriveFileIDFromURL(linkOnVideo);
        Task<byte[]> fileTask = gDriveWrapper.getFile(fileID);
        fileTask.addOnSuccessListener(fileBytes -> {

            ByteArrayDataSource dataSource = new ByteArrayDataSource(fileBytes);
            try { // Magic part, exoplayer needs Uri :(
                dataSource.open(new DataSpec(Uri.fromFile(Environment.getExternalStorageDirectory())));
            } catch (IOException e) {
                e.printStackTrace();
            }

            DataSource.Factory factory = () -> dataSource;

            String title = gDriveWrapper.getTitle(fileID);
            String author = gDriveWrapper.getAuthor(fileID);
            String previewURL = gDriveWrapper.getPreviewURL(fileID);

            //TODO: add metadata from gdrive
            //AVPMediaMetaData meta = new AVPMediaMetaData(title, author, null, previewURL);
            MediaSource fileSource = new ProgressiveMediaSource
                    .Factory(factory)
                    //        .setTag(meta)
                    .createMediaSource(MediaItem.fromUri(dataSource.getUri()));
            resObservable.onNext(fileSource);
        });

        return resObservable;
    }

    @SuppressLint("StaticFieldLeak")
    private Observable<MediaSource> getObservableMediaSourceFromYouTube() {
        PublishSubject<MediaSource> resObservable = PublishSubject.create(); // create a list to save result MediaSource
        new YouTubeExtractor(context) {
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
                    int videoTag = getBestPossibleVideoTag(ytFiles);
                    int audioTag = 140; // audio tag for m4a

                    AVPMediaMetaData mediaMetadata = new AVPMediaMetaData(videoMeta.getTitle(), videoMeta.getAuthor(), linkOnVideo,
                            videoMeta.getMqImageUrl());

                    MediaSource audioSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(ytFiles.get(audioTag).getUrl())
                                    .setTag(mediaMetadata)
                                    .build());

                    MediaSource videoSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(ytFiles.get(videoTag).getUrl())
                                    .setTag(mediaMetadata)
                                    .build());
                    MediaSource resMediaSource = new MergingMediaSource(true, videoSource, audioSource);
                    resObservable.onNext(resMediaSource);
                } else {
                    resObservable.onError(new RuntimeException("Cant load this youtube link: " + linkOnVideo));
                }
            }
        }.extract(linkOnVideo, false, false);

        return resObservable;
    }
}
