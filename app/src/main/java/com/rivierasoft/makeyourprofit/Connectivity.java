package com.rivierasoft.makeyourprofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Connectivity {
    public static String isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if ((info != null && info.isConnected())) {
            return "yes";
        } else return "no";
    }

    public static String executeCmd(){
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 1 google.com");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            String res = "no";
            while ((s = stdInput.readLine()) != null) {
                res += s + "\n";
            }
            p.destroy();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "no";
    }
}

