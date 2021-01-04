package com.example.qapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Category extends Fragment {

    ArrayList<CardView> cardViews;
    GridLayout g;
    CardView c;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_categories,container, false);
        g = view.findViewById(R.id.grid_cards);
        ((HomePage)getActivity()).changeBack(true);
        cardViews = new ArrayList<CardView>();
        //PARAMETRI

        LinearLayout l;
        CardView.LayoutParams cp;
        LinearLayout.LayoutParams lp;
        LinearLayout.LayoutParams lp_t;
        LinearLayout.LayoutParams lp_image;
        ImageView image;
        TextView t;
        String imageName;
        int d_image;
        for(int i = 0; i< 8;++i){
            c = new CardView(getActivity().getApplicationContext());
            cp = new CardView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.setMargins(70,40,0,0);
            c.setLayoutParams(cp);
            c.setRadius(30);
            c.setCardElevation(6);
            l = new LinearLayout(getActivity().getApplicationContext());
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            l.setGravity(Gravity.CENTER);
            l.setOrientation(LinearLayout.VERTICAL);
            l.setPadding(16,0,0,0);
            image = new AppCompatImageView(getActivity().getApplicationContext());
            t = new TextView(getActivity().getApplicationContext());
            lp_t = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp_t.setMargins(0,12,0,0);
            lp_image = new LinearLayout.LayoutParams(300,
                    300);
            image.setLayoutParams(lp_image);

            switch(i){
                case 0:
                    d_image = R.drawable.babyboy;
                    imageName = getResources().getResourceName(d_image);
                    image.setImageResource(d_image);
                    t.setText("name");
                    c.setId(R.id.layout0);
                    break;
                case 1:
                    d_image = R.drawable.bread;
                    imageName = getResources().getResourceName(d_image);
                    image.setImageResource(d_image);
                    t.setText("name");
                    c.setId(R.id.layout1);
                    break;
                case 2:
                    d_image = R.drawable.breakfast;
                    imageName = getResources().getResourceName(d_image);
                    image.setImageResource(d_image);
                    t.setText("name");
                    c.setId(R.id.layout2);
                    break;
                case 3:
                    d_image = R.drawable.drinks;
                    imageName = getResources().getResourceName(d_image);
                    image.setImageResource(d_image);
                    t.setText("name");
                    c.setId(R.id.layout3);
                    break;
                case 4:
                    d_image = R.drawable.food;
                    imageName = getResources().getResourceName(d_image);
                    image.setImageResource(d_image);
                    t.setText("name");
                    c.setId(R.id.layout4);
                    break;
                case 5:
                    d_image = R.drawable.ginger;
                    imageName = getResources().getResourceName(d_image);
                    image.setImageResource(d_image);
                    t.setText("name");
                    c.setId(R.id.layout5);
                    break;
                case 6:
                    d_image = R.drawable.glass;
                    imageName = getResources().getResourceName(d_image);
                    image.setImageResource(d_image);
                    t.setText("name");
                    c.setId(R.id.layout6);
                    break;
                case 7:
                    d_image = R.drawable.spice;
                    imageName = getResources().getResourceName(d_image);
                    image.setImageResource(d_image);
                    t.setText("name");
                    c.setId(R.id.layout7);
                    break;
            }
            t.setLayoutParams(lp_t);
            t.setTextSize(18);
            l.addView(image);
            l.addView(t);
            c.setClickable(true);
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cardViews.contains((CardView)v))
                    {
                        cardViews.remove((CardView)v);
                        v.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    else{
                        cardViews.add((CardView)v);
                        v.setBackgroundColor(getResources().getColor(R.color.teal_200));
                    }

                }
            });
            c.addView(l);
            g.addView(c);
            System.out.println(i);


        }

        return view;
    }
}
