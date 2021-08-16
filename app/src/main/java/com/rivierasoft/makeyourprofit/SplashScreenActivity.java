package com.rivierasoft.makeyourprofit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    Animation anim;
    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          //      WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView= findViewById(R.id.imageView);
        textView= findViewById(R.id.textView);

        //Typeface typeface = Typeface.createFromAsset(getAssets(),"font.ttf");
        //textView.setTypeface(typeface);

        //ReplaceFont.replaceDefaultFont(this,"DEFAULT","font.ttf");

        // Declare an imageView to show the animation.
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        // Create the animation.
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);
                if (sharedPreferences.getInt("status",1) == 1)
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                else startActivity(new Intent(getApplicationContext(), MainActivity.class));
                // HomeActivity.class is the activity to go after showing the splash screen.
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        imageView.startAnimation(anim);
        textView.startAnimation(anim);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}