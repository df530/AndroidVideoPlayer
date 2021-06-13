package com.gdrive;

import android.content.Context;
import android.net.Uri;

import com.example.avp.model.VideoModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GDriveWrapper {

    private final Drive driveService;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    public GDriveWrapper(Context context, GoogleSignInAccount account) {
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE));
        credential.setSelectedAccount(account.getAccount());
        driveService =
                new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .setApplicationName("AndroidVideoPlayer")
                .build();
    }

    public Task<InputStream> getStream(final String fileID) {
        return Tasks.call(mExecutor, () -> driveService.files().get(fileID).executeMediaAsInputStream());
    }

    public long getSize(String fileID) {
        Task<Long> task = Tasks.call(mExecutor, () -> driveService.files().get(fileID).executeMedia().getHeaders().getContentLength());
        while (!task.isComplete()) {} //TODO: сделать это адекватно
        return task.getResult();
    }

    public Task<GDriveFile> getFile(String fileID) {
        return Tasks.call(mExecutor, () -> {
             File file = driveService.files().get(fileID).setFields("*").execute();
             System.out.println(file.getName());
             return new GDriveFile(
                     driveService.files().get(fileID).executeMediaAsInputStream(),
                     file.getName(),
                     null,
                     file.getThumbnailLink(),
                     file.getSize()
             );
        });
    }

    public Task<ArrayList<VideoModel>> getVideoList() {
        return Tasks.call(mExecutor, () -> {
            ArrayList<VideoModel> videos = new ArrayList<>();
            String pageToken = null;
            do {
                FileList files = driveService
                        .files()
                        .list()
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name, thumbnailLink, videoMediaMetadata)")
                        .setPageToken(pageToken)
                        .execute();
                for (File file : files.getFiles()) {
                    if (file.getVideoMediaMetadata() != null) {
                        VideoModel videoModel = new VideoModel();
                        videoModel.setBooleanSelected(false);
                        videoModel.setGDriveFile(true);
                        videoModel.setName(file.getName());
                        videoModel.setStrPath("drive.google.com/file/d/" + file.getId());
                        videoModel.setStrThumb(file.getThumbnailLink());
                        videos.add(videoModel);
                    }
                }
                pageToken = files.getNextPageToken();
            } while (pageToken != null);
            return videos;
        });
    }
}
