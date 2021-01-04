package com.example.qapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.StringUtils;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Booking extends Fragment {

    Button btnNext;

    TextView store_text;

    TextView date;
    TextView timeslot;
    TextView duration;
    CheckBox estimate;
    DatePickerDialog.OnDateSetListener setListener;
    private NumberPicker np;
    ArrayList<String> numbers;
    Boolean which = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_booking,container, false);

        ((HomePage)getActivity()).changeBack(true);
        QRCodeWriter writer = new QRCodeWriter();

        btnNext = view.findViewById(R.id.button_next);
        store_text = view.findViewById(R.id.store_text);
        date = view.findViewById(R.id.editTextDate);
        timeslot = view.findViewById(R.id.editTextDate2);
        duration = view.findViewById(R.id.editTextNumber);
        estimate = view.findViewById(R.id.checkBox);
        np = view.findViewById(R.id.pid_inputPicker);



        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        estimate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    duration.setText(Integer.toString(5));
                }

            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth+"/"+month+"/"+year;
                view.setMinDate(System.currentTimeMillis() - 1000);
                //
            }
        };

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                np.setVisibility(View.GONE);
                DatePickerDialog d = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date_ = dayOfMonth+"/"+month+"/"+year;
                        date.setText(date_);
                    }
                }, year, month, day);
                d.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                d.show();
            }
        });


        timeslot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(date.getText().toString().equals("") || date.getText().toString()==null || date.getText().toString().isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(), "Insert the date before", Toast.LENGTH_SHORT).show();
                }
                else{
                    numbers = new ArrayList<String>();
                    which = true;
                    String startHour;
                    String endHour = "19:00";

                    Calendar c = Calendar.getInstance();

                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    Date today = c.getTime();

                    long todayInMillis = c.getTimeInMillis();


                    String[] dateS = date.getText().toString().split ( "/" );
                    int dayOfMonth = Integer.parseInt ( dateS[0]);
                    int month = Integer.parseInt ( dateS[1] );
                    int year = Integer.parseInt ( dateS[2] );;

                    System.out.println(year+"_"+month+"_"+dayOfMonth);

                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    Date dateSpecified = c.getTime();

                    if (dateSpecified.after(today)) {
                        System.out.println("IS AFTER");
                        startHour = "8:30";
                    } else {
                        System.out.println("IS NOT AFTER");
                        startHour = "17:30";
                    }

                    String[] timeS = startHour.split ( ":" );
                    int hourS = Integer.parseInt ( timeS[0].trim() );
                    int minS = Integer.parseInt ( timeS[1].trim() );

                    String[] timeE = endHour.split ( ":" );
                    int hourE = Integer.parseInt ( timeE[0].trim() );
                    int minE = Integer.parseInt ( timeE[1].trim() );

                    String put;
                    int step = 30;
                    while(hourS!=hourE || minS != minE)
                    {
                        if(minS != 0)
                            put = Integer.toString(hourS)+":"+Integer.toString(minS);
                        else
                            put = Integer.toString(hourS)+":00";
                        numbers.add(put);
                        minS += step;
                        if (minS == 60)
                        {
                            minS = 0;
                            hourS = hourS + 1;
                        }
                    }
                    for(String n:numbers)
                        System.out.println(n);

                    np.setVisibility(View.VISIBLE);

                    np.setMaxValue(numbers.size()-1);
                    np.setMinValue(0);
                    np.setWrapSelectorWheel(false);
                    np.setDisplayedValues(numbers.toArray(new String[0]));
                    np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    //if(date.getText()) se seleziono prima la data ecc..
                }



            }
        });



        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                numbers = new ArrayList<String>();
                which = false;
                String start = "15";
                String end = "120";

                int istart = Integer.parseInt(start);
                int estart = Integer.parseInt(end);

                String put;

                int step = 15;
                while(istart!=estart)
                {
                    put = Integer.toString(istart);
                    numbers.add(put);
                    istart = istart + step;
                }

                np.setDisplayedValues(null);
                np.setVisibility(View.VISIBLE);

                np.setMaxValue(numbers.size()-1);
                np.setMinValue(0);
                np.setWrapSelectorWheel(false);
                np.setDisplayedValues(numbers.toArray(new String[0]));
                np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                //if(date.getText()) se seleziono prima la data ecc..

                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        //timeslot.setText(newVal);
                    }
                });

            }
        });

        np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(which) {
                    String selected = numbers.get(np.getValue());
                    timeslot.setText(selected);
                }
                else
                {
                    String selected = numbers.get(np.getValue());
                    duration.setText(selected);
                }
                np.setVisibility(View.GONE);
            }
        });

        Bundle bundle = this.getArguments();
        String str = getArguments().getString("store_text");
        store_text.setText(str);

        View.OnClickListener handler = new View.OnClickListener(){

            public void onClick(View v) {

                if(v==btnNext){
                    Bundle bundle = new Bundle();
                    //bundle.putString("date", date.getText().toString());
                    //bundle.putString("timeslot", timeslot.getText().toString());
                    //bundle.putString("duration", duration.getText().toString());
                    Fragment fragment = new Category();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment, "category");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

            }
        };

        btnNext.setOnClickListener(handler);

        return view;
    }
}
