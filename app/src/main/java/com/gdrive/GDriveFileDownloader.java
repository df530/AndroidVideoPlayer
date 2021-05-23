package com.gdrive;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GDriveFileDownloader implements FileDownloader {

    final Drive driveService;

    public GDriveFileDownloader(GoogleSignInAccount account) throws GeneralSecurityException, IOException {
        driveService = GDriveServiceFactory.getGDriveService(account);
    }

    @Override
    public OutputStream downloadFullFile(String fileURL) throws IOException {
        Drive.Files.Get request = getDriveGetRequest(fileURL);
        OutputStream resultStream = new ByteArrayOutputStream();
        request.executeMediaAndDownloadTo(resultStream);
        return resultStream;
    }

    @Override
    public OutputStream downloadPartOfFile(String fileURL, long leftByteBound, long rightByteBound) throws IOException {
        Drive.Files.Get request = getDriveGetRequest(fileURL);
        HttpHeaders rangeHeader = new HttpHeaders();
        rangeHeader.setRange("bytes=" + leftByteBound + "-" + rightByteBound);
        request.setRequestHeaders(rangeHeader);
        OutputStream resultStream = new ByteArrayOutputStream();
        request.executeMediaAndDownloadTo(resultStream);
        return resultStream;
    }

    private Drive.Files.Get getDriveGetRequest(String fileURL) throws IOException {
        if (!validateGDriveURL(fileURL)) {
            throw new IllegalArgumentException("Illegal URL format.");
        }
        String fileID = getGDriveFileIDFromURL(fileURL);
        return driveService.files().get(fileID);
    }

    static public String getGDriveFileIDFromURL(String fileURL) {
        Pattern pattern = Pattern.compile("/d/(.+)?/", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileURL);
        if (!matcher.find()) {
            throw new IllegalArgumentException("It's not GDrive file URL format.");
        }
        return matcher.group(1);
    }

    static public boolean validateGDriveURL(String fileURL) {
        Pattern pattern = Pattern.compile("(https?://)?(www.)?drive.google.com/file/d/(.+)", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(fileURL).matches();
    }

}
