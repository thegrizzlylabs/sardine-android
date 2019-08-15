package com.thegrizzlylabs.sardineandroid.impl;


import android.content.ContentResolver;
import android.net.Uri;

import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.SardineListener;

import java.io.File;
import java.io.FileInputStream;
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

    private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE

    public static RequestBody create(final ContentResolver cr, final Uri uri, final long contentLength, final MediaType mediaType, final SardineListener listener) {

        return new RequestBody() {

            InputStream inputStream = null;
            SardineListener mListener = null;

            private void init ()
            {
                try {
                    inputStream = cr.openInputStream(uri);
                    mListener = listener;
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
                return contentLength;
            }

            @Override
            public synchronized void writeTo(BufferedSink sink) throws IOException {

                init();
                Source source = Okio.source(inputStream);

                if (mListener == null) {
                    sink.writeAll(source);
                }
                else {

                    try {

                        long total = 0;
                        long read;

                        while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1 && (mListener != null && mListener.continueUpload())) {
                            total += read;
                            if (mListener != null)
                                mListener.transferred(total);
                            sink.flush();
                        }


                    } finally {
                        Util.closeQuietly(source);
                    }
                }

            }
        };
    }

    public static RequestBody create(final File fileSource, final MediaType mediaType, final SardineListener listener) {

        return new RequestBody() {

            InputStream inputStream = null;

            private void init ()
            {
                try {
                    inputStream = new FileInputStream(fileSource);
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
                return fileSource.length();
            }

            @Override
            public synchronized void writeTo(BufferedSink sink) throws IOException {

                init();
                Source source = Okio.source(inputStream);

                if (listener == null)
                {
                    sink.writeAll(source);
                }
                else {
                    try {

                        long total = 0;
                        long read;

                        while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                            total += read;
                            listener.transferred(total);
                        }

                        sink.flush();

                    } finally {
                        Util.closeQuietly(source);
                    }
                }

            }

        };
    }
}