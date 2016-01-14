package com.sahil.assignment0;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final String URL = "http://agni.iitd.ernet.in/cop290/assign0/register/";
    final String reqTag = "JSONRequest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //TODO Add a settings menu maybe?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //shows About page activity
    public void showAbout(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    //Onclick of button send the details if internet is connected, otherwise ask to connect to WiFi
    //Todo ask WiFi or Data depending on situation
    public void submitMessage(View view){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        boolean isConnected = network !=null && network.isConnectedOrConnecting();
        if(isConnected) {
            sendRequest();
        }
        else{
            DialogFragment showInternet= new showInternetDialogFragment();
            showInternet.show(getFragmentManager(),"showInternet");
        }
    }

    // Creates the params for JSON file which will be sent as request
    //// TODO: 1/10/2016 Integrity check of parameters
    public HashMap<String,String> getParams(){
        final EditText team_name =(EditText) findViewById(R.id.teamName);
        final EditText name1 =(EditText) findViewById(R.id.editText_Name1);
        final EditText entry1 =(EditText) findViewById(R.id.editText_Entry1);
        final EditText name2 =(EditText) findViewById(R.id.editText_Name2);
        final EditText entry2 =(EditText) findViewById(R.id.editText_Entry2);
        final EditText name3 =(EditText) findViewById(R.id.editText_Name3);
        final EditText entry3 =(EditText) findViewById(R.id.editText_Entry3);

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("teamname", team_name.getText().toString());
        params.put("entry1",entry1.getText().toString());
        params.put("name1", name1.getText().toString());
        params.put("entry2",entry2.getText().toString());
        params.put("name2", name2.getText().toString());
        params.put("entry3",entry3.getText().toString());
        params.put("name3", name3.getText().toString());

        return params;
    }

    public static String response_message,response_title;

    //method: Sends the JSON request based on the params generated from text fields
    public void sendRequest(){
        final JSONObject jsonDetails= new JSONObject(getParams());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,jsonDetails, new Response.Listener<JSONObject>(){
            //Send JSON request and handle the response
            @Override
            public void onResponse(JSONObject response) {
                try{
                    //VolleyLog.v("Response:%n %s", response.toString(4));
                    response_message =response.getString("RESPONSE_MESSAGE");
                    response_title=response.getString("RESPONSE_SUCCESS");
                    response_title="Response Status: ".concat(response_title);
                    DialogFragment message=new showResponseDialogFragment();
                    message.show(getFragmentManager(),"message");
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            //Handle Error Responses
            @Override
            public void onErrorResponse(VolleyError error) {
                response_message =error.getMessage();
                DialogFragment message=new showResponseDialogFragment();
                message.show(getFragmentManager(),"message");
            }
        });
        request.setTag(reqTag);
        RequestQueue mQueue = Volley.newRequestQueue(this);
        mQueue.add(request);
    }
    //Shows Dialog Displaying the Result of POST query
    public static class showResponseDialogFragment extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(response_message).setTitle(response_title);
            return builder.create();
        }
    }

    //Shows dialog if internet is not connected to connect to the internet
    //TODO Possibly add another button, one to connect to WiFi and another for Data
    public static class showInternetDialogFragment extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please Connect to the Internet to continue").setTitle("Not Connected to Internet")
            .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
                //Cancels the dialog
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showInternetDialogFragment.this.getDialog().cancel();
                }
            })
            .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                //Starts activity to open WiFi settings
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            return builder.create();
        }
    }

    //To check if any fields in the form are empty
    

}
