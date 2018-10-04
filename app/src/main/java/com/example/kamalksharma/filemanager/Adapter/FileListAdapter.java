package com.example.kamalksharma.filemanager.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamalksharma.filemanager.Activity.FileListViewActivity;
import com.example.kamalksharma.filemanager.Model.DataModel;
import com.example.kamalksharma.filemanager.Utils.DropboxFileTask;
import com.example.kamalksharma.filemanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileListViewHolder>  {
    private ArrayList<DataModel> mFileList;
    private FileListViewActivity mAcivity;
    private Context mContext;
    private boolean mIsLocalStorage;
    private ProgressDialog progressDialog;


    public interface RecyclerViewClickListener{
        void FileEncryption(View view, DataModel fileData);
        void FileDecryption(View view, DataModel fileData);
    }


    public FileListAdapter(Context context, FileListViewActivity activity, boolean isLocalStorage) {
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
        fileListViewHolder.fileIcon.setImageResource(getImageId(mFileList.get(i)));
        if (mFileList.get(i).isFolder()) {
            fileListViewHolder.folderArrowIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/ic_chevron_right_black_24dp", null, mContext.getPackageName()));
        } else {
            fileListViewHolder.folderArrowIcon.setImageResource(0);
        }
    }


    public int getImageId(DataModel itemData) {
        if (itemData.isFolder()) {
            String uri = "@drawable/folder";
            return mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
        } else {
            if (getExtension(itemData.getFileName()).equals("image")) {
                String uri = "@drawable/image";
                return mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
            } else if (getExtension(itemData.getFileName()).equals("pdf")) {
                String uri = "@drawable/pdficon";
                return mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());

            } else {
                String uri = "@drawable/apkicon";
                return mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
            }
        }
    }

    public String getExtension(String fileName) {

        int l = fileName.length();
        if (l > 4 && (fileName.substring(l - 4, l).equals(".png") || fileName.substring(l - 5, l).equals(".jpeg") || fileName.substring(l - 4, l).equals(".jpg"))) {
            return "image";
        } else if (l > 3 && fileName.substring(l - 4, l).equals(".pdf")) {
            return "pdf";
        } else {
            return "apk";
        }
    }


    @Override
    public int getItemCount() {
        return mFileList.size();
    }


    public void setListContent(ArrayList<DataModel> listModel, boolean isLocalStorage) {
        this.mFileList = listModel;
        this.mIsLocalStorage = isLocalStorage;
    }

    public class FileListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
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
            if (mFileList.get(getAdapterPosition()).isFolder()) {
                if (mIsLocalStorage) {
                    mAcivity.folderHistory.push(mFileList.get(getAdapterPosition()).getFilePath());
                    actionBar.setTitle(mFileList.get(getAdapterPosition()).getFileName());
                    mAcivity.getLocalStoageList(mFileList.get(getAdapterPosition()).getFilePath());
                } else {
                    mAcivity.folderHistory.push(mFileList.get(getAdapterPosition()).getFilePath());
                    actionBar.setTitle(mFileList.get(getAdapterPosition()).getFileName());
                    mAcivity.getDropboxList(mFileList.get(getAdapterPosition()).getFilePath());
                }
            } else {
                if (FileListAdapter.this.getExtension(mFileList.get(getAdapterPosition()).getFileName()).equals("image")) {
                    mAcivity.playFile(mFileList.get(getAdapterPosition()).getFilePath(), mFileList.get(getAdapterPosition()).getFileName());
                    return;
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "This is File, Method not implemeted", Toast.LENGTH_SHORT).show();

                    return;
                }
            }

        }

        public boolean getExtension(String fileName) {
            int l = fileName.length();
            if (l > 3 && (fileName.substring(l - 3, l).equals("png") || fileName.substring(l - 3, l).equals("jpg") || fileName.substring(l - 3, l).equals("PNG") || fileName.substring(l - 3, l).equals("pdf") || fileName.substring(l - 3, l).equals("PDF") || fileName.substring(l - 3, l).equals("apk"))) {
                return true;
            } else if (l > 4 && fileName.substring(l - 4, l).equals("jpeg")) {
                return true;
            } else return false;

        }


        @Override
        public boolean onLongClick(View v) {
            if (mFileList.get(getAdapterPosition()).isFolder()) {
                Toast.makeText(mContext.getApplicationContext(), "This is Folder, Method not implemeted", Toast.LENGTH_LONG).show();
                return true;
            }
            final CharSequence[] items = {"Encryption", "Decryption"};
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Select The Options");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        mAcivity.fileEncryption(mFileList.get(getAdapterPosition()));
                    } else {
                        mAcivity.fileDecryption(mFileList.get(getAdapterPosition()));
                    }
                }
            });
            builder.show();
            return true;
        }

    }
}
