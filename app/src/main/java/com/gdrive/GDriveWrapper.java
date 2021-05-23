package com.gdrive;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GDriveWrapper {

    private final Drive driveService;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    public GDriveWrapper(Drive driveService) {
        this.driveService = driveService;
    }

    public Task<Void> saveFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            File file = driveService.files().get(fileId).execute();
            System.out.println("!!!!!" + file.getName());
            return null;
        });
    }
}
