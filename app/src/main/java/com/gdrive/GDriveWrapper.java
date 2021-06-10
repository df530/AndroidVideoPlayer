package com.gdrive;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GDriveWrapper {

    private final Drive driveService;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    public GDriveWrapper(AppCompatActivity context, GoogleSignInAccount account) {
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE));
        credential.setSelectedAccount(account.getAccount());
        driveService =
                new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .setApplicationName("AndroidVideoPlayer")
                .build();
    }

    public Task<byte[]> getFile(final String fileID) {
        return Tasks.call(mExecutor, () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            driveService.files().get(fileID).executeMediaAndDownloadTo(outputStream);
            return outputStream.toByteArray();
        });
    }

    public Task<FileList> queryFiles() {
        return Tasks.call(mExecutor, () -> driveService.files().list().setSpaces("drive").execute());
    }
}
