package com.sahil.assignment0;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
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

    public void showAbout(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    //Onclick of button send the details
    public void submitMessage(View view){
        sendRequest();
    }

    //// TODO: 1/10/2016 Integrity check of parameters
    public HashMap<String,String> getParams(){
        final EditText name1 =(EditText) findViewById(R.id.editText_Name1);
        final EditText entry1 =(EditText) findViewById(R.id.editText_Entry1);

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("entry1",entry1.getText().toString());
        params.put("name1", name1.getText().toString());

        return params;
    }

    public static String response_message,response_title;
    //Does not work yet
    public void sendRequest(){
        final JSONObject jsonDetails= new JSONObject(getParams());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,jsonDetails, new Response.Listener<JSONObject>(){
            //Send JSON request and handle the response
            @Override
            public void onResponse(JSONObject response) {
                try{
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    response_message =response.getString("RESPONSE_MESSAGE");
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
    //It is working
    public static class showResponseDialogFragment extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(response_message).setTitle("response_title");
            return builder.create();
        }
    }

}
