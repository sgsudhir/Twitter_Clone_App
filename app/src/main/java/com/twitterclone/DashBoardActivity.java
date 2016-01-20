package com.twitterclone;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class DashBoardActivity extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;
    TextView textViewName,textViewPhone,textViewEmail;
    String name,email,phone;
    String authToken=null;
    int uid=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        actionBar=getSupportActionBar();
        actionBar.show();
        Bundle b=getIntent().getExtras();
        authToken=b.getString("token");
        uid=b.getInt("uid");
        Toast.makeText(getApplicationContext(),"Token: " + authToken + "| UID: " + uid, Toast.LENGTH_LONG).show();

        textViewName=(TextView)findViewById(R.id.tv_profile_name);
        textViewEmail=(TextView)findViewById(R.id.tv_profile_email);
        textViewPhone=(TextView)findViewById(R.id.tv_profile_phone);
        if(authToken==null || uid==0){
            Toast.makeText(getApplicationContext(),"Something went wrong.\nPlease try later !!!",Toast.LENGTH_LONG).show();
        }else{
            new GetUserProfile().execute();
        }
    }

    class GetUserProfile extends AsyncTask<Void,Void,Void>{
        String jsonString;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog=new ProgressDialog(DashBoardActivity.this);
            dialog.setMessage("Please wait while loading your profile");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                JsonGetHandler jsonGetHandler=new JsonGetHandler();
                Log.d("URL : ", ApiUrls.profileURL + uid);
                jsonString=jsonGetHandler.requestJsonByUrl((ApiUrls.profileURL+uid).toString(),JsonGetHandler.GET,authToken);
                Log.d("JSON: ",jsonString);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            JSONObject object;
            super.onPostExecute(aVoid);
            dialog.dismiss();
            try{
                object=new JSONObject(jsonString.toString());
                textViewName.setText(object.getString("name"));
                textViewEmail.setText(object.getString("email"));
                textViewPhone.setText(object.getString("mobile_number"));
                name=object.getString("name");
                email=object.getString("email");
                phone=object.getString("mobile_number");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit_user) {
            Intent editUserIntent=new Intent(DashBoardActivity.this,EditUserActivity.class);
            editUserIntent.putExtra("token", authToken);
            editUserIntent.putExtra("uid",uid);
            editUserIntent.putExtra("name",name);
            editUserIntent.putExtra("email",email);
            editUserIntent.putExtra("phone",phone);
            startActivity(editUserIntent);
            DashBoardActivity.this.finish();
            return true;
        }if (id==R.id.action_home){

        }if (id==R.id.action_users){

        }if (id==R.id.action_logout){
            SharedPreferences preferences=getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.remove("token");
            editor.remove("uid");
            editor.commit();
            startActivity(new Intent(DashBoardActivity.this, MainActivity.class));
            DashBoardActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
