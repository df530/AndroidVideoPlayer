package com.gdrive;

import android.content.Context;
import android.net.Uri;

import com.example.avp.model.VideoModel;
import com.example.avp.player.AVPMediaMetaData;
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
import java.util.Date;
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

    private String getFilePath(File file) throws IOException {
        String folderPath = "";
        String fullFilePath = null;

        List<String> parentIdList = file.getParents();
        List<String> folderList = new ArrayList<String>();

        List<String> finalFolderList = getFoldersList(driveService, parentIdList, folderList);
        Collections.reverse(finalFolderList);

        for (String folder : finalFolderList) {
            folderPath += "/" + folder;
        }

        fullFilePath = folderPath + "/" + file.getName();

        return fullFilePath;
    }

    private List<String> getFoldersList(Drive drive, List<String> parentIdList, List<String> folderList) throws IOException {
        for (int i = 0; i < parentIdList.size(); i++) {
            String id = parentIdList.get(i);

            File file = drive.files().get(id).execute();
            folderList.add(file.getName());

            if (!(file.getParents().isEmpty())) {
                List<String> parentReferenceslist2 = file.getParents();
                getFoldersList(drive, parentReferenceslist2, folderList);
            }
        }
        return folderList;
    }

    public Task<List<AVPMediaMetaData>> getVideoListTask() {
        return Tasks.call(mExecutor, () -> getVideoList());
    }

    public List<AVPMediaMetaData> getVideoList() {
        try {
            List<AVPMediaMetaData> videos = new ArrayList<>();
            String pageToken = null;
            do {
                FileList files = driveService
                        .files()
                        .list()
                        .setQ("mimeType='video/mp4'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, createdTime, name, thumbnailLink, videoMediaMetadata)")
                        .setPageToken(pageToken)
                        .execute();
                for (File file : files.getFiles()) {
                    if (file.getVideoMediaMetadata() != null) {
                        AVPMediaMetaData metaData = new AVPMediaMetaData(
                                file.getName(),
                                null,
                                "https://drive.google.com/file/d/" + file.getId(),
                                file.getThumbnailLink(),
                                null, //getFilePath(file),
                                null,
                                file.getCreatedTime() == null ? null : new Date(file.getCreatedTime().getValue())
                        );
                        videos.add(metaData);
                    }
                }
                pageToken = files.getNextPageToken();
            } while (pageToken != null);
            return videos;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
