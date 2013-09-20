package com.cantwellcode.cantwellgallery;

import java.io.File;

/**
 * Created by Chris on 9/20/13.
 * Contains information about directories represented in a database
 * Used for passing directory information during touch events
 */
public class DirectoryData {
    private final long      mDirectoryID;
    private final String    mDirectoryPath;

    public long getDirectoryID() {
        return mDirectoryID;
    }
    public String getDirectoryPath() {
        return mDirectoryPath;
    }
    public DirectoryData(long directoryID, String directoryPath) {
        this.mDirectoryID = directoryID;
        this.mDirectoryPath = directoryPath;
        File file = new File(directoryPath);
        if (!file.isDirectory()) throw new IllegalArgumentException("Must provide directory path.");
    }
}
