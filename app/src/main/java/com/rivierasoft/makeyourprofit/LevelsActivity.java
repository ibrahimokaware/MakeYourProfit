package com.rivierasoft.makeyourprofit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class LevelsActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    Intent intent;

    @SuppressLint("ResourceAsColor")
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
        setContentView(R.layout.activity_levels);

        intent = getIntent();

        textView = findViewById(R.id.textView7);
        imageView = findViewById(R.id.imageView5);

        textView.setText(intent.getStringExtra("title"));

        switch (intent.getIntExtra("type",1)) {
            case 1: imageView.setImageResource(R.drawable.image_category_knowledge_raster);
                break;
            case 2: imageView.setImageResource(R.drawable.mosque);
                break;
            case 3: imageView.setImageResource(R.drawable.image_category_geography_raster);
                break;
            case 4: imageView.setImageResource(R.drawable.image_category_sports_raster);
                break;
            case 5: imageView.setImageResource(R.drawable.flag);
                break;
            case 6: imageView.setImageResource(R.drawable.image_category_history_raster);
                break;
            case 7: imageView.setImageResource(R.drawable.athlete);
                break;
            case 8: imageView.setImageResource(R.drawable.club);
                break;
            case 9: imageView.setImageResource(R.drawable.people);
                break;
            case 10: imageView.setImageResource(R.drawable.apple);
                break;
            case 11: imageView.setImageResource(R.drawable.squares);
                break;
            case 12: imageView.setImageResource(R.drawable.tic_tac_toe);
                break;
        }

        if (intent.getIntExtra("type",1) == 11 || intent.getIntExtra("type",1) == 12) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                LevelsGameFragment levelsGameFragment = LevelsGameFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1));
                fragmentTransaction.replace(R.id.fragment_container, levelsGameFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                LevelsFragment levelsFragment = LevelsFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1));
                fragmentTransaction.replace(R.id.fragment_container, levelsFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
    }

    @Override
    public void onBackPressed() {
        if (intent.getIntExtra("back", 1) != 1) {
            super.onBackPressed();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else super.finish();
        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}