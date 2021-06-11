package com.gdrive;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GDriveFileDownloader implements FileDownloader {

    private final GDriveWrapper driveService;

    public GDriveFileDownloader(AppCompatActivity context, GoogleSignInAccount account) {
        driveService = new GDriveWrapper(context, account);
    }

    @Override
    public Task<byte[]> downloadFullFile(String fileURL) {
        if (!isGDriveURL(fileURL)) {
            throw new IllegalArgumentException("Illegal URL format.");
        }
        String fileID = getGDriveFileIDFromURL(fileURL);
        return driveService.getFile(fileID);
    }

    @Override
    public Task<InputStream> downloadPartOfFile(String fileURL, long leftByteBound, long rightByteBound) {
        throw new UnsupportedOperationException();
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

}
