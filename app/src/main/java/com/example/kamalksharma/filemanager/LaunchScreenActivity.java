package com.example.kamalksharma.filemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.auth.DbxAppAuthRequests;
import com.example.kamalksharma.filemanager.R;

public class LaunchScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        Button mainActivityBtn = (Button)findViewById(R.id.azureBtn);
//        Button azureLogoutBtn = (Button)findViewById(R.id.onPremBtn);
        mainActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });
//        azureLogoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/KakesApp").build();
//                DbxClientV2 clientV2 = new DbxClientV2(config, "Qzmg3GEhnsAAAAAAAAAEs05bMlnYeXIclE1nFUyF1");
//                try {
//                    clientV2.auth().tokenRevoke();
//                } catch (DbxException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

}
