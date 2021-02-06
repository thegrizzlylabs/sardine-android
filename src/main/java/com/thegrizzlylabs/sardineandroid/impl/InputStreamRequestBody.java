package com.thegrizzlylabs.sardineandroid.impl;

import com.thegrizzlylabs.sardineandroid.InputStreamProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class InputStreamRequestBody extends RequestBody {

    private final InputStreamProvider inputStreamProvider;

    public InputStreamRequestBody(InputStreamProvider inputStreamProvider) {
        this.inputStreamProvider = inputStreamProvider;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return inputStreamProvider.getContentType();
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = inputStreamProvider.getInputStream();
            bufferedSink.writeAll(Okio.source(inputStream));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
