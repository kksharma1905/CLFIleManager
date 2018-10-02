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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            File file = new File(path +"_Encypted" +mDropBoxdata.getFileName());
          //  File file = new File(path, metadata.getName());



            // Make sure the Downloads directory exists.
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
                mclient.files().download(mDropBoxdata.getFilePath().toLowerCase()).download(outputStream);
            }

            // Tell android about the file
            return true;
        } catch (DbxException | IOException e) {
            mexception = e;
        }
        return false;
    }
}

