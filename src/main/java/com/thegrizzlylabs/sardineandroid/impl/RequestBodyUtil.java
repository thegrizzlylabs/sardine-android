package com.thegrizzlylabs.sardineandroid.impl;


import android.content.ContentResolver;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by n8fr8 on 12/29/17.
 */

public class RequestBodyUtil {

    public static RequestBody create(final ContentResolver cr, final Uri uri, final MediaType mediaType) {


        return new RequestBody() {

            InputStream inputStream = null;

            private void init ()
            {
                try {
                    inputStream = cr.openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                try {
                    init();
                    long length = inputStream.available();
                    inputStream.close();
                    return length;
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public synchronized void writeTo(BufferedSink sink) throws IOException {

                init();
                Source source = Okio.source(inputStream);

                try {
                    sink.writeAll(source);

                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }
}