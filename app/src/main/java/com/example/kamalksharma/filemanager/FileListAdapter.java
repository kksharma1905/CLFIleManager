package com.example.kamalksharma.filemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter <FileListAdapter.FileListViewHolder> {
    private static final String ACCESS_TOKEN = "Qzmg3GEhnsAAAAAAAAAEs05bMlnYeXIclE1nFUyF1-nfnFVhCXPpvaTCdF0EU94n";
    private File[] mlistMembers;
    private ArrayList<DataModel> mFileList;
    FileListViewActivity mAcivity;
    Context mContext;
    boolean mIsLocalStorage;
    ProgressDialog progressDialog;

    public FileListAdapter(Context context, FileListViewActivity activity, boolean isLocalStorage){
        this.mContext = context;
        mAcivity = activity;
    }
    @NonNull
    @Override
    public FileListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflate = LayoutInflater.from(viewGroup.getContext());
        View view = inflate.inflate(R.layout.list_fileitem_layout, viewGroup, false);
        return new FileListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileListViewHolder fileListViewHolder, int i) {
        String temporaryFileName = mFileList.get(i).getFileName();
        fileListViewHolder.fileName.setText(temporaryFileName);
        fileListViewHolder.fileIcon.setImageResource(getImageId(temporaryFileName));

    }
public int getImageId(String name){
        if(getExtension(name).equals("image")){
            String uri = "@drawable/fileicon";
            return mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
        }

        else if(getExtension(name).equals("pdf")) {
            String uri = "@drawable/pdficon";
            return mContext.getResources().getIdentifier(uri , null, mContext.getPackageName());

        }
        else if(getExtension(name).equals("apk")){
            String uri = "@drawable/apkicon";
            return mContext.getResources().getIdentifier(uri , null, mContext.getPackageName());
        }
        else
        {
            String uri = "@drawable/folder";
            return mContext.getResources().getIdentifier(uri , null, mContext.getPackageName());
        }

}
    public String getExtension(String fileName)
    {
        int l = fileName.length();

        if(l>4&&(fileName.substring(l-3,l).equals("png")||fileName.substring(l-4,l).equals("jpeg")||fileName.substring(l-3,l).equals("jpg"))){
            return "image";
        }
        else if(l>4&&fileName.substring(l-3,l).equals("pdf")){
            return "pdf";
        }
        else if(l>4&&fileName.substring(l-3,l).equals("apk")){
            return "apk";
        }
        else return "folder";
    }


    @Override
    public int getItemCount() {
        return mFileList.size();
    }


    public void setListContent(ArrayList<DataModel> listModel,boolean isLocalStorage) {
        this.mFileList = listModel;
        this.mIsLocalStorage = isLocalStorage;
    }

    public class FileListViewHolder extends RecyclerView.ViewHolder  implements RecyclerView.OnClickListener,View.OnLongClickListener{
        ImageView fileIcon;
        TextView fileName;
        ImageView folderArrowIcon;


        public FileListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.fileName);
            folderArrowIcon = itemView.findViewById(R.id.folderArrowIcon);
        }

        @Override
        public void onClick(View v) {
            mAcivity.folderHistory.push(mFileList.get(getAdapterPosition()).getFilePath());
            String filePath = mFileList.get(getAdapterPosition()).getFilePath();
            if(getExtension(mFileList.get(getAdapterPosition()).getFileName())){
                Toast.makeText(mContext.getApplicationContext(),"This is File, Method not implemeted",Toast.LENGTH_LONG).show();
                return;
            }
            if(mIsLocalStorage){
                mAcivity.populateRecyclerViewValues(mFileList.get(getAdapterPosition()).getFilePath());
            }
            else
            {
                mAcivity.getDropboxList(mFileList.get(getAdapterPosition()).getFilePath());
            }
        }

        public boolean getExtension(String fileName)
        {
            int l = fileName.length();

            if(fileName.substring(l-3,l).equals("png")||fileName.substring(l-3,l).equals("jpg")||fileName.substring(l-3,l).equals("PNG")||fileName.substring(l-3,l).equals("pdf")||fileName.substring(l-3,l).equals("PDF")||fileName.substring(l-3,l).equals("apk")){
                return true;
            }
            else if(fileName.substring(l-4,l).equals("jpeg")){
                return true;
            }
            else return false;

        }


        @Override
        public boolean onLongClick(View v) {

            if(!getExtension(mFileList.get(getAdapterPosition()).getFileName())){
                Toast.makeText(mContext.getApplicationContext(),"This is Folder, Method not implemeted",Toast.LENGTH_LONG).show();
                return true;
            }
            final CharSequence[] items = {"Encryption", "Decryption"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Select The Options");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    String basePath = mFileList.get(getAdapterPosition()).getFilePath();
                    String[] array = basePath.split("/");
                    String input="/";
                    for(int i=1;i<array.length-1;i++){
                        input = input + array[i]+"/";
                    }
                    if(item==0){
                        if(mIsLocalStorage){
                            Toast.makeText(mContext.getApplicationContext(),"Encrypting",Toast.LENGTH_LONG).show();

                            try {
                                downloadFile(input,"Encrypting_");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            downloadDBFile();
                        }
                    }
                    else{
                        Toast.makeText(mContext.getApplicationContext(),"Decrypting",Toast.LENGTH_LONG).show();
                        try {
                            downloadFile(input,"Decypting_");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            builder.show();
            return true;
        }

        public void downloadDBFile(){
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/KakesApp").build();
            DbxClientV2 mDbxClient = new DbxClientV2(config, ACCESS_TOKEN);
//            File path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS);
//            File file = new File(path +"Encypted_" +mFileList.get(getAdapterPosition()).getFileName() );
          new DropboxFileTask(mContext,mAcivity,mDbxClient,mFileList.get(getAdapterPosition()), new DropboxFileTask.Callback() {
              @Override
              public void onDownload(Boolean result) {
                  if (result){
                      Toast.makeText(mAcivity.getApplicationContext(),"Encryption Done",Toast.LENGTH_LONG).show();
                  }
              }
              @Override
              public void onError(Exception e) {
                  Toast.makeText(mAcivity.getApplicationContext(),"Error"+e,Toast.LENGTH_LONG).show();
              }
          }).execute();
        }



        public void downloadFile(String input,String editedName) throws IOException {
            progressDialog = ProgressDialog.show(mAcivity,
                    "ProgressDialog",
                    "Wait for some seconds");
            InputStream in = null;
            File sr = new File(input + mFileList.get(getAdapterPosition()).getFileName());
            File ot = new File(input + editedName + mFileList.get(getAdapterPosition()).getFileName());
            OutputStream out = null;
            try {

                //create output directory if it doesn't exist
                File dir = new File (input);
                if (!dir.exists())
                {
                    dir.mkdirs();
                }
                String filname = mFileList.get(getAdapterPosition()).getFileName();
                // filname = "Encrytted" +filname;

                in = new FileInputStream(sr);
                out = new FileOutputStream(ot);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                mAcivity.populateRecyclerViewValues(input);
                progressDialog.dismiss();
                in.close();
                in = null;

                // write the output file
                out.flush();
                out.close();
                out = null;

            }

            catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            }
            catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
    }
}
