package com.example.qapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public final class TokenHandler {
    private TokenHandler() {}

    private static String token = "";

    public static void setToken(String newToken) {
        if (newToken != null)
            token = newToken;
    }

    public static String getToken() {
        return token;
    }


    public static String decodeToken() throws UnsupportedEncodingException {
        String[] parts = token.split("\\.", 0);

        int i = 0;
        for (String part : parts) {
            byte[] bytes = Base64.decode(part, Base64.DEFAULT);
            String decodedString = new String(bytes, StandardCharsets.UTF_8);

            System.out.println("Decoded: " + decodedString);
            if(i == 1)
                return decodedString;
            i++;
        }
        return null;
    }
}