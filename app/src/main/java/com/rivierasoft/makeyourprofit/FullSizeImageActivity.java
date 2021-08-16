package com.rivierasoft.makeyourprofit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class FullSizeImageActivity extends AppCompatActivity {

    PhotoView imageView;
    Intent intent;

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
        setContentView(R.layout.activity_full_size_image);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView = findViewById(R.id.imageView);

        intent = getIntent();

        Picasso.with(getApplicationContext())
                .load(intent.getStringExtra("url"))
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_broken_image)
                .into(imageView);
    }
}