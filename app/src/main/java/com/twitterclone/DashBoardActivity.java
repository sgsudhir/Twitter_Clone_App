package com.twitterclone;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class DashBoardActivity extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;
    TextView textViewName,textViewPhone,textViewEmail;
    String authToken=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        actionBar=getSupportActionBar();
        actionBar.show();
        Bundle b=getIntent().getExtras();
        authToken=b.getString("token");
        Toast.makeText(getApplicationContext(),"Token: " + authToken,Toast.LENGTH_LONG).show();

        textViewName=(TextView)findViewById(R.id.tv_profile_name);
        textViewEmail=(TextView)findViewById(R.id.tv_profile_email);
        textViewPhone=(TextView)findViewById(R.id.tv_profile_phone);
        if(authToken==null){
            Toast.makeText(getApplicationContext(),"Something went wrong. AuthToken can't found\nPlease try later !!!",Toast.LENGTH_LONG).show();
        }else{
         //   new GetUserProfile().execute();
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
                jsonString=jsonGetHandler.requestJsonByUrl((ApiUrls.profileURL).toString(),JsonGetHandler.GET);
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
        if (id == R.id.action_settings) {
            return true;
        }if (id==R.id.action_home){

        }if (id==R.id.action_users){

        }if (id==R.id.action_logout){

        }
        return super.onOptionsItemSelected(item);
    }
}
