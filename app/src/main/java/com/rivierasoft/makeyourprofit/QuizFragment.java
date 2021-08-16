package com.rivierasoft.makeyourprofit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {

    DateFormat dateFormat;

    TextView textView, questionNoTextView, titleTextView, levelTextView, helpTextView;
    Button button1, button2, button3, button4;
    LinearLayout linearLayout;
    CardView cardView;

    private String userAnswer, unitAds1Id, unitAds2Id, unitAds3Id, today;
    private int questionNo = 1, points = 0, correctAnswers = 0, wrongAnswers = 0, questionsSize, deletedChoices, p, l, po;
    private boolean isEarn = false, isAds;

    Handler handler = new Handler();

    ArrayList<Question> questions ;

    private ProgressBar progressBar;
    ProgressDialog progressDialog;

    List<Question> questionList = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("questions");
    private DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());
    private DocumentReference docPasswordRef = db.collection("passwords").document(firebaseUser.getUid());

//    private InterstitialAd mInterstitialAd;
//    private RewardedAd rewardedAd;
//    private RewardedAd rewardedAd2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String title;
    private int type;
    private int level;

    public QuizFragment() {
        // Required empty public constructor
    }

    public static QuizFragment newInstance(String title, int type, int level) {
        QuizFragment fragment = new QuizFragment();
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
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        unitAds1Id = "ca-app-pub-3940256099942544/5224354917";
//        unitAds2Id = "ca-app-pub-3940256099942544/5224354917";
//        unitAds3Id = "ca-app-pub-3940256099942544/1033173712";

//        mInterstitialAd = new InterstitialAd(getActivity());
//        mInterstitialAd.setAdUnitId(unitAds3Id);
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());

//        rewardedAd = new RewardedAd(getActivity(), unitAds1Id);
//        rewardedAd2 = new RewardedAd(getActivity(), unitAds2Id);
//
//        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
//            @Override
//            public void onRewardedAdLoaded() {
//                // Ad successfully loaded.
//            }
//
//            @Override
//            public void onRewardedAdFailedToLoad(LoadAdError adError) {
//                // Ad failed to load.
//            }
//        };
//
//        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
//        rewardedAd2.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        textView = view.findViewById(R.id.questionTextView);
        questionNoTextView = view.findViewById(R.id.questionsTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        levelTextView = view.findViewById(R.id.levelTextView);
        helpTextView = view.findViewById(R.id.helpTextView);
        button1 = view.findViewById(R.id.choice_bt1);
        button2 = view.findViewById(R.id.choice_bt2);
        button3 = view.findViewById(R.id.choice_bt3);
        button4 = view.findViewById(R.id.choice_bt4);
        linearLayout= view.findViewById(R.id.linearLayout5);
        cardView = view.findViewById(R.id.cardView2);

        linearLayout.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        button3.setVisibility(View.INVISIBLE);
        button4.setVisibility(View.INVISIBLE);
        helpTextView.setVisibility(View.INVISIBLE);

        progressBar = getActivity().findViewById(R.id.progressBar);

        titleTextView.setText(title);

        levelTextView.setText(level+"");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("جار التحميل...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");

        readData(new MyCallback() {
            @Override
            public void onCallback(List<Question> list) {
                questions = (ArrayList<Question>) list;

                //questionsSize = questions.size();
                //questionNoTextView.setText(questionsSize+"/1");
                if (level == 0) {
                    levelTextView.setText("0");
                    questionsSize = 3;
                } else questionsSize = questions.size();

                questionNoTextView.setText(questionsSize+"/1");

                Collections.shuffle(questions);

                setQuestions();

                linearLayout.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.VISIBLE);
                button4.setVisibility(View.VISIBLE);

                if (isAds)
                    helpTextView.setVisibility(View.VISIBLE);

                helpTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deletedChoices == 2)
                            Toast.makeText(getActivity(), "لا يمكن حذف أكثر من إجابتين!", Toast.LENGTH_SHORT).show();
                        else helpDialog("حذف إجابة","قم بمشاهدة إعلان واحصل على مكافأة بحذف إجابة خاطئة.", R.drawable.ic_tip);
                    }
                });

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verify(button1);
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verify(button2);
                    }
                });

                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verify(button3);
                    }
                });

                button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verify(button4);
                    }
                });
            }
        });

    }

    public void setQuestions() {
        deletedChoices = 0;
        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        button3.setVisibility(View.VISIBLE);
        button4.setVisibility(View.VISIBLE);
        Question question = questions.get(0);
        textView.setText(question.getText());
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(question.getChoice1());
        arrayList.add(question.getChoice2());
        arrayList.add(question.getChoice3());
        arrayList.add(question.getChoice4());
        Collections.shuffle(arrayList);
        button1.setText(arrayList.get(0));
        button2.setText(arrayList.get(1));
        button3.setText(arrayList.get(2));
        button4.setText(arrayList.get(3));
    }

    public String getAnswer() {
        Question question = questions.get(0);
        return question.getChoice1();
    }

    public void verify(final Button button) {
        userAnswer = button.getText().toString();
        button.setBackground(getResources().getDrawable(R.drawable.orange));
        disableButtons();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userAnswer.equals(getAnswer())) {
                    button.setBackground(getResources().getDrawable(R.drawable.correct));
                    if (questionNo%questionsSize == 0)
                        points+=1;
                    //correctAnswers+=1;
                    //pointsTextView.setText(points+"");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (questionNo < questionsSize) {
                                questions.remove(0);
                                setQuestions();
                                enableButtons();
                                button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                questionNo+=1;
                                questionNoTextView.setText(questionsSize+"/"+questionNo);
                                /*if (questionNo == 1) {
                                    questions.remove(0);
                                    setQuestions();
                                    enableButtons();
                                    button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                    questionNo+=1;
                                    questionNoTextView.setText(questionsSize+"/"+questionNo);
                                } else if (questionNo == 2) {
                                    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                                        @Override
                                        public void onRewardedAdLoaded() {
                                            // Ad successfully loaded.
                                            if (rewardedAd.isLoaded()) {
                                                Activity activityContext = getActivity();
                                                RewardedAdCallback adCallback = new RewardedAdCallback() {
                                                    @Override
                                                    public void onRewardedAdOpened() {
                                                        // Ad opened.
                                                    }

                                                    @Override
                                                    public void onRewardedAdClosed() {
                                                        // Ad closed.
                                                        rewardedAd = createAndLoadRewardedAd();
                                                        questions.remove(0);
                                                        setQuestions();
                                                        enableButtons();
                                                        button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                                        questionNo+=1;
                                                        questionNoTextView.setText(questionsSize+"/"+questionNo);
                                                    }

                                                    @Override
                                                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                                                        // User earned reward.
                                                    }

                                                    @Override
                                                    public void onRewardedAdFailedToShow(AdError adError) {
                                                        // Ad failed to display.
                                                    }
                                                };
                                                rewardedAd.show(activityContext, adCallback);
                                            } else {
                                                //Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                                                Toast.makeText(getActivity(), "يرجى الانتظار قليلاً", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onRewardedAdFailedToLoad(LoadAdError adError) {
                                            // Ad failed to load.
                                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                                        }
                                    };
                                    rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                                } else if (questionNo == 3) {
                                    questions.remove(0);
                                    setQuestions();
                                    enableButtons();
                                    button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                    questionNo+=1;
                                    questionNoTextView.setText(questionsSize+"/"+questionNo);
                                } else if (questionNo == 4) {
                                    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                                        @Override
                                        public void onRewardedAdLoaded() {
                                            // Ad successfully loaded.
                                            if (rewardedAd2.isLoaded()) {
                                                Activity activityContext = getActivity();
                                                RewardedAdCallback adCallback = new RewardedAdCallback() {
                                                    @Override
                                                    public void onRewardedAdOpened() {
                                                        // Ad opened.
                                                    }

                                                    @Override
                                                    public void onRewardedAdClosed() {
                                                        // Ad closed.
                                                        rewardedAd2 = createAndLoadRewardedAd();
                                                        questions.remove(0);
                                                        setQuestions();
                                                        enableButtons();
                                                        button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                                        questionNo+=1;
                                                        questionNoTextView.setText(questionsSize+"/"+questionNo);
                                                    }

                                                    @Override
                                                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                                                        // User earned reward.
                                                    }

                                                    @Override
                                                    public void onRewardedAdFailedToShow(AdError adError) {
                                                        // Ad failed to display.
                                                    }
                                                };
                                                rewardedAd2.show(activityContext, adCallback);
                                            } else {
                                                //Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                                                Toast.makeText(getActivity(), "يرجى الانتظار قليلاً", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onRewardedAdFailedToLoad(LoadAdError adError) {
                                            // Ad failed to load.
                                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                                        }
                                    };
                                    rewardedAd2.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                                } else if (questionNo == 5) {
                                    questions.remove(0);
                                    setQuestions();
                                    enableButtons();
                                    button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                    questionNo+=1;
                                    questionNoTextView.setText(questionsSize+"/"+questionNo);
                                } else if (questionNo == 6) {
                                    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                                        @Override
                                        public void onRewardedAdLoaded() {
                                            // Ad successfully loaded.
                                            if (rewardedAd3.isLoaded()) {
                                                Activity activityContext = getActivity();
                                                RewardedAdCallback adCallback = new RewardedAdCallback() {
                                                    @Override
                                                    public void onRewardedAdOpened() {
                                                        // Ad opened.
                                                    }

                                                    @Override
                                                    public void onRewardedAdClosed() {
                                                        // Ad closed.
                                                        rewardedAd3 = createAndLoadRewardedAd();
                                                        questions.remove(0);
                                                        setQuestions();
                                                        enableButtons();
                                                        button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                                        questionNo+=1;
                                                        questionNoTextView.setText(questionsSize+"/"+questionNo);
                                                    }

                                                    @Override
                                                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                                                        // User earned reward.
                                                    }

                                                    @Override
                                                    public void onRewardedAdFailedToShow(AdError adError) {
                                                        // Ad failed to display.
                                                    }
                                                };
                                                rewardedAd3.show(activityContext, adCallback);
                                            } else {
                                                //Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                                                Toast.makeText(getActivity(), "يرجى الانتظار قليلاً", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onRewardedAdFailedToLoad(LoadAdError adError) {
                                            // Ad failed to load.
                                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                                        }
                                    };
                                    rewardedAd3.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                                } else if (questionNo == 7) {
                                    questions.remove(0);
                                    setQuestions();
                                    enableButtons();
                                    button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                    questionNo+=1;
                                    questionNoTextView.setText(questionsSize+"/"+questionNo);
                                } else if (questionNo == 8) {
                                    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                                        @Override
                                        public void onRewardedAdLoaded() {
                                            // Ad successfully loaded.
                                            if (rewardedAd4.isLoaded()) {
                                                Activity activityContext = getActivity();
                                                RewardedAdCallback adCallback = new RewardedAdCallback() {
                                                    @Override
                                                    public void onRewardedAdOpened() {
                                                        // Ad opened.
                                                    }

                                                    @Override
                                                    public void onRewardedAdClosed() {
                                                        // Ad closed.
                                                        rewardedAd4 = createAndLoadRewardedAd();
                                                        questions.remove(0);
                                                        setQuestions();
                                                        enableButtons();
                                                        button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                                        questionNo+=1;
                                                        questionNoTextView.setText(questionsSize+"/"+questionNo);
                                                    }

                                                    @Override
                                                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                                                        // User earned reward.
                                                    }

                                                    @Override
                                                    public void onRewardedAdFailedToShow(AdError adError) {
                                                        // Ad failed to display.
                                                    }
                                                };
                                                rewardedAd4.show(activityContext, adCallback);
                                            } else {
                                                //Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                                                Toast.makeText(getActivity(), "يرجى الانتظار قليلاً", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onRewardedAdFailedToLoad(LoadAdError adError) {
                                            // Ad failed to load.
                                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                                        }
                                    };
                                    rewardedAd4.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                                }*/
                            } else {
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
                                                        docRef.update("points",(points+p)+"")
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        docRef.update("p",points+p)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        if (dateFormat.format(Calendar.getInstance().getTime()).equals(today)) {
                                                                                            docRef.update("today_points",(po+points)+"/"+dateFormat.format(Calendar.getInstance().getTime()))
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            if (level == 0) {
                                                                                                                progressDialog.cancel();
                                                                                                                showWonDialog();
                                                                                                            } else {
                                                                                                                if (level == l) {
                                                                                                                    docRef.update("t"+type,(level+1)+"")
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
                                                                                                                    progressDialog.cancel();
                                                                                                                    showWonDialog();
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                        } else {
                                                                                            docRef.update("today_points",points+"/"+dateFormat.format(Calendar.getInstance().getTime()))
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            if (level == 0) {
                                                                                                                progressDialog.cancel();
                                                                                                                showWonDialog();
                                                                                                            } else {
                                                                                                                if (level == l) {
                                                                                                                    docRef.update("t"+type,(level+1)+"")
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
                                                                                                                    progressDialog.cancel();
                                                                                                                    showWonDialog();
                                                                                                                }
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

                                /*progressDialog.show();

                                if (Connectivity.isConnected(getActivity()).equals("yes"))
                                    thread.start();
                                else {
                                    progressDialog.cancel();
                                    setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                                }*/

                                if (AdsCenter.mInterstitialAd.isLoaded()) {
                                    AdsCenter.mInterstitialAd.show();
                                    docRef.update("points",(points+p)+"")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    docRef.update("p",points+p)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    if (dateFormat.format(Calendar.getInstance().getTime()).equals(today)) {
                                                                        docRef.update("today_points",(po+points)+"/"+dateFormat.format(Calendar.getInstance().getTime()))
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        if (level == 0) {
//                                                                                            progressDialog.cancel();
//                                                                                            showWonDialog();
                                                                                        } else {
                                                                                            if (level == l) {
                                                                                                docRef.update("t"+type,(level+1)+"")
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
//                                                                                                                progressDialog.cancel();
//                                                                                                                showWonDialog();
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                            } else {
//                                                                                                progressDialog.cancel();
//                                                                                                showWonDialog();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        docRef.update("today_points",points+"/"+dateFormat.format(Calendar.getInstance().getTime()))
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        if (level == 0) {
//                                                                                            progressDialog.cancel();
//                                                                                            showWonDialog();
                                                                                        } else {
                                                                                            if (level == l) {
                                                                                                docRef.update("t"+type,(level+1)+"")
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
//                                                                                                                progressDialog.cancel();
//                                                                                                                showWonDialog();
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                            } else {
//                                                                                                progressDialog.cancel();
//                                                                                                showWonDialog();
                                                                                            }
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
                                        //AdsCenter.loadInterstitialAd(getActivity());
                                        showWonDialog();
                                    }
                                });
                            }
                        }
                    }, 500);
                }
                else {
                    button.setBackground(getResources().getDrawable(R.drawable.wrong));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //points = 0;
                            //pointsTextView.setText(points+"");
                            //wrongAnswers+=1;
                            /*FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            LostFragment lostFragment = LostFragment.newInstance(title, type, false);
                            fragmentTransaction.replace(R.id.fragment_container, lostFragment);
                            //fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();*/
                            showLostDialog(button);
                            /*if (points > 0) {
                                points-=1;
                                pointsTextView.setText(points+"");
                                if (questionNo < 3) {
                                    questions.remove(0);
                                    setQuestions();
                                    enableButtons();
                                    button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                    questionNo+=1;
                                    wrongAnswers+=1;
                                    questionNoTextView.setText("3/"+questionNo);
                                } else {
                                    //startActivity(new Intent(getActivity(),LevelsActivity.class));
                                    Toast.makeText(getActivity(), "لقد أنهيت المستوى وبلغ عدد نقاطك: "+ points, Toast.LENGTH_LONG).show();
                                }
                            } else //startActivity(new Intent(getActivity(),LevelsActivity.class));
                                Toast.makeText(getActivity(), "لقد خسرت وبقي عدد نقاطك كما هو: "+ points, Toast.LENGTH_LONG).show();*/
                        }
                    }, 500);
                }
            }
        }, 1000);
    }

    public void enableButtons() {
        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        button4.setEnabled(true);
    }

    public void disableButtons() {
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
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
                            if (button4.getVisibility() == View.VISIBLE && !getAnswer().equals(button4.getText().toString()))
                                button4.setVisibility(View.INVISIBLE);
                            else if (button3.getVisibility() == View.VISIBLE && !getAnswer().equals(button3.getText().toString()))
                                button3.setVisibility(View.INVISIBLE);
                            else if (button2.getVisibility() == View.VISIBLE && !getAnswer().equals(button2.getText().toString()))
                                button2.setVisibility(View.INVISIBLE);
                            else if (button1.getVisibility() == View.VISIBLE && !getAnswer().equals(button1.getText().toString()))
                                button1.setVisibility(View.INVISIBLE);
                            deletedChoices++;
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

        Button okButton = dialog.findViewById(R.id.ok_bt);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LevelsActivity.class).putExtra("title",title)
                        .putExtra("type", type).putExtra("back", 2));
            }
        });
        dialog.show();
    }

    public void showLostDialog(final Button button) {
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

        TextView exitTextView = dialog.findViewById(R.id.exitTextView);
        Button tryButton = dialog.findViewById(R.id.try_bt);

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
                            button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                            enableButtons();
                            dialog.cancel();
                            //rewardedAd = createAndLoadRewardedAd();
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

    public void checkInternetConnection() {
        mAuth.signInWithEmailAndPassword("ahmedmohd8822@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    docPasswordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    //Toast.makeText(getApplicationContext(), documentSnapshot.getString("email"), Toast.LENGTH_SHORT).show();
                                    mAuth.signInWithEmailAndPassword(documentSnapshot.getString("email"), documentSnapshot.getString("password")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                //here...

                                            } else {

                                            }
                                        }

                                    });
                                }

                                else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                            } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //FirebaseAuth.getInstance().signOut();
                } else {
                    setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                }
            }
        });
    }

    public void readData(final MyCallback myCallback) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        p = Integer.parseInt(documentSnapshot.getString("points"));
                        l = Integer.parseInt(documentSnapshot.getString("t"+type));
                        isAds = documentSnapshot.getBoolean("ads");
                        String tp = documentSnapshot.getString("today_points");
                        String[] strings = tp.split("/");
                        po = Integer.parseInt(strings[0]);
                        today = strings[1];
                    }
                    else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        if (level == 0) {
            notebookRef.whereEqualTo("type",type+"")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String text = document.getString("text");
                            String choice1 = document.getString("choice1");
                            String choice2 = document.getString("choice2");
                            String choice3 = document.getString("choice3");
                            String choice4 = document.getString("choice4");
                            questionList.add(new Question(text, choice1, choice2, choice3, choice4));
                        }
                        myCallback.onCallback(questionList);
                    } else {
                        Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            notebookRef.whereEqualTo("tl",type+""+level)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String text = document.getString("text");
                            String choice1 = document.getString("choice1");
                            String choice2 = document.getString("choice2");
                            String choice3 = document.getString("choice3");
                            String choice4 = document.getString("choice4");
                            questionList.add(new Question(text, choice1, choice2, choice3, choice4));
                        }
                        myCallback.onCallback(questionList);
                    } else {
                        Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public interface MyCallback {
        void onCallback(List<Question> list);
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