package com.example.kamalksharma.filemanager.Model;

import java.io.File;

public class DataModel {
    private String mfileName;
    private String mfilePath;
    private boolean misFolder;

    public String getFileName() {
        return mfileName;
    }

    public void setFileName(String mfileName) {
        this.mfileName = mfileName;
    }

    public String getFilePath() {
        return mfilePath;
    }

    public void setFilePath(String mfilePath) {
        this.mfilePath = mfilePath;
    }

    public boolean isFolder() {
        return misFolder;
    }

    public void setIsFolder(boolean misFolder) {
        this.misFolder = misFolder;
    }
}