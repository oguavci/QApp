package com.example.qapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ticket extends Fragment {

    Button lineup_button, booking_button;
    TextView store_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ticket_created,container, false);
        ((HomePage)getActivity()).changeBack(true);
        lineup_button = view.findViewById(R.id.button2);
        booking_button = view.findViewById(R.id.button3);
        store_text = view.findViewById(R.id.textView11);
        Bundle bundle = this.getArguments();
        String str = getArguments().getString("namestore");
        String id = getArguments().getString("idstore");
        store_text.setText("Selected store: "+str);


        View.OnClickListener handler = new View.OnClickListener(){

            public void onClick(View v) {

                if(v==lineup_button){
                    sendPostRequest(id);
                }

                if(v==booking_button){
                    // doStuff
                    Bundle bundle = new Bundle();
                    bundle.putString("store_text", str);
                    bundle.putString("store_id", id);
                    Fragment fragment = new Booking();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment, "booking");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        };

        lineup_button.setOnClickListener(handler);
        booking_button.setOnClickListener(handler);

        return view;
    }

    private void sendPostRequest(String id){
        System.out.println("ENTRATO");
        RequestQueue mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://10.0.2.2:8080/lineup";

        StringRequest request = new StringRequest( Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        Bundle bundle = new Bundle();
                        bundle.putString("numPeople", object.getString("numPeople"));
                        //QRCODE CONTENT
                        bundle.putString("time", object.getString("time"));
                        //bundle.putString("id", object.getString("time"));
                        bundle.putString("id", "1");
                        Fragment fragment = new LineUp();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragment, "lineup");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            System.out.println("ERROR JSON");
            error.printStackTrace();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.statusCode == 401) {
                Toast.makeText(getActivity().getApplicationContext(), "The store is full. Retry later", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("storeId", id);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println(TokenHandler.getToken());
                params.put("Authorization","Bearer "+  TokenHandler.getToken());
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }
}
