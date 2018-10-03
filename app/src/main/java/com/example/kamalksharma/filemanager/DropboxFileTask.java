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

    private final Context mcontext;
    private final Activity mactivity;
    private final Callback mcallback;
    private  final DbxClientV2 mclient;
    private DataModel mDropBoxdata;
    private Exception mexception;
    ProgressDialog progressDialog;



    public interface Callback{
        void onDownload(Boolean result);
        void onError(Exception e);
    }

    DropboxFileTask(Context mcontext,Activity mactivity ,DbxClientV2 mclient,DataModel mDropBoxdata, Callback mcallback){
        this.mactivity = mactivity;
        this.mcontext = mcontext;
        this.mclient = mclient;
        this.mcallback = mcallback;

        this.mDropBoxdata = mDropBoxdata;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(mactivity,
                "ProgressDialog",
                "Wait for some seconds");
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
            File file = new File(path +"Encypted__" +mDropBoxdata.getFileName());

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
                mclient.files().download(mDropBoxdata.getFilePath().toLowerCase()).download(outputStream);
                String basePath = mDropBoxdata.getFilePath();
                String[] array = basePath.split("/");
                String input="/";
                for(int i=1;i<array.length-1;i++){
                    input = input + array[i]+"/";
                }
                mclient.files().uploadBuilder(input + "Encrypted_" + mDropBoxdata.getFileName())
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            }
            return true;
        } catch (DbxException | IOException e) {
            mexception = e;
        }
        return false;
    }
}

