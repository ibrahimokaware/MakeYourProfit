package com.rivierasoft.makeyourprofit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    public static final String CHANNEL_ID = "withdraw_notification_channel";

    TextView pointsTextView;
    String points, today_points, name, dob, gender, phone, address;
    boolean notification;
    private View badgeView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdsCenter.loadInterstitialAd(getApplicationContext());
        AdsCenter.loadRewardedAd(getApplicationContext());
        AdsCenter.loadRewardedAd2(getApplicationContext());

        pointsTextView = findViewById(R.id.pointsTextView);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        addBadgeView();

        pointsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddQuestionActivity.class));
                //displayNotification();
                //Notification.displayNotification(getApplicationContext());
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //MenuItem item = bottomNavigationView.getMenu().findItem(R.id.page_4);
        //item.setChecked(true);

        //bottomNavigationView.getMenu().getItem(3).setChecked(true);

        //View parentLayout = findViewById(android.R.id.content);
        //Snackbar.make(parentLayout, "أنت غير متصل بالإنترنت!", Snackbar.LENGTH_SHORT).show();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        pointsTextView.setText(documentSnapshot.getString("points"));
                        points = documentSnapshot.getString("points");
                        today_points = documentSnapshot.getString("today_points");
                        name = documentSnapshot.getString("name");
                        dob = documentSnapshot.getString("date_of_birth");
                        gender = documentSnapshot.getString("gender");
                        phone = documentSnapshot.getString("phone");
                        address = documentSnapshot.getString("address");
                        notification = documentSnapshot.getBoolean("notification");
                        if (notification)
                            badgeView.setVisibility(View.VISIBLE);
                        else hideBadgeView();
                    }
                    else Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void addBadgeView() {
        try {
            //Toast.makeText(getApplicationContext(), "rorre", Toast.LENGTH_SHORT).show();
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(3);
            badgeView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.badge_view, menuView, false);
            itemView.addView(badgeView);
            badgeView.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideBadgeView() {
        try {
            boolean isBadgeVisible = badgeView.getVisibility() == View.VISIBLE;
            if (isBadgeVisible)
                badgeView.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshBadgeView() {
        try {
            boolean isBadgeVisible = badgeView.getVisibility() != View.GONE;
            badgeView.setVisibility(isBadgeVisible ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel( CHANNEL_ID, "دخول السحب",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel Description .......");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Get the layouts to use in the custom notification
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_small);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.app_logo2)
                .setContentTitle("تهانينا...")
                .setLargeIcon(drawableToBitmap(getResources().getDrawable(R.drawable.app_logo)))
                //.setContentText("تهانينا! لقد دخلت السحب بنجاح\nانقر للاطلاع على التفاصيل")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                //.setCustomContentView(notificationLayout)
                //.setCustomBigContentView(notificationLayoutExpanded);
                .setStyle(new NotificationCompat.BigTextStyle().bigText("لقد دخلت السحب بنجاح"))
                .addAction(R.drawable.ic_baseline_card_giftcard_24, "التفاصيل", pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(1, builder.build());
    }

    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.page_1:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.page_2:
                    selectedFragment = new PointsFragment();
                    break;
                case R.id.page_3:
                    selectedFragment = new RankingFragment();
                    break;
                case R.id.page_4:
                    selectedFragment = SettingsFragment.newInstance(name, dob, gender, phone, address, notification);
                    break;
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (bottomNavigationView.getSelectedItemId() != R.id.page_1) {
            bottomNavigationView.setSelectedItemId(R.id.page_1);
        } else finishAffinity();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onStart() {
        super.onStart();
        Resources res = getResources();
        Configuration newConfig = new Configuration( res.getConfiguration() );
        Locale locale = new Locale( "ar" );
        newConfig.locale = locale;
        newConfig.setLocale(locale);
        newConfig.setLayoutDirection( locale );
        res.updateConfiguration( newConfig, null);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}