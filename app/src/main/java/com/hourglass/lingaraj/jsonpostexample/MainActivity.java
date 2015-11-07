package com.hourglass.lingaraj.jsonpostexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {

    EditText firstname,lastname,age,mobileNumber,email;
    Button submitDataToSerializableObject,writeDataToServer,openUrlInBrowser;
    JSONArray userProfileArray;
    String responseString;
    JSONObject userProfileArrayObject,parentJsonObject;
    int i=0;
    ProfileData profileData;
    UserProfiles userProfile;
    ProgressDialog progressDialog;
    Gson gson;
    TextView textViewToDisplayResultUrlWhereDataIsStored;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAvailableDataOnPostUrl();
        firstname=(EditText) findViewById(R.id.firstNameEditText);
        lastname=(EditText)findViewById(R.id.lastNameEditText);
        age=(EditText) findViewById(R.id.ageEditText);
        mobileNumber=(EditText)findViewById(R.id.mobile_editText);
        email=(EditText)findViewById(R.id.email_editText);
        submitDataToSerializableObject=(Button) findViewById(R.id.submit);
        writeDataToServer=(Button) findViewById(R.id.write_data_back_to_server);
        progressDialog = new ProgressDialog(MainActivity.this);
        openUrlInBrowser=(Button) findViewById(R.id.open_url_on_browser);
        textViewToDisplayResultUrlWhereDataIsStored=(TextView) findViewById(R.id.textviewForUrl);



        userProfileArray=new JSONArray();
        userProfileArrayObject=new JSONObject();
        parentJsonObject=new JSONObject();


        submitDataToSerializableObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataToSerializableObject();
            }

        });
        writeDataToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("writing Data Back to server");
                progressDialog.show();
                try {
                    writeDataBackToserver();
                }
                catch (Exception e)
                {
                    Log.v("writeBackEror",e.toString());
                }
            }
        });
        openUrlInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Uri uri = Uri.parse(textViewToDisplayResultUrlWhereDataIsStored.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });





    }

    private void writeDataBackToserver() {

        AsyncHttpClient writeDataBackToserver=new AsyncHttpClient();
        StringEntity entity=null;

        try {
           Gson gson = new Gson();
            String gsonString = gson.toJson(profileData,  ProfileData.class);
            //Gson convert Json data to String and send it as StringEntity with the URL.
            entity = new StringEntity(gsonString);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        writeDataBackToserver.post(getApplicationContext(), getString(R.string.postUrl), entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("successobject", response.toString());
                try {
                    textViewToDisplayResultUrlWhereDataIsStored.setText(response.get("uri").toString());
                    /* In response of our successfull POST call request, we will get a URL back from
                     *  "myjson.com", where the data we have sent is stored. If you are using MongoDb etc
                     *   this will be the same url which you used for POST call, which can also be used With GET Call.
                     */
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }


                progressDialog.dismiss();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.v("successarray", response.toString());

                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                progressDialog.dismiss();
            }

        });


    }


    private void getAvailableDataOnPostUrl()
    {
        /* This get Call is to say the Serializable object and Gson what are the
         *  key and value it have to match with. Like giving a structure to how the data will
         *  be written.
         */

       AsyncHttpClient httpClient=new AsyncHttpClient();
        httpClient.get(getString(R.string.serverUrl), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("successjObject", response.toString());
                try {

                    responseString = response.toString();
                    gson = new GsonBuilder().create();
                    profileData = gson.fromJson(responseString, ProfileData.class);
                    profileData.userProfiles = new ArrayList<UserProfiles>();

                    /* GET call will get the following object as response,
                    * { "userProfiles":[{}]}
                     * This will be passed to the Gson and Profile data class  to set the
                     *  members and values accordingly.
                     */


                } catch (Exception e) {
                    Log.v("arrayerror", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.v("successJArray", response.toString());
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.v("successresponsestring", responseString);
                super.onSuccess(statusCode, headers, responseString);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("failureJarray", errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("failureStringres", responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("failureJObject", errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }


    private void addDataToSerializableObject()
    {
        /* This method will add entered data to serializable objects
         *  new ArrayList<UserPorfiles> allow you to store and
         *  multiple userprofile values.
          */


        userProfile=new UserProfiles();
        userProfile.firstName=firstname.getText().toString();
        userProfile.lastName=lastname.getText().toString();
        userProfile.age=Integer.parseInt(age.getText().toString().trim());
        userProfile.mobileNumber=Integer.parseInt(mobileNumber.getText().toString().trim());
        userProfile.eMail=email.getText().toString();
        profileData.userProfiles.add(userProfile);
        firstname.setText("");
        lastname.setText("");
        mobileNumber.setText("");
        age.setText("");
        email.setText("");





    }
    
    


}
