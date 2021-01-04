package com.example.qapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity{

    Button btnLogin;
    TextView textRegister;
    TextView username;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btn_sign_ing);
        textRegister = (TextView) findViewById(R.id.text_sign_up);
        username = (TextView) findViewById(R.id.text_username);
        password = (TextView) findViewById(R.id.text_psw);


        View.OnClickListener handler = new View.OnClickListener(){

            public void onClick(View v) {

                if(v==btnLogin){
                    if(TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText())){
                        if(TextUtils.isEmpty(username.getText()))
                            username.setError( "First name is required!" );
                        if(TextUtils.isEmpty(password.getText()))
                            password.setError( "Password is required!" );
                    }
                    else
                    {
                        if(username.getText().toString().contains("@")) {
                            Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
                            Matcher mat = pattern.matcher(username.getText());
                            if (mat.matches()) {
                                sendRequest(username.getText().toString(), password.getText().toString());
                            } else {
                                Toast.makeText(getApplicationContext(), "Insert a valid email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                            sendRequest(username.getText().toString(), password.getText().toString());
                    }
                }

                if(v==textRegister){
                    // doStuff
                    Intent intentMain = new Intent(Login.this ,
                            Register.class);
                    Login.this.startActivity(intentMain);
                    Log.i("Content "," Main layout ");
                }
            }
        };

        btnLogin.setOnClickListener(handler);
        textRegister.setOnClickListener(handler);
    }

    private void sendRequest(String username, String password){
        System.out.println("ENTRATO");
        RequestQueue mQueue = Volley.newRequestQueue(Login.this);
        String url = "http://10.0.2.2:8080/login?username="+username+"&password="+password;

        JsonObjectRequest request = new JsonObjectRequest( url,null,
                response -> {

                    try {

                        JSONArray jsonArray = response.getJSONArray("cities");
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
                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        try {
                            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                            JSONObject jsonResponse = new JSONObject(jsonString);
                            // set token after receiving from login response
                            String s = response.headers.get("Authorization");
                            s = s.replace("Bearer ", "" );
                            TokenHandler.setToken(s);
                            System.out.println("CHECK BEAR, RICEVUTO : "+s);
                            return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException | JSONException e) {
                            return Response.error(new ParseError(e));
                        }
                    }
                };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }
}