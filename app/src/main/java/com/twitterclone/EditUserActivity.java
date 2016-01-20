package com.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
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

public class EditUserActivity extends AppCompatActivity {
    EditUserUserData editUserUserData;
    Button buttonSave;
    String authToken=null;
    int uid=0;
    String serverResponse=null;
    String name,email,phone;
    EditText editTextName,editTextEmail,editTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Bundle b=getIntent().getExtras();
        authToken=b.getString("token");
        uid=b.getInt("uid");
        name=b.getString("name");
        email=b.getString("email");
        phone=b.getString("phone");
        buttonSave=(Button)findViewById(R.id.bt_edit_user_save);
        editTextName=(EditText)findViewById(R.id.et_edit_user_profile_name);
        editTextEmail=(EditText)findViewById(R.id.et_edit_user_email);
        editTextPhone=(EditText)findViewById(R.id.et_edit_user_mobile_number);
        editTextName.setText(name);
        editTextEmail.setText(email);
        editTextPhone.setText(phone);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEditUserFormData()){
                    new EditUserAsyncTask().execute();
                }
            }
        });
    }

    class EditUserAsyncTask extends AsyncTask<Void,Void,Void>{
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(EditUserActivity.this);
            dialog.setMessage("Saving your data. Please wait ...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            serverResponse=PATCH(ApiUrls.profileURL + uid,editUserUserData,authToken);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(serverResponse==null)
                Toast.makeText(EditUserActivity.this,"Error in saving data. Please try again later ...",Toast.LENGTH_LONG).show();
            dialog.hide();

            Intent dashBoardIntent=new Intent(EditUserActivity.this,DashBoardActivity.class);
            dashBoardIntent.putExtra("token", authToken);
            dashBoardIntent.putExtra("uid", uid);
            startActivity(dashBoardIntent);

            EditUserActivity.this.finish();
        }
    }

    public boolean checkEditUserFormData(){
        String name,email,phone;
        name=editTextName.getText().toString();
        email=editTextEmail.getText().toString();
        phone=editTextPhone.getText().toString();
        if(name==""){
            Toast.makeText(getApplicationContext(),"Name Can't be left Blank!!!",Toast.LENGTH_SHORT).show();
            return false;
        } if(email==""){
            Toast.makeText(getApplicationContext(),"Email can't be left blank!!!",Toast.LENGTH_SHORT).show();
            return false;
        } if(phone==""){
            Toast.makeText(getApplicationContext(),"Mobile number can't be left blank!!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        editUserUserData=new EditUserUserData();
        editUserUserData.setName(name);
        editUserUserData.setEmail(email);
        editUserUserData.setPhone(phone);
        return true;
    }

    public class EditUserUserData{
        private String name,phone,email;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static String PATCH(String url,EditUserUserData editUserUserData,String authToken){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name",editUserUserData.getName());
            jsonObject.accumulate("email",editUserUserData.getEmail());
            jsonObject.accumulate("mobile_number",editUserUserData.getPhone());

            json = jsonObject.toString();


            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("X-HTTP-Method-Override", "PATCH");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization","Token token=" + authToken);

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
