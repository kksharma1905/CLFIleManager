package com.example.kamalksharma.filemanager;

import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FileListViewActivity extends AppCompatActivity {

    private File currentDir;
    String state = Environment.getExternalStorageState();
    RecyclerView recyclerView;
    FileListAdapter adapter;
    ProgressBar pgsBar;
    boolean isLocalStoage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list_view);
         pgsBar = (ProgressBar)findViewById(R.id.pBar);
        pgsBar.setVisibility(View.VISIBLE);


        Bundle extras = getIntent().getExtras();
         isLocalStoage = false;

        if (extras != null) {
            isLocalStoage = extras.getBoolean("isLocalStorage");
            //The key argument here must match that used in the other activity
        }
        recyclerView = (RecyclerView)findViewById(R.id.fileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileListAdapter(this,this,isLocalStoage);
        if(isLocalStoage){
            listLocalStorage();
        }
        else{
            getDropboxList("");
        }

    }
    public void getDropboxList(String mpath){
        new UserAccountTask(mpath,new UserAccountTask.TaskDelegate() {
            @Override
            public void onAccountReceived(ListFolderResult account) {
                //Print account's info
                List<Metadata> list = account.getEntries();
                ArrayList<DataModel> listModel = new ArrayList<>();
                for(int i=0;i<list.size();i++){
                    DataModel object = new DataModel();
                    object.setFileName(list.get(i).getName());
                    object.setFilePath(list.get(i).getPathDisplay());
                    listModel.add(object);
                }
                adapter.setListContent(listModel,isLocalStoage);
                recyclerView.setAdapter(adapter);
//                Metadata example = list.get(0);
                pgsBar.setVisibility(View.GONE);
                Log.d("User", String.valueOf(list.get(0)));
            }

            @Override
            public void onError(Exception error) {
                Log.d("User", "Error receiving account details.");
            }
        }).execute();

    }
    public void listLocalStorage(){
        populateRecyclerViewValues("root");
    }

    public void populateRecyclerViewValues(String fileName) {
        pgsBar.setVisibility(View.GONE);
        if (Environment.MEDIA_MOUNTED.equals(state)){
            final File rootFilePath;
            if(fileName.compareTo("root")==0){
                rootFilePath = Environment.getExternalStorageDirectory();
            }
            else
            {
                rootFilePath = new File(fileName);
            }
            File[] fileList = rootFilePath.listFiles();
            pgsBar.setVisibility(View.GONE);
            ArrayList<DataModel> listModel = new ArrayList<>();
            for (int i=0;i<fileList.length;i++){
                DataModel object = new DataModel();
                object.setFileName(fileList[i].getName());
                object.setFilePath(fileList[i].toString());
                listModel.add(object);
            }
            adapter.setListContent(listModel,isLocalStoage);
            recyclerView.setAdapter(adapter);
        }
        else{
            Toast.makeText(getApplicationContext(),"You dont have permission to access",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        if(adapter.goBack()){
            moveTaskToBack(true);
        }
    }






}
