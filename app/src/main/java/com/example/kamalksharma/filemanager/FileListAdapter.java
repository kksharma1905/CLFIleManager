package com.example.kamalksharma.filemanager;

import android.app.ActionBar;
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
    private ArrayList<DataModel> mFileList;
    private  FileListViewActivity mAcivity;
    private Context mContext;
    private boolean mIsLocalStorage;
    private ProgressDialog progressDialog;

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
        if(getExtension(temporaryFileName).equals("folder")){
            fileListViewHolder.folderArrowIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/ic_chevron_right_black_24dp", null, mContext.getPackageName()));
        }
        else{
            fileListViewHolder.folderArrowIcon.setImageResource(0);
        }
    }


public int getImageId(String name){
        if(getExtension(name).equals("image")){
            String uri = "@drawable/image";
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

        if(l>4&&(fileName.substring(l-4,l).equals(".png")||fileName.substring(l-5,l).equals(".jpeg")||fileName.substring(l-4,l).equals(".jpg"))){
            return "image";
        }
        else if(l>3&&fileName.substring(l-4,l).equals(".pdf")){
            return "pdf";
        }
        else if(l>3&&fileName.substring(l-4,l).equals(".apk")){
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
            android.support.v7.app.ActionBar actionBar = mAcivity.getSupportActionBar();
            String filePath = mFileList.get(getAdapterPosition()).getFilePath();
            if(getExtension(mFileList.get(getAdapterPosition()).getFileName())){
                Toast.makeText(mContext.getApplicationContext(),"This is File, Method not implemeted",Toast.LENGTH_SHORT).show();
                return;
            }
            if(mIsLocalStorage){
                mAcivity.folderHistory.push(mFileList.get(getAdapterPosition()).getFilePath());
                actionBar.setTitle(mFileList.get(getAdapterPosition()).getFileName());
                mAcivity.populateRecyclerViewValues(mFileList.get(getAdapterPosition()).getFilePath());

            }
            else
            {
                mAcivity.folderHistory.push(mFileList.get(getAdapterPosition()).getFilePath());
                actionBar.setTitle(mFileList.get(getAdapterPosition()).getFileName());
                mAcivity.getDropboxList(mFileList.get(getAdapterPosition()).getFilePath());
            }
        }

        public boolean getExtension(String fileName)
        {
            int l = fileName.length();

            if(l>3&&(fileName.substring(l-3,l).equals("png")||fileName.substring(l-3,l).equals("jpg")||fileName.substring(l-3,l).equals("PNG")||fileName.substring(l-3,l).equals("pdf")||fileName.substring(l-3,l).equals("PDF")||fileName.substring(l-3,l).equals("apk"))){
                return true;
            }
            else if(l>4&&fileName.substring(l-4,l).equals("jpeg")){
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
                    if(input.equals("/")){
                        input = "";
                    }
                    if(item==0){
                        if(mIsLocalStorage){
                            Toast.makeText(mContext.getApplicationContext(),"Encrypting",Toast.LENGTH_LONG).show();
                            try {
                                EncryptLocalFile(input,"Encrypting_");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            EncryptDBFile("Encrypted_",input);
                        }
                    }
                    else{
                        if(mIsLocalStorage){
                            Toast.makeText(mContext.getApplicationContext(),"Decrypting",Toast.LENGTH_LONG).show();
                            try {
                                EncryptLocalFile(input,"Decypting_");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            EncryptDBFile("Decrypted_",input);
                        }

                    }

                }
            });
            builder.show();
            return true;
        }

        public void EncryptDBFile(final String fileOption, final String input){
          new DropboxFileTask(mContext,mAcivity,fileOption,mFileList.get(getAdapterPosition()), new DropboxFileTask.Callback() {
              @Override
              public void onDownload(Boolean result) {
                  if (result&&(fileOption.equals("Encrypted_"))){
                      mAcivity.getDropboxList(input);
                      Toast.makeText(mAcivity.getApplicationContext(),"Encryption Done",Toast.LENGTH_LONG).show();
                  }
                  else{
                      mAcivity.getDropboxList(input);
                      Toast.makeText(mAcivity.getApplicationContext(),"Decryption Done",Toast.LENGTH_LONG).show();
                  }
              }
              @Override
              public void onError(Exception e) {
                  Toast.makeText(mAcivity.getApplicationContext(),"Error"+e,Toast.LENGTH_LONG).show();
              }
          }).execute();
        }

        public void EncryptLocalFile(String input,String editedName) throws IOException {
            InputStream inputStream = null;
            File sourceFile = new File(input + mFileList.get(getAdapterPosition()).getFileName());
            File outputFile = new File(input + editedName + mFileList.get(getAdapterPosition()).getFileName());
            OutputStream outputStream = null;
            try {

                //create output directory if it doesn't exist
                File dir = new File (input);
                if (!dir.exists())
                {
                    dir.mkdirs();
                }
                String filname = mFileList.get(getAdapterPosition()).getFileName();
                // filname = "Encrytted" +filname;

                inputStream = new FileInputStream(sourceFile);
                outputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                mAcivity.populateRecyclerViewValues(input);
                Toast.makeText(mContext.getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
                inputStream.close();
                inputStream = null;
                // write the output file
                outputStream.flush();
                outputStream.close();
                outputStream = null;
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
