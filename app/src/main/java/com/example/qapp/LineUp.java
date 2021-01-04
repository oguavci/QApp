package com.example.qapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LineUp extends Fragment {

    Button btnShow;
    Button btnCancel;
    Button btnHome;
    //timerHandler.removeCallbacks(timerRunnable); for stop
    ImageView img;
    String content;
    TextView people;
    TextView timer;
    long startTime = 0;
    String t;
    int t_int;
    String id;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            int print = (int) (t_int - minutes);
            if(print == 0) {
                //people.setText("The store is free!");
                //timer.setText("You can come!");
                timerHandler.removeCallbacks(timerRunnable);
                return;
            }
            else
                timer.setText(String.format("Your turn will come in about %d min!", print));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_lineup,container, false);
        ((HomePage)getActivity()).changeBack(false);
        QRCodeWriter writer = new QRCodeWriter();
        try {
            content = "ciao";
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            img = view.findViewById(R.id.qr_img);
            btnShow = view.findViewById(R.id.button_show);
            btnHome = view.findViewById(R.id.button_home);
            btnCancel = view.findViewById(R.id.button_cancel);
            img.setImageBitmap(bmp);
            Bundle bundle = this.getArguments();
            String p = getArguments().getString("numPeople");
            t = getArguments().getString("time");
            id = getArguments().getString("id");

            people = view.findViewById(R.id.peopletext);
            timer = view.findViewById(R.id.timertext);

            t_int = Integer.parseInt(t);
            if(t_int == 0) {
                //people.setText("The store is free!");
                //timer.setText("You can come!");
            }
            else{
                int t_int_min = (int)t_int/60;
                people.setText("There are "+p+" people before you!");
                timer.setText("Your turn will come in about "+t+ "min!");
                }

            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        View.OnClickListener handler = new View.OnClickListener(){

            public void onClick(View v) {

                if(v==btnShow){
                    Bundle bundle = new Bundle();
                    bundle.putString("qr_string", content);
                    Fragment fragment = new ShowQRCode();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment, "qrcodeshow");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if(v==btnCancel){
                    sendDeleteRequest(id);
                }
                if(v==btnHome){

                    Fragment fragment = new City();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment, "city");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        };

        btnShow.setOnClickListener(handler);
        btnCancel.setOnClickListener(handler);
        btnHome.setOnClickListener(handler);

        return view;
    }

    private void sendDeleteRequest(String id){
        System.out.println("ENTRATO");
        RequestQueue mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://10.0.2.2:8080/lineup";

        StringRequest request = new StringRequest( Request.Method.DELETE, url,
                response -> {

                        Fragment fragment = new City();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragment, "city");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

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
                params.put("ticketId", id);
                //params.put("storeId", id);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println(TokenHandler.getToken());
                params.put("Authorization","Bearer "+ TokenHandler.getToken());
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
