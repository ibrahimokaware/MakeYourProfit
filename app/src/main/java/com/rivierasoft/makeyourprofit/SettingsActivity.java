package com.rivierasoft.makeyourprofit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

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
        setContentView(R.layout.activity_settings);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Drawable drawable = toolbar.getNavigationIcon();
        drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);

        Intent intent = getIntent();
        int s = intent.getIntExtra("s", 1);

        if (s == 1) {
            getSupportActionBar().setTitle("الملف الشخصي");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ProfileFragment profileFragment = ProfileFragment.newInstance(intent.getStringExtra("name"),
                    intent.getStringExtra("dob"), intent.getStringExtra("gender")
                    , intent.getStringExtra("phone"), intent.getStringExtra("address"));
            fragmentTransaction.replace(R.id.fragment_container, profileFragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if (s == 2) {
            getSupportActionBar().setTitle("تعليمات");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            InstructionsFragment instructionsFragment = InstructionsFragment.newInstance("inside", null);
            fragmentTransaction.replace(R.id.fragment_container, instructionsFragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if (s == 3) {
            getSupportActionBar().setTitle("السحب");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            WithdrawFragment withdrawFragment = new WithdrawFragment();
            fragmentTransaction.replace(R.id.fragment_container, withdrawFragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if (s == 4) {
            getSupportActionBar().setTitle("مساعدة");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            HelpFragment helpFragment = new HelpFragment();
            fragmentTransaction.replace(R.id.fragment_container, helpFragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if (s == 5) {
            getSupportActionBar().setTitle("الإشعارات");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            NotificationsFragment notificationsFragment = new NotificationsFragment();
            fragmentTransaction.replace(R.id.fragment_container, notificationsFragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}