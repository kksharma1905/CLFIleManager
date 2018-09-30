package com.example.kamalksharma.filemanager;

import android.app.AlertDialog;
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

import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FileListAdapter extends RecyclerView.Adapter <FileListAdapter.FileListViewHolder> {

    private File[] mlistMembers;
    private List<Metadata> mlist;
    private ArrayList<DataModel> listModel;
    Stack<String> mfolderHistory;
    FolderHistory mfh;
    FileListViewActivity mAcivity;
    Context mContext;
     boolean isLocalStorage;

    public FileListAdapter(Context context, FileListViewActivity activity,boolean isLocalStorage){
        this.mContext = context;
        mAcivity = activity;
        mfh = new FolderHistory();
        if(isLocalStorage){
            mfolderHistory = mfh.getHistory();
        }
        else
        {
            mfolderHistory = mfh.getDropBoxHistory();
        }
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
      String temporaryFileName = listModel.get(i).getFileName();
        fileListViewHolder.fileName.setText(temporaryFileName);
        fileListViewHolder.fileIcon.setImageResource(getImageId(temporaryFileName));
    }
public int getImageId(String name){
        if(getExtension(name)){
             return mContext.getResources().getIdentifier("drawable/" + "file.png", null, mContext.getPackageName());
        }
        else {
            return mContext.getResources().getIdentifier("drawable/" + "folder.png", null, mContext.getPackageName());

        }

}
    public boolean getExtension(String fileName)
    {
        int l = fileName.length();

        if(fileName.substring(l-3,l).equals("png")){
            return true;
        }
        else return false;

    }


    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public void setListContent(ArrayList<DataModel> listModel,boolean isLocalStorage) {
        this.listModel = listModel;
        this.isLocalStorage = isLocalStorage;
    }

    public boolean goBack() {
        if (mfolderHistory.isEmpty())
            return true;
        mfolderHistory.pop();

        if (!mfolderHistory.isEmpty())
            if(isLocalStorage){
                mAcivity.populateRecyclerViewValues(mfolderHistory.peek());
            }
            else {
            mAcivity.getDropboxList(mfolderHistory.peek());
            }
//
        return false;
    }


    public class FileListViewHolder extends RecyclerView.ViewHolder  implements RecyclerView.OnClickListener,View.OnLongClickListener{
        ImageView fileIcon;
        TextView fileName;


        public FileListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.fileName);
        }

        @Override
        public void onClick(View v) {
            mfolderHistory.push(listModel.get(getAdapterPosition()).getFilePath());
            if(getExtension(listModel.get(getAdapterPosition()).getFileName())){
                Toast.makeText(mContext.getApplicationContext(),"This is File, Method not implemeted",Toast.LENGTH_LONG).show();
                return;
            }
            if(isLocalStorage){
                mAcivity.populateRecyclerViewValues(listModel.get(getAdapterPosition()).getFilePath());
            }
            else
            {
                mAcivity.getDropboxList(listModel.get(getAdapterPosition()).getFilePath());
            }
        }

        public boolean getExtension(String fileName)
        {
            int l = fileName.length();

            if(fileName.substring(l-3,l).equals("png")||fileName.substring(l-3,l).equals("jpg")||fileName.substring(l-3,l).equals("PNG")){
                return true;
            }
            else if(fileName.substring(l-4,l).equals("jpeg")){
                return true;
            }
            else return false;

        }


        @Override
        public boolean onLongClick(View v) {

            if(!getExtension(listModel.get(getAdapterPosition()).getFileName())){
                Toast.makeText(mContext.getApplicationContext(),"This is Folder, Method not implemeted",Toast.LENGTH_LONG).show();
                return true;
            }
            final CharSequence[] items = {"Encryption", "Decryption"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setTitle("Select The Options");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    Toast.makeText(mContext.getApplicationContext(),"sadasdasda",Toast.LENGTH_LONG).show();
                    String basePath = listModel.get(getAdapterPosition()).getFilePath();
                    String[] array = basePath.split("/");
                    String input="/";
                    for(int i=1;i<array.length-1;i++){
                        input = input + array[i]+"/";
                    }
                    InputStream in = null;
                    File sr = new File(input + listModel.get(getAdapterPosition()).getFileName());
                    File ot = new File(input + "Encypted" + listModel.get(getAdapterPosition()).getFileName());
                    OutputStream out = null;
                    try {

                        //create output directory if it doesn't exist
                        File dir = new File (input);
                        if (!dir.exists())
                        {
                            dir.mkdirs();
                        }
                     String filname = listModel.get(getAdapterPosition()).getFileName();
                       // filname = "Encrytted" +filname;

                        in = new FileInputStream(sr);
                        out = new FileOutputStream(input);

                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        in = null;

                        // write the output file
                        out.flush();
                        out.close();
                        out = null;

                        // delete the original file
                       // new File(sr.delete());


                    }

                    catch (FileNotFoundException fnfe1) {
                        Log.e("tag", fnfe1.getMessage());
                    }
                    catch (Exception e) {
                        Log.e("tag", e.getMessage());
                    }

                }
            });
            builder.show();
            return true;
        }
    }
}
