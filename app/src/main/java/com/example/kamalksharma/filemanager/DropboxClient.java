package com.example.kamalksharma.filemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class DropboxClient extends AsyncTask {
    private static final String ACCESS_TOKEN = "Qzmg3GEhnsAAAAAAAAAEs05bMlnYeXIclE1nFUyF1-nfnFVhCXPpvaTCdF0EU94n";
    DbxClientV2 dbxClient;
    ListFolderResult result = null;
    CharSequence[] cs;
    ArrayList<String> arrayList;
//    private DbxClientV2 dbxClient;

    public DropboxClient(){
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/KakesApp").build();
        dbxClient = new DbxClientV2(config,ACCESS_TOKEN );
    }

    public static DbxClientV2 getClient(String ACCESS_TOKEN) throws DbxException {
        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/KakesApp").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        return client;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String path = "";
        TreeMap<String, Metadata> children = new TreeMap<String, Metadata>();

        try {
            try {
                result = dbxClient.files()
                        .listFolder(path);
            } catch (ListFolderErrorException ex) {
                ex.printStackTrace();
            }

            List<Metadata> list = result.getEntries();
            cs = new CharSequence[list.size()];
            arrayList = new ArrayList<>();
            arrayList.add("/");
            while (true) {
                int i = 0;
                for (Metadata md : result.getEntries()) {
                    if (md instanceof DeletedMetadata) {
                        children.remove(md.getPathLower());
                    } else {
                        String fileOrFolder = md.getPathLower();
                        children.put(fileOrFolder, md);
                        if(!fileOrFolder.contains("."))
                            arrayList.add(fileOrFolder);
                    }
                    i++;
                }

                if (!result.getHasMore()) break;

                try {
                    result = dbxClient.files()
                            .listFolderContinue(result.getCursor());
                } catch (ListFolderContinueErrorException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    protected void onPostExecute(Object o) {

    }
}
