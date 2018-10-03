package com.example.kamalksharma.filemanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.telecom.Call;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.security.auth.callback.Callback;

public class DropboxFileTask extends AsyncTask <Void, Void,Boolean> {

    private static final String ACCESS_TOKEN = "Qzmg3GEhnsAAAAAAAAAEs05bMlnYeXIclE1nFUyF1-nfnFVhCXPpvaTCdF0EU94n";

    private final Context mcontext;
    private final Activity mactivity;
    private final Callback mcallback;
    private DataModel mDropBoxdata;
    private Exception mexception;
    ProgressDialog progressDialog;
    DbxClientV2 mDbxClient;
    String mfileOption;


    public interface Callback{
        void onDownload(Boolean result);
        void onError(Exception e);
    }

    DropboxFileTask(Context context,Activity activity,String fileOption ,DataModel DropBoxdata, Callback callback){
        this.mactivity = activity;
        this.mcontext = context;
        this.mfileOption = fileOption;
        this.mcallback = callback;
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/KakesApp").build();
        mDbxClient = new DbxClientV2(config, ACCESS_TOKEN);
        this.mDropBoxdata = DropBoxdata;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mfileOption.equals("Encrypted_")){
            progressDialog = ProgressDialog.show(mactivity,
                    "Wait",
                    "File Encrypting");
        }
        else{
            progressDialog = ProgressDialog.show(mactivity,
                    "Wait",
                    "File Decrypting");
        }

    }
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        super.onPreExecute();
        if(mexception!=null){
            mcallback.onError(mexception);
        }
        else{
            progressDialog.dismiss();
            mcallback.onDownload(aBoolean);
        }
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file;
            if(mfileOption.equals("Encrypted_")){
                 file = new File(path + mfileOption +mDropBoxdata.getFileName());
            }

            else{
                file = new File(path + mfileOption +mDropBoxdata.getFileName());
            }
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    mexception = new RuntimeException("Unable to create directory: " + path);
                }
            } else if (!path.isDirectory()) {
                mexception = new IllegalStateException("Download path is not a directory: " + path);
                return false;
            }

            // Download the file.
            try (OutputStream outputStream = new FileOutputStream(file)) {
                InputStream inputStream = new FileInputStream(file);
                mDbxClient.files().download(mDropBoxdata.getFilePath().toLowerCase()).download(outputStream);
                String basePath = mDropBoxdata.getFilePath();
                String[] array = basePath.split("/");
                String input="/";
                for(int i=1;i<array.length-1;i++){
                    input = input + array[i]+"/";
                }
                mDbxClient.files().uploadBuilder(input + mfileOption + mDropBoxdata.getFileName())
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            }

            file.delete();
            return true;
        } catch (DbxException | IOException e) {
            mexception = e;
        }
        return false;
    }
}

