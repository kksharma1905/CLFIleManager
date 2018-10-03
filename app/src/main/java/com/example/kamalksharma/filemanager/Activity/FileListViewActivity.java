package com.example.kamalksharma.filemanager.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.paper.Folder;
import com.example.kamalksharma.filemanager.Model.DataModel;
import com.example.kamalksharma.filemanager.Adapter.FileListAdapter;
import com.example.kamalksharma.filemanager.R;
import com.example.kamalksharma.filemanager.Utils.DropboxFileTask;
import com.example.kamalksharma.filemanager.Utils.UserAccountTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    public Stack<String> folderHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list_view);
        pgsBar = (ProgressBar) findViewById(R.id.pBar);
        pgsBar.setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        isLocalStoage = false;

        if (extras != null) {
            isLocalStoage = extras.getBoolean("isLocalStorage");
        }
        recyclerView = (RecyclerView) findViewById(R.id.fileList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new FileListAdapter(this, this, isLocalStoage);

        if (isLocalStoage) {
            getLocalStoageList("root");
            getHistory();
        } else {
            getDropboxList("");
            getDropBoxHistory();
        }

    }

    public void getDropboxList(String mpath) {
        pgsBar.setVisibility(View.VISIBLE);
        new UserAccountTask(mpath, new UserAccountTask.TaskDelegate() {
            @Override
            public void onAccountReceived(ListFolderResult account) {
                //Print account's info
                List<Metadata> list = account.getEntries();
                ArrayList<DataModel> listModel = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    DataModel object = new DataModel();
                    object.setFileName(list.get(i).getName());
                    object.setFilePath(list.get(i).getPathDisplay());
                    object.setIsFolder(list.get(i) instanceof FolderMetadata);
                    listModel.add(object);
                }
                adapter.setListContent(listModel, isLocalStoage);
                recyclerView.setAdapter(adapter);
                pgsBar.setVisibility(View.GONE);
                Log.d("User", String.valueOf(list.get(0)));
            }

            @Override
            public void onError(Exception error) {
                Log.d("User", "Error receiving account details.");
            }
        }).execute();

    }

    public void getLocalStoageList(String fileName) {
        pgsBar.setVisibility(View.GONE);
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            final File rootFilePath;
            if (fileName.compareTo("root") == 0) {
                rootFilePath = Environment.getExternalStorageDirectory();
            } else {
                rootFilePath = new File(fileName);
            }
            File[] fileList = rootFilePath.listFiles();
            pgsBar.setVisibility(View.GONE);
            ArrayList<DataModel> listModel = new ArrayList<>();
            for (int i = 0; i < fileList.length; i++) {
                DataModel object = new DataModel();
                if (fileList[i].getName().charAt(0) == '.') {

                } else {
                    object.setFileName(fileList[i].getName());
                    object.setFilePath(fileList[i].toString());
                    object.setIsFolder(fileList[i].isDirectory());
                    listModel.add(object);
                }
            }
            adapter.setListContent(listModel, isLocalStoage);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "You dont have permission to access", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        if (goBack()) {
            this.finish();
        }

    }

    public boolean goBack() {
        if (this.folderHistory.isEmpty())
            return true;
        else if (folderHistory.size() == 1)
            return true;
        folderHistory.pop();

        if (!this.folderHistory.isEmpty()) {
            String[] baseName = this.folderHistory.peek().split("/");
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if (isLocalStoage) {
                actionBar.setTitle(baseName[baseName.length - 1]);
                getLocalStoageList(this.folderHistory.peek());
            } else {
                actionBar.setTitle(baseName[baseName.length - 1]);
                if (actionBar.getTitle().length() < 1) actionBar.setTitle("Root");
                getDropboxList(this.folderHistory.peek());
            }
        }

        return false;
    }

    public void getHistory() {
        File rootFilePath = Environment.getExternalStorageDirectory();
        this.folderHistory = new Stack<>();
        this.folderHistory.add(rootFilePath.toString());

    }

    public void getDropBoxHistory() {
        String rootFilePath = "";
        this.folderHistory = new Stack<>();
        this.folderHistory.add(rootFilePath);

    }

    public void playFile(String clickedFile, String type) {
        File file = new File(clickedFile);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "image/*");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }
   //Encyption method for Dropbox files and Local Files
    public void fileEncryption(DataModel fileData) {
        String filePath = fileData.getFilePath();
        String[] filePathArray = filePath.split("/");
        String fileParentPath = "/";
        for (int i = 1; i < filePathArray.length - 1; i++) {
            fileParentPath = fileParentPath + filePathArray[i] + "/";
        }
        if (fileParentPath.equals("/")) {
            fileParentPath = "";
        }
        if (isLocalStoage) {
            InputStream inputStream = null;
            File sourceFile = new File(fileParentPath + fileData.getFileName());
            File outputFile = new File(fileParentPath + "Encrypted_" + fileData.getFileName());
            OutputStream outputStream = null;
            try {

                //create output directory if it doesn't exist
                File dir = new File(fileParentPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String filname = fileData.getFileName();
                inputStream = new FileInputStream(sourceFile);
                outputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                getLocalStoageList(fileParentPath);
                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                inputStream.close();
                inputStream = null;
                // write the output file
                outputStream.flush();
                outputStream.close();
                outputStream = null;
            } catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }else {
            final String finalFileParentPath = fileParentPath;
            new DropboxFileTask(this, this, "Encrypted_", fileData, new DropboxFileTask.Callback() {
                @Override
                public void onDownload(Boolean result) {
                    if (result && ("Encrypted_".equals("Encrypted_"))) {
                        getDropboxList(finalFileParentPath);
                        Toast.makeText(getApplicationContext(), "Encryption Done", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getApplicationContext(), "Error" + e, Toast.LENGTH_LONG).show();
                }
            }).execute();
        }

    }

   //Decryption method for Dropbox files and Local files
    public void fileDecryption(DataModel fileData) {
        String filePath = fileData.getFilePath();
        String[] filePathArray = filePath.split("/");
        String fileParentPath = "/";
        for (int i = 1; i < filePathArray.length - 1; i++) {
            fileParentPath = fileParentPath + filePathArray[i] + "/";
        }
        if (fileParentPath.equals("/")) {
            fileParentPath = "";
        }
        if (isLocalStoage) {
            InputStream inputStream = null;
            File sourceFile = new File(fileParentPath + fileData.getFileName());
            File outputFile = new File(fileParentPath + "Decrypted_" + fileData.getFileName());
            OutputStream outputStream = null;
            try {

                //create output directory if it doesn't exist
                File dir = new File(fileParentPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String filname = fileData.getFileName();
                inputStream = new FileInputStream(sourceFile);
                outputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                getLocalStoageList(fileParentPath);
                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                inputStream.close();
                inputStream = null;
                // write the output file
                outputStream.flush();
                outputStream.close();
                outputStream = null;
            } catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }else {
            final String finalFileParentPath = fileParentPath;
            new DropboxFileTask(this, this, "Decrypted_", fileData, new DropboxFileTask.Callback() {
                @Override
                public void onDownload(Boolean result) {
                    if (result) {
                        getDropboxList(finalFileParentPath);
                        Toast.makeText(getApplicationContext(), "Decryption Done", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getApplicationContext(), "Error" + e, Toast.LENGTH_LONG).show();
                }
            }).execute();
        }

    }
}
