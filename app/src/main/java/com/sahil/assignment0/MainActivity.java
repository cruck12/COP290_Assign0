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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    final String URL = "http://agni.iitd.ernet.in/cop290/assign0/register/";
    final String reqTag = "JSONRequest";
    RequestQueue mQueue ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this.getApplicationContext());
        //initialize the spinner to display 2 and 3
        Spinner dropdown = (Spinner)findViewById(R.id.no_members);
        String[] items = new String[]{"2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
    }

    //to diasble the text fields when 2 members are selected and enable them when 3 are selected
    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id)
    {
        String s=parent.getItemAtPosition(pos).toString();
        if(s.equals("3"))
        {
            final EditText name3 =(EditText) findViewById(R.id.editText_Name3);
            final EditText entry3 =(EditText) findViewById(R.id.editText_Entry3);
            name3.setEnabled(true);
            entry3.setEnabled(true);
        }
        else if(s.equals("2"))
        {
            final EditText name3 =(EditText) findViewById(R.id.editText_Name3);
            final EditText entry3 =(EditText) findViewById(R.id.editText_Entry3);
            name3.setEnabled(false);
            entry3.setEnabled(false);
            name3.setText("");
            entry3.setText("");
        }
    }

    public void onNothingSelected(AdapterView<?> parent)
    {
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(mQueue!=null)
            mQueue.cancelAll(reqTag);
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
            if(!checkEmpty()) {
                Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!checkEntry())
            {
                Toast.makeText(this, "The entry numbers are not right!", Toast.LENGTH_SHORT).show();
            }
            else {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo network = cm.getActiveNetworkInfo();
                boolean isConnected = network != null && network.isConnectedOrConnecting();
                if (isConnected) {
                    sendRequest();
                } else {
                    DialogFragment showInternet = new showInternetDialogFragment();
                    showInternet.show(getFragmentManager(), "showInternet");
                }
            }
    }

    // Creates the params for JSON file which will be sent as request
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
    /*
    public void sendRequest(){
        final JSONObject jsonDetails= new JSONObject(getParams());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST ,URL ,new JSONObject(getParams()) , new Response.Listener<JSONObject>(){
            //Send JSON request and handle the response
            @Override
            public void onResponse(JSONObject response) {
                try{
                    VolleyLog.v("Request:%n %s", jsonDetails.toString(4));
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
    */
    public void sendRequest() {
        CustomJsonRequest request = new CustomJsonRequest(Request.Method.POST, URL, getParams(), new Response.Listener<JSONObject>() {
            //Send JSON request and handle the response
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //VolleyLog.v("Request:%n %s", jsonDetails.toString(4));
                    response_message = response.getString("RESPONSE_MESSAGE");
                    response_title = response.getString("RESPONSE_SUCCESS");
                    response_title = "Response Status: ".concat(response_title);
                    DialogFragment message = new showResponseDialogFragment();
                    message.show(getFragmentManager(), "message");
                } catch (JSONException e) {
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
        mQueue.add(request);
    }

    //Clears the text fields
    public void resetFields(View view) {
        final EditText team_name =(EditText) findViewById(R.id.teamName);
        final EditText name1 =(EditText) findViewById(R.id.editText_Name1);
        final EditText entry1 =(EditText) findViewById(R.id.editText_Entry1);
        final EditText name2 =(EditText) findViewById(R.id.editText_Name2);
        final EditText entry2 =(EditText) findViewById(R.id.editText_Entry2);
        final EditText name3 =(EditText) findViewById(R.id.editText_Name3);
        final EditText entry3 =(EditText) findViewById(R.id.editText_Entry3);

        team_name.setText("");
        name1.setText("");
        name2.setText("");
        name3.setText("");
        entry1.setText("");
        entry2.setText("");
        entry3.setText("");

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
    public boolean checkEmpty()
    {
        EditText team_name =(EditText) findViewById(R.id.teamName);
        String s = team_name.getText().toString();
        if(s.length()==0) {
            return false;
        }
        EditText name1 =(EditText) findViewById(R.id.editText_Name1);
        s = name1.getText().toString();
        if(s.length()==0) {
            return false;
        }
        EditText entry1 =(EditText) findViewById(R.id.editText_Entry1);
        s = entry1.getText().toString();
        if(s.length()==0) {
            return false;
        }
        EditText name2 =(EditText) findViewById(R.id.editText_Name2);
        s = name2.getText().toString();
        if(s.length()==0) {
            return false;
        }
        EditText entry2 =(EditText) findViewById(R.id.editText_Entry2);
        s = entry2.getText().toString();
        if(s.length()==0) {
            return false;
        }
        Spinner d = (Spinner)findViewById(R.id.no_members);
        String spinner = d.getSelectedItem().toString();
        if(spinner.equals("3"))
        {
            EditText name3 = (EditText) findViewById(R.id.editText_Name3);
            s = name3.getText().toString();
            if(s.length()==0) {
                return false;
            }
            EditText entry3 = (EditText) findViewById(R.id.editText_Entry3);
            s = entry3.getText().toString();
            if(s.length()==0) {
                return false;
            }

        }
        return true;
    }

    //To check if any string has the entry number format
    public boolean checkString (String s)
    {
        char c;
        int d;
        if(s.length()!=11)
        {
            return false;
        }
        for(int i=0;i<4;i++) {
            c = s.charAt(i);
            d = (int) c;
            if(d<48 || d>57) {
                return false;
            }
        }
        for(int i=6;i<s.length();i++) {
            c = s.charAt(i);
            d = (int) c;
            if(d<48 || d>57) {
                return false;
            }
        }
        return true;
    }
    //To check if the entry number fields has the entry number format
    public boolean checkEntry()
    {
        String s;
        final EditText entry1 =(EditText) findViewById(R.id.editText_Entry1);
        s = entry1.getText().toString();
        if(!checkString(s))
        {
            return false;
        }
        final EditText entry2 =(EditText) findViewById(R.id.editText_Entry2);
        s = entry2.getText().toString();
        if(!checkString(s))
        {
            return false;
        }
        final EditText entry3 =(EditText) findViewById(R.id.editText_Entry3);
        s = entry3.getText().toString();
        if(s.length()!=0 && (!checkString(s)))
        {
            return false;
        }
        return true;
    }

}
