package com.thegrizzlylabs.sardineandroid;

import java.io.InputStream;

import okhttp3.MediaType;

public interface InputStreamProvider {
    InputStream getInputStream();
    MediaType getContentType();
}
