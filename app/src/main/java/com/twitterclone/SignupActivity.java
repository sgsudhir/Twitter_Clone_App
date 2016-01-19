package com.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class SignupActivity extends AppCompatActivity {
    Button buttonSignup;
    SignupUserData signupUserData;
    String serverResponse;
    String authToken=null;
    int uid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        buttonSignup=(Button)findViewById(R.id.bt_signup_signup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSignupFormData()){
                    new SignupAsyncTask(ApiUrls.signupURL).execute();
                }
            }
        });
    }

    public boolean checkSignupFormData(){
        EditText editTextName,editTextEmail,editTextPhone,editTextPassword,editTextConformPassword;
        String name,email,phone,password,conformPassword;
        editTextName=(EditText)findViewById(R.id.et_signup_name);
        editTextEmail=(EditText)findViewById(R.id.et_signup_email);
        editTextPhone=(EditText)findViewById(R.id.et_signup_phone);
        editTextPassword=(EditText)findViewById(R.id.et_signup_password);
        editTextConformPassword=(EditText)findViewById(R.id.et_signup_comform_password);
        name=editTextName.getText().toString();
        email=editTextEmail.getText().toString();
        phone=editTextPhone.getText().toString();
        password=editTextPassword.getText().toString();
        conformPassword=editTextConformPassword.getText().toString();
        if(name.equals("")){
            Toast.makeText(getApplicationContext(),"Name can't be blank!",Toast.LENGTH_SHORT).show();
            return false;
        }if(phone.equals("")){
            Toast.makeText(getApplicationContext(), "Phone Number can't be blank!", Toast.LENGTH_SHORT).show();
            return false;
        }if(email.equals("")){
            Toast.makeText(getApplicationContext(), "Email can't be blank!", Toast.LENGTH_SHORT).show();
            return false;
        }if(password.equals("")){
            Toast.makeText(getApplicationContext(),"Password can't be blank!",Toast.LENGTH_SHORT).show();
            return false;
        }if(password.length() < 8){
            Toast.makeText(getApplicationContext(), "Password should be at least eight characters", Toast.LENGTH_SHORT).show();
            return false;
        }if(!conformPassword.equals(password)){
            Toast.makeText(getApplicationContext(),"Conform password doesn't match!",Toast.LENGTH_SHORT).show();
            return false;
        }
        signupUserData=new SignupUserData();
        signupUserData.setName(name);
        signupUserData.setEmail(email);
        signupUserData.setPhone(phone);
        signupUserData.setPassword(password);
        return true;
    }

    public class SignupUserData{
        private String name,email,phone,password;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private class SignupAsyncTask extends AsyncTask<Void,Void,Void> {
        String url;
        ProgressDialog dialog;
        public SignupAsyncTask(String url){
            this.url=url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(SignupActivity.this);
            dialog.setMessage("Please wait while creating your profile");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            serverResponse=POST(url,signupUserData);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            Log.d("Server Response ::: ", serverResponse + " ... Success !!!");
            try{
                JSONObject object=new JSONObject(serverResponse.toString());
                authToken=object.getString("auth_token");
                uid= object.getInt("id");
            }catch (Exception e){
                e.printStackTrace();
            }
            SharedPreferences preferences=getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.remove("token");
            editor.remove("uid");
            editor.putString("token", authToken);
            editor.putInt("uid",uid);
            editor.commit();
            Intent dashBoardIntent=new Intent(SignupActivity.this,DashBoardActivity.class);
            dashBoardIntent.putExtra("token",authToken);
            dashBoardIntent.putExtra("uid",uid);
            startActivity(dashBoardIntent);

            SignupActivity.this.finish();
        }
    }

    public static String POST(String url, SignupUserData userData){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name",userData.getName());
            jsonObject.accumulate("email",userData.getEmail());
            jsonObject.accumulate("mobile_number",userData.getPhone());
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
}
