package com.example.avp.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

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

    public ExoPlayerModel(Context context, String linkOnVideo) {
        this.context = context;
        this.linkOnVideo = linkOnVideo.isEmpty()
                ? "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                : linkOnVideo;
        // TODO: load state (speed, play in the background)
    }

    public Observable<MediaSource> getMediaSource() {
        Observable<MediaSource> res;
        if (isYoutubeUrl(linkOnVideo)) {
            res = getMediaSourceFromYouTube();
        }
        /*
        else if (isGoogleDriveLink(linkOnVideo)) {
            ...
        }
         */
        else {
            res = Observable.fromCallable(() ->
                    new DefaultMediaSourceFactory(context).createMediaSource(MediaItem.fromUri(Uri.parse(linkOnVideo))));
        }

        return res.doOnError(e -> {
            // TODO: send message to the application, that link is incorrect
        });
    }

    private static boolean isYoutubeUrl(String youTubeURl) {
        String pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+";
        return !youTubeURl.isEmpty() && youTubeURl.matches(pattern);
    }

    @SuppressLint("StaticFieldLeak")
    private Observable<MediaSource> getMediaSourceFromYouTube() {
        PublishSubject<MediaSource> res = PublishSubject.create(); // create a list to save result MediaSource
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
                    // 720, 1080, 480
                    int videoTag = getBestPossibleVideoTag(ytFiles);
                    int audioTag = 140; // audio tag for m4a
                    MediaSource audioSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(audioTag).getUrl()));
                    MediaSource videoSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(videoTag).getUrl()));
                    res.onNext(new MergingMediaSource(true, videoSource, audioSource));
                }
            }
        }.extract(linkOnVideo, true, true);

        return res;
    }
}
