package com.rivierasoft.makeyourprofit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class CreateActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        Configuration newConfig = new Configuration( res.getConfiguration() );
        Locale locale = new Locale( "ar" );
        newConfig.locale = locale;
        newConfig.setLocale(locale);
        newConfig.setLayoutDirection( locale );
        res.updateConfiguration( newConfig, null );
        setContentView(R.layout.activity_create);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CreateFragment createFragment = new CreateFragment();
        fragmentTransaction.replace(R.id.fragment_container, createFragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}