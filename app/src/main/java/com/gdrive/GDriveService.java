package com.gdrive;

import android.content.Context;

import com.example.avp.player.AVPMediaMetaData;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GDriveService {

    private final GDriveWrapper driveService;

    public GDriveService(Context context, GoogleSignInAccount account) {
        driveService = new GDriveWrapper(context, account);
    }

    public Task<GDriveFile> getFile(String fileURL) {
        if (!isGDriveURL(fileURL)) {
            throw new IllegalArgumentException("Illegal URL format.");
        }
        String fileID = getGDriveFileIDFromURL(fileURL);
        return driveService.getFile(fileID);
    }

    public long getSizeOfFile(String fileURL) {
        return driveService.getSize(getGDriveFileIDFromURL(fileURL));
    }

    static public String getGDriveFileIDFromURL(String fileURL) {
        Pattern pattern = Pattern.compile("/d/([^/]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileURL);
        if (!matcher.find()) {
            throw new IllegalArgumentException("It's not GDrive file URL format.");
        }
        return matcher.group(1);
    }

    static public boolean isGDriveURL(String fileURL) {
        Pattern pattern = Pattern.compile("(https?://)?(www\\.)?drive\\.google\\.com/file/d/(.+)/?(.+)?", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(fileURL).matches();
    }

    public Task<List<AVPMediaMetaData>> getUserVideosListTask() {
        return driveService.getVideoListTask();
    }

    public List<AVPMediaMetaData> getUserVideosList() {
        return driveService.getVideoList();
    }
}
