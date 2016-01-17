package com.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences=null;
    SharedPreferences.Editor editor;
    int uid=0;
    Intent dashBoardIntent;
    Button buttonSignup;
    TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        buttonSignup=(Button)findViewById(R.id.bt_signup);
        textViewLogin=(TextView)findViewById(R.id.tv_login);

        preferences=getSharedPreferences("UserSession", MODE_PRIVATE);
        uid=preferences.getInt("uid", 0);

        if(uid!=0){
            dashBoardIntent=new Intent(MainActivity.this,DashBoardActivity.class);
            dashBoardIntent.putExtra("uid",uid);
            startActivity(dashBoardIntent);
            this.finish();
        }
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this,SignupActivity.class));
            }
        });
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
