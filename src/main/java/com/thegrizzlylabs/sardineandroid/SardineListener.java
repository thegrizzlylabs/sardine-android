package com.thegrizzlylabs.sardineandroid;

public interface SardineListener {

    public void transferred (long bytes);

    public boolean continueUpload ();
}
