package com.example.avp.player;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamDataSource implements DataSource {

    private TransferListener listener = null;
    private DataSpec dataSpec;
    private InputStream inputStream;
    private long bytesRemaining;
    private boolean opened;

    public InputStreamDataSource(DataSpec dataSpec, InputStream inputStream) {
        this.dataSpec = dataSpec;
        this.inputStream = inputStream;
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        listener = transferListener;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        try {
            long skipped = inputStream.skip(dataSpec.position);
            if (skipped < dataSpec.position)
                throw new EOFException();

            if (dataSpec.length != C.LENGTH_UNSET) {
                bytesRemaining = dataSpec.length;
            } else {
                bytesRemaining = inputStream.available();
                if (bytesRemaining == Integer.MAX_VALUE)
                    bytesRemaining = C.LENGTH_UNSET;
            }
        } catch (IOException e) {
            throw new IOException(e);
        }

        opened = true;
        if (listener != null) {
            listener.onTransferStart(this, dataSpec, true);
        }
        return bytesRemaining;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        } else if (bytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT;
        }

        int bytesRead;
        int bytesToRead = bytesRemaining == C.LENGTH_UNSET ? readLength
                : (int) Math.min(bytesRemaining, readLength);
        bytesRead = inputStream.read(buffer, offset, bytesToRead);

        if (bytesRead == -1) {
            if (bytesRemaining != C.LENGTH_UNSET) {
                throw new IOException(new EOFException());
            }
            return C.RESULT_END_OF_INPUT;
        }
        if (bytesRemaining != C.LENGTH_UNSET) {
            bytesRemaining -= bytesRead;
        }
        if (listener != null) {
            listener.onBytesTransferred(this, dataSpec, true, bytesRead);
        }
        return bytesRead;
    }

    @Override
    public Uri getUri() {
        return dataSpec.uri;
    }

    @Override
    public void close() throws IOException {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            inputStream = null;
            if (opened) {
                opened = false;
            }
            if (listener != null) {
                listener.onTransferEnd(this, dataSpec, true);
            }
        }
    }

}