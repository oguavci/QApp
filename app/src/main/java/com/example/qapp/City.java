package com.example.qapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class City extends Fragment {

    View view;
    ListView lv;
    ArrayList<String> al;
    ArrayAdapter<String> aa;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_cities,container, false);
        ((HomePage)getActivity()).changeBack(false);
        lv = view.findViewById(R.id.list_1);
        al = getActivity().getIntent().getExtras().getStringArrayList("cities");
        aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, al);
        lv.setAdapter(aa);
        System.out.println(al.get(2));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = al.get(position);
                sendRequest(s);
            }
        });


        return view;
    }

    private void sendRequest(String city){
        System.out.println("ENTRATO");
        RequestQueue mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://10.0.2.2:8080/chain?city="+city;

        JsonObjectRequest request = new JsonObjectRequest( url,null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("chains");
                        ArrayList<String> stores = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject store = jsonArray.getJSONObject(i);
                            stores.add(store.getString("name"));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("chains", stores);
                        Fragment fragment = new Chain();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragment, "chain");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        System.out.println("ARRIVATO chains");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            System.out.println("ERROR JSON");
            error.printStackTrace();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.statusCode == 401) {
                Toast.makeText(getActivity().getApplicationContext(), "Anhutorized. You not logged in", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println(TokenHandler.getToken());
                params.put("Authorization","Bearer "+ TokenHandler.getToken());
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