package com.example.firebasetest1.FormalAct;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class StartActivity extends AppCompatActivity {

    private static int TIME_OUT = 1000;
    private static String uniqueID = null;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        mContext = this;
        uniqueID = tools.getID(getApplicationContext());
        if (uniqueID == null || uniqueID.isEmpty()){
            uniqueID = UUID.randomUUID().toString();
           tools.saveID(getApplicationContext(),uniqueID);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(StartActivity.this, QuestionActivity.class);
                startActivity(intent);
                finish();
            }, TIME_OUT);
        } else {
            new getHouseFromRest().execute();
           /* Intent intent = new Intent(StartActivity.this, QuestionActivity.class);
            startActivity(intent);*/

        }
    }

    protected class getHouseFromRest extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            String url = RestClient.BASE_URL +  "house/"+ uniqueID ;

            // prepare the Request
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        // display response
                        tools.saveString(getApplicationContext(),"house","house",response.toString());
                        Intent intent = new Intent(StartActivity.this,MainActivity.class);
                        startActivity(intent);
                    },
                    error ->{
                        try {
                            Log.d("Error.Response", error.getMessage());
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            tools.toast_long(getApplicationContext(),"Please check your Internet");
                            finish();
                        }

                    }

            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json; charset=utf-8");
                    params.put("Accept", "application/json");
                    return params;
                }
            };
            queue.add(getRequest);

            return null;
        }
    }

}

