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

import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_quiz);

        intent = getIntent();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (intent.getIntExtra("type",1) < 5) {
                QuizFragment quizFragment = QuizFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1), intent.getIntExtra("level",1));
                fragmentTransaction.replace(R.id.fragment_container, quizFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        } else if (intent.getIntExtra("type",1) < 9) {
                LettersFragment lettersFragment = LettersFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1),intent.getIntExtra("level",1));
                fragmentTransaction.replace(R.id.fragment_container, lettersFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        } else if (intent.getIntExtra("type",1) == 9) {
                DifferenceFragment differenceFragment = DifferenceFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1),intent.getIntExtra("level",1) );
                fragmentTransaction.replace(R.id.fragment_container, differenceFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        } else if (intent.getIntExtra("type",1) == 10) {
            LogoFragment logoFragment = LogoFragment.newInstance(intent.getStringExtra("title"),
                    intent.getIntExtra("type",1),intent.getIntExtra("level",1) );
            fragmentTransaction.replace(R.id.fragment_container, logoFragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (intent.getIntExtra("type",1) == 11) {
            if (intent.getIntExtra("level",1) == 1) {
                MatchPicturesFragment matchPicturesFragment = MatchPicturesFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1), intent.getIntExtra("level",1) );
                fragmentTransaction.replace(R.id.fragment_container, matchPicturesFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else if (intent.getIntExtra("level",1) == 2) {
                MatchPicturesFragment2 matchPicturesFragment2 = MatchPicturesFragment2.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1), intent.getIntExtra("level",1) );
                fragmentTransaction.replace(R.id.fragment_container, matchPicturesFragment2);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else if (intent.getIntExtra("level",1) == 3) {
                MatchPicturesFragment3 matchPicturesFragment3 = MatchPicturesFragment3.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1), intent.getIntExtra("level",1) );
                fragmentTransaction.replace(R.id.fragment_container, matchPicturesFragment3);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        } else if (intent.getIntExtra("type",1) == 12) {
            if (intent.getIntExtra("level",1) == 1) {
                TicTacToeFragment ticTacToeFragment = TicTacToeFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1), 3);
                fragmentTransaction.replace(R.id.fragment_container, ticTacToeFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else if (intent.getIntExtra("level",1) == 2) {
                TicTacToeFragment ticTacToeFragment = TicTacToeFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1), 5);
                fragmentTransaction.replace(R.id.fragment_container, ticTacToeFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else if (intent.getIntExtra("level",1) == 3) {
                TicTacToeFragment ticTacToeFragment = TicTacToeFragment.newInstance(intent.getStringExtra("title"),
                        intent.getIntExtra("type",1), 10);
                fragmentTransaction.replace(R.id.fragment_container, ticTacToeFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (intent.getIntExtra("type",1) == 12)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        else startActivity(new Intent(getApplicationContext(), LevelsActivity.class).putExtra("title", intent.getStringExtra("title"))
                .putExtra("type",intent.getIntExtra("type",1)).putExtra("back", 2));
        /*AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);

        builder.setMessage("هل تريد بالتأكيد الخروج؟ سوف تخسر تقدمك.")
                .setTitle("انتباه!")
                .setIcon(R.drawable.ic_warning);

        builder.setPositiveButton("خروج", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(getApplicationContext(), LevelsActivity.class).putExtra("title", intent.getStringExtra("title"))
                        .putExtra("type",intent.getIntExtra("type",1))
                        .putExtra("isQuiz", intent.getBooleanExtra("isQuiz",false)));
            }
        });

        builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();*/
    }
}