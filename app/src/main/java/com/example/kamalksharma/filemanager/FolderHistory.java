package com.example.kamalksharma.filemanager;

import android.os.Environment;

import java.io.File;
import java.util.Stack;

public class FolderHistory {

    Stack<String> folderHistory;
    public FolderHistory(){
        folderHistory = new Stack<>();
    }
    public Stack<String> getHistory()
    {
        File rootFilePath = Environment.getExternalStorageDirectory();
        folderHistory.add(rootFilePath.toString());
        return folderHistory;
    }
    public Stack<String> getDropBoxHistory()
    {
        String rootFilePath = "";
        folderHistory.add(rootFilePath);
        return folderHistory;
    }
}
