package com.example.kamalksharma.filemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter <FileListAdapter.FileListViewHolder> {
    private static final String ACCESS_TOKEN = "Qzmg3GEhnsAAAAAAAAAEs05bMlnYeXIclE1nFUyF1-nfnFVhCXPpvaTCdF0EU94n";
    private File[] mlistMembers;
    private List<Metadata> mdropboxmetadata;
    private ArrayList<DataModel> listModel;
    FileListViewActivity mAcivity;
    Context mContext;
     boolean isLocalStorage;

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
        String temporaryFileName = listModel.get(i).getFileName();
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

        if(l>4&&(fileName.substring(l-3,l).equals("png")||fileName.substring(l-4,l).equals("jpeg"))){
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
        return listModel.size();
    }


    public void setListContent(ArrayList<DataModel> listModel,boolean isLocalStorage,List<Metadata> mdropboxmetadata) {
        this.listModel = listModel;
        this.isLocalStorage = isLocalStorage;
        this.mdropboxmetadata = mdropboxmetadata;
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
            mAcivity.folderHistory.push(listModel.get(getAdapterPosition()).getFilePath());
            String filePath = listModel.get(getAdapterPosition()).getFilePath();
            if(getExtension(listModel.get(getAdapterPosition()).getFileName())){
                Toast.makeText(mContext.getApplicationContext(),"This is File, Method not implemeted",Toast.LENGTH_LONG).show();
//                mAcivity.playFile(filePath,"jpeg" );
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
                    try {
                        downloadFile(input,basePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            builder.show();
            return true;
        }
        public void downloadFile(String input,String oldPath) throws IOException {
//            String fileName = "Execption" + ".csv";
//            String headings = "Hello, world!";
//            File path = Environment.getExternalStorageDirectory();
//            File file = new File(path, fileName);
//            path.mkdirs();
//            OutputStream os = new FileOutputStream(file);
//            os.write(headings.getBytes());

//            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/KakesApp").build();
//            DbxClientV2 client = new DbxClientV2(config,ACCESS_TOKEN);
//
//
//            new DownloadFileTask((FileMetadata) mdropboxmetadata.get(getAdapterPosition()), client, new DownloadFileTask.Callback() {
//                @Override
//                public void onDownloadComplete(File result) {
//                    Toast.makeText(mContext.getApplicationContext(),"Downloaded",Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    Toast.makeText(mContext.getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
//
//                }
//            }).execute();

            InputStream in = null;
            OutputStream out = null;
            try {

                //create output directory if it doesn't exist

                in = new FileInputStream(oldPath);
                out = new FileOutputStream(input + "Download/" + listModel.get(getAdapterPosition()).getFileName());

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file (You have now copied the file)
                out.flush();
                out.close();
                out = null;

            }  catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            }
            catch (Exception e) {
                Log.e("tag", e.getMessage());
            }


        }

    }
}
