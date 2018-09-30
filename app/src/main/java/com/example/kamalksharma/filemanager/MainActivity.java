package com.example.kamalksharma.filemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.DropBoxManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {
   private  Button msignInBtn;
   private Button mlocalStorageBtn;
   private Button mtempBtn;
    private static final String ACCESS_TOKEN = "Qzmg3GEhnsAAAAAAAAAEs05bMlnYeXIclE1nFUyF1-nfnFVhCXPpvaTCdF0EU94n";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msignInBtn = (Button) findViewById(R.id.dropboxLoginBtn);
        mlocalStorageBtn = (Button) findViewById(R.id.deviceMemoryBtn);
//        mtempBtn = (Button) findViewById(R.id.tempBtn);


       msignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.APP_KEY));
                Toast.makeText(getApplicationContext(),"Why is this happening",Toast.LENGTH_SHORT).show();
            }
        });
        mlocalStorageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(MainActivity.this, FileListViewActivity.class);
                startIntent.putExtra("isLocalStorage",true);
                startActivity(startIntent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        getAccessToken();
    }

    public void getAccessToken() {
        String accessToken = Auth.getOAuth2Token(); //generate Access Token
        if (accessToken != null) {
            //Store accessToken in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("com.example.valdio.dropboxintegration", Context.MODE_PRIVATE);
            prefs.edit().putString("access-token", ACCESS_TOKEN).apply();
            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
//            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/KakesApp").build();
//            DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
            //Proceed to MainActivity
            Intent intent = new Intent(MainActivity.this, FileListViewActivity.class);
            intent.putExtra("isLocalStorage",false);
            startActivity(intent);
        }
    }

}
