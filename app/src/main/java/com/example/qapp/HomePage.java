package com.example.qapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ImageView go_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        System.out.println("Entered");

        JSONObject json = null;
        String name = null;
        String username = null;
        String email = null;
        try {
            json = new JSONObject(TokenHandler.decodeToken());
            System.out.println(TokenHandler.decodeToken());
            name  = json.getString("name");
            username = json.getString("username");
            email  = json.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(name + username + email);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Hello "+name.toUpperCase());

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.menu_user);
        TextView navEmail = (TextView) headerView.findViewById(R.id.menu_email);

        navUsername.setText(username);
        navEmail.setText(email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        go_back = toolbar.findViewById(R.id.back_arrow);
        go_back.setVisibility(View.INVISIBLE);
        View.OnClickListener handler = new View.OnClickListener(){

            public void onClick(View v) {

                if(v==go_back){
                    // doStuff
                    Fragment fchain = (Fragment) getSupportFragmentManager().findFragmentByTag("chain");
                    Fragment fstore = (Fragment) getSupportFragmentManager().findFragmentByTag("store");
                    Fragment fticket = (Fragment) getSupportFragmentManager().findFragmentByTag("ticket");
                    Fragment fbooking = (Fragment) getSupportFragmentManager().findFragmentByTag("booking");
                    Fragment fcategory = (Fragment) getSupportFragmentManager().findFragmentByTag("category");
                    Fragment fqrcode = (Fragment) getSupportFragmentManager().findFragmentByTag("qrcodeshow");
                    Fragment flineup = (Fragment) getSupportFragmentManager().findFragmentByTag("lineup");
                    if (fchain != null && fchain.isVisible()) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new City(), "city").commit();
                        return;
                    }
                    if (fstore != null && fstore.isVisible()) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fchain, "chain").commit();
                        return;
                    }
                    if (fticket != null && fticket.isVisible()) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fstore, "store").commit();
                        return;
                    }
                    if (fbooking != null && fbooking.isVisible()) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fticket, "ticket").commit();
                        return;
                    }
                    if (fcategory != null && fcategory.isVisible()) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fbooking, "booking").commit();
                        return;
                    }
                    if (fqrcode != null && fqrcode.isVisible()) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, flineup, "lineup").commit();
                        return;
                    }

                }
            }
        };
        go_back.setOnClickListener(handler);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new City(), "city").commit();
            navigationView.setCheckedItem(R.id.nav_explore);
            System.out.println("ENTRATO DOVE VOLEVO");
        }
    }

    public void changeBack(boolean visible){
        if(visible) {
            go_back.setVisibility(View.VISIBLE);
            go_back.setImageResource(R.drawable.abc_ic_ab_back_material);
        }
        else {
            go_back.setVisibility(View.VISIBLE);
            go_back.setImageResource(R.drawable.q_24);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        System.out.println("ENTRATO");
        switch (item.getItemId()){

            case R.id.nav_explore:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new City(),"city").commit();
                System.out.println("changed");
                break;

            case R.id.nav_ticket:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Ticket(), "ticket").commit();
                System.out.println("changed");
                break;

            case R.id.nav_logout:
                TokenHandler.setToken("");
                Intent homeIntent = new Intent(HomePage.this, Login.class);
                startActivity(homeIntent);
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}