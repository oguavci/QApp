package com.example.qapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnBack = (Button) findViewById(R.id.buttonback);
        Button btnRegister = (Button) findViewById(R.id.button_register);
        TextView name = (TextView) findViewById(R.id.text_name);
        TextView lastName = (TextView) findViewById(R.id.text_last_name);
        TextView username = (TextView) findViewById(R.id.text_username_signUp);
        TextView email = (TextView) findViewById(R.id.text_email);
        TextView password = (TextView) findViewById(R.id.text_psw_signUp);

        View.OnClickListener handler = new View.OnClickListener(){

            public void onClick(View v) {

                if(v==btnBack){
                    // doStuff
                    Intent intentMain = new Intent(Register.this , Login.class);
                    Register.this.startActivity(intentMain);
                }

                if(v==btnRegister){
                    boolean passed = true;
                    if(TextUtils.isEmpty(username.getText())) {
                        username.setError("First name is required!");
                        passed = false;
                    }
                    if(TextUtils.isEmpty(password.getText())) {
                        password.setError("Password is required!");
                        passed = false;
                    }
                    if(TextUtils.isEmpty(email.getText())) {
                        email.setError("Email is required!");
                        passed = false;
                    }
                    if(TextUtils.isEmpty(name.getText())) {
                        name.setError("Name is required!");
                        passed = false;
                    }
                    if(TextUtils.isEmpty(lastName.getText())) {
                        lastName.setError("LastName name is required!");
                        passed = false;
                    }
                    if(!passed)
                        return;
                    Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
                    Matcher mat = pattern.matcher(email.getText());
                    if (mat.matches()) {
                        sendPostRequest(username.getText().toString(), password.getText().toString(),
                                name.getText().toString(),
                                lastName.getText().toString(),
                                email.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Insert a valid email!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };

        btnBack.setOnClickListener(handler);
        btnRegister.setOnClickListener(handler);
    }

    private void sendPostRequest(String username, String password,
                                 String name, String lastName, String email){
        System.out.println("ENTRATO");
        RequestQueue mQueue = Volley.newRequestQueue(Register.this);
        String url = "http://10.0.2.2:8080/register";

        StringRequest request = new StringRequest( Request.Method.POST, url,
                response -> {

                    try {

                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray  = object.getJSONArray("cities");
                        ArrayList<String> cities = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject city = jsonArray.getJSONObject(i);
                            cities.add(city.getString("name"));
                        }

                        Intent intent = new Intent(getBaseContext(), HomePage.class);
                        intent.putExtra("cities", cities);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            System.out.println("ERROR JSON");
            error.printStackTrace();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.statusCode == 401) {
                Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("name", name);
                params.put("lastName", lastName);
                params.put("email", email);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Authorization", "Bearer "+ TokenHandler.getToken());
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String jsonString = null;
                    try {
                        jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // set token after receiving from login response
                    String s = response.headers.get("Authorization");
                    s = s.replace("Bearer ", "" );
                    TokenHandler.setToken(s);

                    return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }
}