package com.rivierasoft.makeyourprofit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;

public class VideoActivity extends AppCompatActivity {

    private WebView webView;
    private Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        Configuration newConfig = new Configuration(res.getConfiguration());
        Locale locale = new Locale( "ar" );
        newConfig.locale = locale;
        newConfig.setLocale(locale);
        newConfig.setLayoutDirection(locale);
        res.updateConfiguration( newConfig, null);
        setContentView(R.layout.activity_video);

        webView = findViewById(R.id.webView);

        intent = getIntent();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(intent.getStringExtra("link"));
        webView.setWebViewClient(new WebViewClient());
    }
}