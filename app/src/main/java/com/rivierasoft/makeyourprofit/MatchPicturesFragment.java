package com.rivierasoft.makeyourprofit;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchPicturesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchPicturesFragment extends Fragment {

    MyClass myClass = new MyClass();

    TableLayout tableLayout;
    LinearLayout linearLayout;
    ImageView img1,img2,img3,img4,img5,img6,img7,img8,img9,img10,img11,img12;
    TextView scoreTextView, attemptsTextView, titleTextView, helpTextView;
    int counter = 0 , score = 0 , attempts = 6, d_image = R.drawable.dflower, points = 0, p, po;
    boolean isEarn = false, isAds;
    String unitAds1Id, unitAds2Id, unitAds3Id, today;

    DateFormat dateFormat;
    ProgressDialog progressDialog;
    private ProgressBar progressBar;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String title;
    private int type;
    private int level;

    public MatchPicturesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MatchPicturesFragment newInstance(String title, int type, int level) {
        MatchPicturesFragment fragment = new MatchPicturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putInt(ARG_PARAM2, type);
        args.putInt(ARG_PARAM3, level);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            type = getArguments().getInt(ARG_PARAM2);
            level = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_pictures, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("جار التحميل...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");

        img1 = view.findViewById(R.id.Image1);
        img2 = view.findViewById(R.id.Image2);
        img3 = view.findViewById(R.id.Image3);
        img4 = view.findViewById(R.id.Image4);
        img5 = view.findViewById(R.id.Image5);
        img6 = view.findViewById(R.id.Image6);
        img7 = view.findViewById(R.id.Image7);
        img8 = view.findViewById(R.id.Image8);
        img9 = view.findViewById(R.id.Image9);
        img10 = view.findViewById(R.id.Image10);
        img11 = view.findViewById(R.id.Image11);
        img12 = view.findViewById(R.id.Image12);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        attemptsTextView = view.findViewById(R.id.attemptsTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        helpTextView = view.findViewById(R.id.helpTextView);
        linearLayout = view.findViewById(R.id.linearLayout5);
        tableLayout = view.findViewById(R.id.tableLayout);
        progressBar = getActivity().findViewById(R.id.progressBar);

        linearLayout.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.INVISIBLE);
        helpTextView.setVisibility(View.INVISIBLE);

        readData(new MyCallback() {
            @Override
            public void onCallback(int pp, int popo, String t) {

                p = pp; po = popo; today = t;

                linearLayout.setVisibility(View.VISIBLE);
                tableLayout.setVisibility(View.VISIBLE);
                if (isAds)
                    helpTextView.setVisibility(View.VISIBLE);

                titleTextView.setText(title);
                attemptsTextView.setText("المحاولات: "+attempts);

                final ArrayList<Integer> arrayList = new ArrayList<Integer>(16);

                arrayList.add(R.drawable.flowerb);
                arrayList.add(R.drawable.flowerb);
                arrayList.add(R.drawable.flowerg);
                arrayList.add(R.drawable.flowerg);
                arrayList.add(R.drawable.flowerp);
                arrayList.add(R.drawable.flowerp);
                arrayList.add(R.drawable.flowero);
                arrayList.add(R.drawable.flowero);
                arrayList.add(R.drawable.flowerr);
                arrayList.add(R.drawable.flowerr);
                arrayList.add(R.drawable.flowery);
                arrayList.add(R.drawable.flowery);

                Collections.shuffle(arrayList);

                final int r1 = arrayList.get(0);
                final int r2 = arrayList.get(1);
                final int r3 = arrayList.get(2);
                final int r4 = arrayList.get(3);
                final int r5 = arrayList.get(4);
                final int r6 = arrayList.get(5);
                final int r7 = arrayList.get(6);
                final int r8 = arrayList.get(7);
                final int r9 = arrayList.get(8);
                final int r10 = arrayList.get(9);
                final int r11 = arrayList.get(10);
                final int r12 = arrayList.get(11);

                img1.setImageResource(r1);
                img2.setImageResource(r2);
                img3.setImageResource(r3);
                img4.setImageResource(r4);
                img5.setImageResource(r5);
                img6.setImageResource(r6);
                img7.setImageResource(r7);
                img8.setImageResource(r8);
                img9.setImageResource(r9);
                img10.setImageResource(r10);
                img11.setImageResource(r11);
                img12.setImageResource(r12);

        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 5000ms
                setD_image();
            }
        }, 5000);*/
                setD_image();

                helpTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (attempts == 6)
                            Toast.makeText(getActivity(), "لا يمكن إضافة عدد أكبر من المحاولات!", Toast.LENGTH_SHORT).show();
                        else helpDialog("إضافة محاولة","قم بمشاهدة إعلان واحصل على مكافأة بإضافة محاولة.", R.drawable.ic_tip);
                    }
                });

                img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img1.setImageResource(r1);
                            counter++;
                            verify(r1,img1); }
                    }
                });

                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img2.setImageResource(r2);
                            counter++;
                            verify(r2,img2); }
                    }
                });

                img3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img3.setImageResource(r3);
                            counter++;
                            verify(r3,img3); }
                    }
                });

                img4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img4.setImageResource(r4);
                            counter++;
                            verify(r4,img4); }
                    }
                });

                img5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img5.setImageResource(r5);
                            counter++;
                            verify(r5,img5); }
                    }
                });

                img6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img6.setImageResource(r6);
                            counter++;
                            verify(r6,img6); }
                    }
                });

                img7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img7.setImageResource(r7);
                            counter++;
                            verify(r7,img7); }
                    }
                });

                img8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img8.setImageResource(r8);
                            counter++;
                            verify(r8,img8); }
                    }
                });

                img9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img9.setImageResource(r9);
                            counter++;
                            verify(r9,img9); }
                    }
                });

                img10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img10.setImageResource(r10);
                            counter++;
                            verify(r10,img10); }
                    }
                });

                img11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img11.setImageResource(r11);
                            counter++;
                            verify(r11,img11); }
                    }
                });

                img12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (counter<2) {
                            img12.setImageResource(r12);
                            counter++;
                            verify(r12,img12); }
                    }
                });
            }
        });

    }

    public void verify(int a , ImageView imageView) {

        if (counter==1) {
            myClass.setA1(a);
            myClass.setImageView1(imageView);
            imageView.setEnabled(false);
        }

        if (counter==2) {
            int a1 = myClass.getA1();
            final ImageView imageView1 = myClass.getImageView1();
            int a2 =a;
            final ImageView imageView2 = imageView;

            if (a1 == a2) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        score ++;
                        counter = 0;
                        scoreTextView.setText("الدرجة: "+score);
                        imageView2.setEnabled(false);

                        if (score == 6) {
                            points++;
                            final Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final String finalRes =  Connectivity.executeCmd();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (finalRes.equals("no")) {
                                                progressDialog.cancel();
                                                setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                                            } else {
                                                if (isAds) {
                                                    progressDialog.cancel();
                                                    setDialog("خطأ!", "الإنترنت ضعيف أو حدث خطأ ما يرجى إعادة المحاولة لاحقاً.", R.drawable.ic_warning);
                                                } else {
                                                    docRef.update("points",(1+p)+"")
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    docRef.update("p",1+p)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    if (dateFormat.format(Calendar.getInstance().getTime()).equals(today)) {
                                                                                        docRef.update("today_points",(po+1)+"/"+dateFormat.format(Calendar.getInstance().getTime()))
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                    progressDialog.cancel();
                                                                                                    showWonDialog();
                                                                                                    }
                                                                                                })
                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                    @Override
                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                        Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });
                                                                                    } else {
                                                                                        docRef.update("today_points",1+"/"+dateFormat.format(Calendar.getInstance().getTime()))
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                    progressDialog.cancel();
                                                                                                    showWonDialog();
                                                                                                    }
                                                                                                })
                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                    @Override
                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                        Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });
                                }
                            });

                            if (AdsCenter.mInterstitialAd.isLoaded()) {
                                AdsCenter.mInterstitialAd.show();
                                                docRef.update("points",(1+p)+"")
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                docRef.update("p",1+p)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                if (dateFormat.format(Calendar.getInstance().getTime()).equals(today)) {
                                                                                    docRef.update("today_points",(po+1)+"/"+dateFormat.format(Calendar.getInstance().getTime()))
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
//                                                                                                    progressDialog.cancel();
//                                                                                                    showWonDialog();
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            });
                                                                                } else {
                                                                                    docRef.update("today_points",1+"/"+dateFormat.format(Calendar.getInstance().getTime()))
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
//                                                                                                    progressDialog.cancel();
//                                                                                                    showWonDialog();
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                            } else {
                                progressDialog.show();

                                if (Connectivity.isConnected(getActivity()).equals("yes"))
                                    thread.start();
                                else {
                                    progressDialog.cancel();
                                    setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                                }
                                //Toast.makeText(getActivity(), "جار تحميل الإعلان... يرجى الانتظار قليلاً", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(), "The interstitial wasn't loaded yet.", Toast.LENGTH_SHORT).show();
                                //Log.d("TAG", "The interstitial wasn't loaded yet.");
                            }

                            AdsCenter.mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    //Toast.makeText(getActivity(), "ad finishes loading", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onAdFailedToLoad(LoadAdError adError) {
                                    // Code to be executed when an ad request fails.
                                    //View parentLayout = findViewById(android.R.id.content);
                                    //Snackbar.make(parentLayout, "أنت غير متصل بالإنترنت!", Snackbar.LENGTH_SHORT).show();
                                    //Toast.makeText(getApplicationContext(), "ad request fails", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when the ad is displayed.
                                    //Toast.makeText(getActivity(), "ad is displayed", Toast.LENGTH_SHORT).show();
                                    AdsCenter.loadInterstitialAd(getActivity());
                                }

                                @Override
                                public void onAdClicked() {
                                    // Code to be executed when the user clicks on an ad.
                                    //Toast.makeText(getActivity(), "user clicks on an ad", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onAdLeftApplication() {
                                    // Code to be executed when the user has left the app.
                                    //Toast.makeText(getActivity(), "user has left the app", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when the interstitial ad is closed.
                                    //Toast.makeText(getApplicationContext(), "interstitial ad is closed", Toast.LENGTH_SHORT).show();
                                    showWonDialog();
                                }
                            });
                        }
                    }
                }, 1000);
            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        attempts--;
                        imageView1.setImageResource(d_image);
                        imageView2.setImageResource(d_image);
                        counter = 0;
                        attemptsTextView.setText("المحاولات: "+attempts);
                        imageView1.setEnabled(true);
                        imageView2.setEnabled(true);

                        if (attempts == 0) {
                            showLostDialog();
                        }
                    }
                }, 1000);
            }
        }
    }

    public void setD_image() {
        img1.setImageResource(d_image);
        img2.setImageResource(d_image);
        img3.setImageResource(d_image);
        img4.setImageResource(d_image);
        img5.setImageResource(d_image);
        img6.setImageResource(d_image);
        img7.setImageResource(d_image);
        img8.setImageResource(d_image);
        img9.setImageResource(d_image);
        img10.setImageResource(d_image);
        img11.setImageResource(d_image);
        img12.setImageResource(d_image);
    }

    public void helpDialog (String titl, String message, int icon) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String finalRes =  Connectivity.executeCmd();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalRes.equals("no")) {
                            progressDialog.cancel();
                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                        } else {
                            progressDialog.cancel();
                            //Toast.makeText(getActivity(), "جار تحميل الإعلان... اضغط مرة أخرى للعرض", Toast.LENGTH_SHORT).show();
                            //rewardedAd = createAndLoadRewardedAd(unitAds1Id);
                            setDialog("خطأ!", "الإنترنت ضعيف أو حدث خطأ ما يرجى إعادة المحاولة لاحقاً.", R.drawable.ic_warning);
                        }
                    }
                });
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(titl)
                .setIcon(icon);

        builder.setPositiveButton("مشاهدة إعلان", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                if (AdsCenter.rewardedAd.isLoaded()) {
                    //r = r+1;
                    Activity activityContext = getActivity();
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                            //Toast.makeText(getActivity(), "Ad opened.", Toast.LENGTH_SHORT).show();
                            AdsCenter.loadRewardedAd(getActivity());
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            if (!isEarn) {
                                Toast.makeText(getActivity(), "قم بمشاهدة الإعلان كاملاً لتحصل على مكافأتك.", Toast.LENGTH_SHORT).show();
                                //rewardedAd = createAndLoadRewardedAd(unitAds1Id);
                            }
                        }
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            //Toast.makeText(getActivity(), "تهانينا! لقد حصلت على نقطة جديدة", Toast.LENGTH_SHORT).show();
                            isEarn = true;
                            attempts++;
                            attemptsTextView.setText("المحاولات: "+attempts);
                            dialog.cancel();
                            //rewardedAd = createAndLoadRewardedAd();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            // Ad failed to display.
                            Toast.makeText(getActivity(), "Ad failed to display.", Toast.LENGTH_SHORT).show();
                        }
                    };
                    AdsCenter.rewardedAd.show(activityContext, adCallback);
                } else {
                    progressDialog.show();

                    if (Connectivity.isConnected(getActivity()).equals("yes"))
                        thread.start();
                    else {
                        progressDialog.cancel();
                        setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                    }
                }
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
        dialog.show();
    }


    public void showWonDialog(){
        if (p == 999)
            Notification.displayNotification(getActivity());

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.won);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView stateTextView = dialog.findViewById(R.id.stateTextView);
        Button okButton = dialog.findViewById(R.id.ok_bt);

        stateTextView.setText("لقد أكملت اللعبة بنجاح");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LevelsActivity.class).putExtra("title",title)
                        .putExtra("type", type).putExtra("back", 2));
            }
        });
        dialog.show();
    }

    public void showLostDialog() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String finalRes =  Connectivity.executeCmd();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalRes.equals("no")) {
                            progressDialog.cancel();
                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                        } else {
                            progressDialog.cancel();
                            //Toast.makeText(getActivity(), "جار تحميل الإعلان... اضغط مرة أخرى للعرض", Toast.LENGTH_SHORT).show();
                            //rewardedAd2 = createAndLoadRewardedAd(unitAds2Id);
                            if (isAds)
                                setDialog("خطأ!", "الإنترنت ضعيف أو حدث خطأ ما يرجى إعادة المحاولة لاحقاً.", R.drawable.ic_warning);
                            else setDialog("خطأ!", "لا يتوفر إعلان حالياً يرجى إعادة المحاولة لاحقاً.", R.drawable.ic_warning);
                        }
                    }
                });
            }
        });

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.lost);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView stateTextView = dialog.findViewById(R.id.stateTextView);
        TextView exitTextView = dialog.findViewById(R.id.exitTextView);
        Button tryButton = dialog.findViewById(R.id.try_bt);

        stateTextView.setText("لديك فرصة لإكمال اللعية");

        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AdsCenter.rewardedAd2.isLoaded()) {
                    //r = r+1;
                    Activity activityContext = getActivity();
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                            //Toast.makeText(getActivity(), "Ad opened.", Toast.LENGTH_SHORT).show();
                            AdsCenter.loadRewardedAd2(getActivity());
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            if (!isEarn) {
                                Toast.makeText(getActivity(), "قم بمشاهدة الإعلان كاملاً لتحصل على مكافأتك.", Toast.LENGTH_SHORT).show();
                                //rewardedAd2 = createAndLoadRewardedAd(unitAds2Id);
                            }
                        }
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            //Toast.makeText(getActivity(), "تهانينا! لقد حصلت على نقطة جديدة", Toast.LENGTH_SHORT).show();
                            isEarn = true;
                            if (score <= 2)
                                attempts = 3;
                            else if (score <= 4)
                                attempts = 2;
                            else attempts = 1;
                            attemptsTextView.setText("المحاولات: "+attempts);
                            dialog.cancel();
                            /*if (attempts < 3)
                                Toast.makeText(getActivity(), "تهانينا لقد حصلت على "+ attempts +" محاولة جديدة", Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getActivity(), "تهانينا لقد حصلت على "+ attempts +" محاولات جديدة", Toast.LENGTH_SHORT).show();*/
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            // Ad failed to display.
                            Toast.makeText(getActivity(), "Ad failed to display.", Toast.LENGTH_SHORT).show();
                        }
                    };
                    AdsCenter.rewardedAd2.show(activityContext, adCallback);
                } else {
                    progressDialog.show();

                    if (Connectivity.isConnected(getActivity()).equals("yes"))
                        thread.start();
                    else {
                        progressDialog.cancel();
                        setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                    }
                }
            }
        });

        exitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                startActivity(new Intent(getActivity(), LevelsActivity.class).putExtra("title",title)
                        .putExtra("type", type).putExtra("back", 2));
            }
        });
        dialog.show();
    }

    public void setDialog (String titl, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(titl)
                .setIcon(icon);

        builder.setPositiveButton("حسناً", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void readData(final MyCallback myCallback) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        p = Integer.parseInt(documentSnapshot.getString("points"));
                        String tp = documentSnapshot.getString("today_points");
                        isAds = documentSnapshot.getBoolean("ads");
                        String[] strings = tp.split("/");
                        po = Integer.parseInt(strings[0]);
                        today = strings[1];
                    } else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                    myCallback.onCallback(p, po, today);
                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface MyCallback {
        void onCallback(int p, int po, String today);
    }

    public RewardedAd createAndLoadRewardedAd(String unitAdsId) {
        RewardedAd rewardedAd = new RewardedAd(getActivity(),
                unitAdsId);
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }
}