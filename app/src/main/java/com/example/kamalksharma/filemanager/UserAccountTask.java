package com.example.kamalksharma.filemanager;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;

public class UserAccountTask extends AsyncTask <Void, Void, ListFolderResult>{
    private static final String ACCESS_TOKEN = "Qzmg3GEhnsAAAAAAAAAEs05bMlnYeXIclE1nFUyF1-nfnFVhCXPpvaTCdF0EU94n";
    private DbxClientV2 dbxClient;
    private TaskDelegate  delegate;
    private Exception error;
    private String mpath;
    public interface TaskDelegate {
        void onAccountReceived(ListFolderResult account);
        void onError(Exception error);
    }

    public UserAccountTask(String mpath, TaskDelegate delegate){
        this.mpath = mpath;
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/KakesApp").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        this.dbxClient = client;
        this.delegate = delegate;
    }

    @Override
    protected ListFolderResult doInBackground(Void... params) {
        try {
            //get the users FullAccount
//            return dbxClient.users().getCurrentAccount();
            return dbxClient.files().listFolder(mpath);
        } catch (DbxException e) {
            e.printStackTrace();
            error = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(ListFolderResult account) {
        super.onPostExecute(account);

        if (account != null && error == null){
            //User Account received successfully
            delegate.onAccountReceived(account);
        }
        else {
            // Something went wrong
            delegate.onError(error);
        }
    }
}
