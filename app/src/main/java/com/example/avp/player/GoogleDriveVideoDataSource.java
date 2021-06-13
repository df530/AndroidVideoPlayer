package com.example.avp.player;

import android.net.Uri;

import com.gdrive.GDriveService;
import com.gdrive.GDriveWrapper;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.gms.tasks.Task;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class GoogleDriveVideoDataSource implements DataSource {
    private DataSpec dataSpec;
    private InputStream inputStream;

    private long videoSize;
    private long bytesRemaining = 0;
    private long bytesRead = 0;

    private TransferListener transferListener;
    private GDriveService driveService;

    public GoogleDriveVideoDataSource(DataSpec dataSpec, GDriveService driveService) {
        this.dataSpec = dataSpec;
        this.driveService = driveService;
        this.videoSize = driveService.getSizeOfFile(dataSpec.uri.toString());
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        this.transferListener = transferListener;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        try {
            if (inputStream != null && bytesRead <= dataSpec.position) {
                long skipped = inputStream.skip(dataSpec.position - bytesRead);
                bytesRead += skipped;
            } else {
                inputStream = getInputStream(dataSpec.uri);
                long skipped = inputStream.skip(dataSpec.position);
                bytesRead = skipped;
            }

            if (bytesRead < dataSpec.position)
                throw new EOFException();

            bytesRemaining = videoSize - bytesRead;
        } catch (IOException e) {
            throw new IOException(e);
        }

        if (transferListener != null) {
            transferListener.onTransferStart(this, dataSpec, true);
        }

        return bytesRemaining;
    }

    private InputStream getInputStream(Uri uri) {
        Task<InputStream> task = driveService.getDownloadStreamOnFile(uri.toString());
        while (!task.isComplete()) {} // sorry...
        return task.getResult();
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        } else if (bytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT;
        }

        int newBytesToRead = (int) Math.min(bytesRemaining, readLength);
        int newBytesRead = inputStream.read(buffer, offset, newBytesToRead);

        if (newBytesRead == -1) {
            return C.RESULT_END_OF_INPUT;
        }
        bytesRead += newBytesRead;
        bytesRemaining -= newBytesRead;

        if (transferListener != null) {
            transferListener.onBytesTransferred(this, dataSpec, true, newBytesRead);
        }

        return newBytesRead;
    }

    @Override
    public Uri getUri() {
        return dataSpec.uri;
    }

    @Override
    public void close() throws IOException {
        // ничего не делаю, чтобы в случае переоткрытия с перемоткой вперед не делать новый запрос к API
        if (transferListener != null) {
            transferListener.onTransferEnd(this, dataSpec, true);
        }
    }
}