package com.example.qapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Store extends Fragment {

    ListView lv;
    ArrayList<String> al;
    ArrayAdapter<String> aa;
    HashMap<String, String> stores = new HashMap<String, String>();


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_store,container, false);
        ((HomePage)getActivity()).changeBack(true);
        lv = view.findViewById(R.id.list_3);
        Bundle bundle = this.getArguments();
        stores = (HashMap<String, String>)  getArguments().getSerializable("stores");
        System.out.println(stores.get("STORE_1"));
        al = new ArrayList<String>();
        for ( String key : stores.keySet() ) {
            al.add(key);
        }
        aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, al);
        lv.setAdapter(aa);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = al.get(position);
                System.out.println(s);

                Bundle bundle = new Bundle();
                bundle.putString("namestore", s);
                bundle.putString("idstore", stores.get(s));
                System.out.println(stores.get(s+"_"+stores.get(s)));

                Fragment fragment = new Ticket();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment, "ticket");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                System.out.println("ARRIVATO");
            }
        });


        return view;
    }

}
