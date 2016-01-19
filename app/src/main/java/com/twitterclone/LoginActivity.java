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
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {
    Button buttonLogin;
    String serverResponse;
    LoginUserData loginUserData;
    String authToken=null;
    int uid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin=(Button)findViewById(R.id.bt_login_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLoginFormData()){
                    new LoginAsyncTask(ApiUrls.loginURL).execute();
                }
            }
        });
    }

    class LoginAsyncTask extends AsyncTask<Void,Void,Void>{
        String url;
        ProgressDialog dialog;
        public LoginAsyncTask(String url) {
            this.url=url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Please wait while Logging");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            serverResponse=POST(url,loginUserData);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            Log.d("Login ...",serverResponse);
            try{
                JSONObject object=new JSONObject(serverResponse.toString());
                authToken=object.getString("auth_token");
                uid=object.getInt("id");
            }catch (Exception e){
                e.printStackTrace();
            }
            SharedPreferences preferences=getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.remove("token");
            editor.remove("uid");
            editor.putString("token",authToken);
            editor.putInt("uid",uid);
            editor.commit();
            Intent dashBoardIntent=new Intent(LoginActivity.this,DashBoardActivity.class);
            dashBoardIntent.putExtra("token", authToken);
            dashBoardIntent.putExtra("uid",uid);
            startActivity(dashBoardIntent);

            LoginActivity.this.finish();
        }
    }

    public static String POST(String url, LoginUserData userData){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("login_with",userData.getLoginWith());
            jsonObject.accumulate("password",userData.getPassword());

            json = jsonObject.toString();


            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = new ConvertInputStreamToString(inputStream).getString();
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public boolean checkLoginFormData(){
        Button buttonLogin;
        String loginWith,password;
        EditText editTextLoginWith,editTextPassword;
        editTextLoginWith=(EditText)findViewById(R.id.et_login_login_with);
        editTextPassword=(EditText)findViewById(R.id.et_login_password);
        loginWith=editTextLoginWith.getText().toString();
        password=editTextPassword.getText().toString();
        if(loginWith==""){
            Toast.makeText(getApplicationContext(),"Email/Phone can't be left blank!",Toast.LENGTH_SHORT).show();
            return false;
        }if(password==""){
            Toast.makeText(getApplicationContext(),"Email/Phone can't be left blank!",Toast.LENGTH_SHORT).show();
            return false;
        }if(password.length() < 8){
            Toast.makeText(getApplicationContext(),"Password must be atleast eight Characters",Toast.LENGTH_SHORT).show();
            return false;
        }
        loginUserData=new LoginUserData();
        loginUserData.setLoginWith(loginWith);
        loginUserData.setPassword(password);
        return true;
    }

    public class LoginUserData{
        private String loginWith,password;

        public String getLoginWith() {
            return loginWith;
        }

        public void setLoginWith(String loginWith) {
            this.loginWith = loginWith;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
