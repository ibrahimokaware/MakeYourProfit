package com.rivierasoft.makeyourprofit;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DifferenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DifferenceFragment extends Fragment {

    ImageView imageView, fullscreenImageView;
    Button button1, button2, button3, button4;
    LinearLayout titleLinearLayout, choicesLinearLayout, solutionLinearLayout;
    TextView questionTextView, questionNoTextView, titleTextView, levelTextView, helpTextView,
    show, next;

    String userAnswer, unitAds1Id, unitAds2Id, unitAds3Id, today;
    int questionNo = 1, points = 0, correctAnswers = 0, wrongAnswers = 0, questionsSize, deletedChoices, p, l, po;
    boolean isEarn = false, isSolution, isAds;

    Handler handler = new Handler();
    DateFormat dateFormat;

    ArrayList<Difference> questions ;

    private ProgressBar progressBar;
    ProgressDialog progressDialog;

    List<Difference> questionList = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("questions");
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

    public DifferenceFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DifferenceFragment newInstance(String title, int type, int level) {
        DifferenceFragment fragment = new DifferenceFragment();
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
        return inflater.inflate(R.layout.fragment_difference, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        imageView = view.findViewById(R.id.imageView);
        fullscreenImageView = view.findViewById(R.id.fullscreen_image);
        button1 = view.findViewById(R.id.choice_bt1);
        button2 = view.findViewById(R.id.choice_bt2);
        button3 = view.findViewById(R.id.choice_bt3);
        button4 = view.findViewById(R.id.choice_bt4);
        titleLinearLayout = view.findViewById(R.id.linearLayout5);
        choicesLinearLayout = view.findViewById(R.id.choicesLinearLayout);
        solutionLinearLayout = view.findViewById(R.id.solutionLinearLayout);
        questionTextView = view.findViewById(R.id.textView36);
        questionNoTextView = view.findViewById(R.id.questionsTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        levelTextView = view.findViewById(R.id.levelTextView);
        helpTextView = view.findViewById(R.id.helpTextView);
        show = view.findViewById(R.id.showSolution);
        next = view.findViewById(R.id.next);

        titleLinearLayout.setVisibility(View.INVISIBLE);
        choicesLinearLayout.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        fullscreenImageView.setVisibility(View.INVISIBLE);
        questionTextView.setVisibility(View.INVISIBLE);
        helpTextView.setVisibility(View.INVISIBLE);

        progressBar = getActivity().findViewById(R.id.progressBar);

        titleTextView.setText(title);

        levelTextView.setText(level+"");

//        if (level == 0)
//            levelTextView.setText("عشوائي");
//        else levelTextView.setText(level+"");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("جار التحميل...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");

        readData(new MyCallback() {
            @Override
            public void onCallback(List<Difference> list) {
                questions = (ArrayList<Difference>) list;

                if (level == 0) {
                    levelTextView.setText("0");
                    questionsSize = 3;
                } else questionsSize = questions.size();

                questionNoTextView.setText(questionsSize+"/1");

                Collections.shuffle(questions);

                setQuestions();

                titleLinearLayout.setVisibility(View.VISIBLE);
                choicesLinearLayout.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                fullscreenImageView.setVisibility(View.VISIBLE);
                questionTextView.setVisibility(View.VISIBLE);
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

                fullscreenImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Difference question = questions.get(0);
                        if (isSolution)
                            startActivity(new Intent(getActivity(), FullSizeImageActivity.class).putExtra("url", question.getImage_s()));
                        else startActivity(new Intent(getActivity(), FullSizeImageActivity.class).putExtra("url", question.getImage()));                    }
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
        if (isAds)
            helpTextView.setVisibility(View.VISIBLE);
        fullscreenImageView.setVisibility(View.VISIBLE);
        choicesLinearLayout.setVisibility(View.VISIBLE);
        solutionLinearLayout.setVisibility(View.GONE);
        //show.setEnabled(true);
        show.setText("عرض الحل");
        questionTextView.setText("كم عدد الفروقات بين الصورتين؟");
        Difference question = questions.get(0);
        Picasso.with(getActivity())
                .load(question.getImage())
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_broken_image)
                .into(imageView);
        isSolution = false;
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
        Difference question = questions.get(0);
        return question.getChoice1();
    }

    public void setImage() {
        Difference question = questions.get(0);
        Picasso.with(getActivity())
                .load(question.getImage())
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_broken_image)
                .into(imageView);
        isSolution = false;
    }

    public void setImage_s() {
        Difference question = questions.get(0);
        Picasso.with(getActivity())
                .load(question.getImage_s())
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_broken_image)
                .into(imageView);
        isSolution = true;
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
                    if (questionNo == questionsSize)
                        next.setText("إنهاء");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            choicesLinearLayout.setVisibility(View.GONE);
                            helpTextView.setVisibility(View.GONE);
                            fullscreenImageView.setVisibility(View.GONE);
                            solutionLinearLayout.setVisibility(View.VISIBLE);
                            questionTextView.setText("الإجابة صحيحة");
                            show.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isSolution) {
                                        setImage();
                                        show.setText("عرض الحل");
                                        isSolution = false;
                                    } else {
                                        setImage_s();
                                        show.setText("عرض الأصل");
                                        isSolution = true;
                                    }
                                    //show.setEnabled(false);
                                }
                            });
                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (questionNo < questionsSize) {
                                        questions.remove(0);
                                        setQuestions();
                                        enableButtons();
                                        button.setBackground(getResources().getDrawable(R.drawable.level_selector));
                                        questionNo+=1;
                                        questionNoTextView.setText(questionsSize+"/"+questionNo);
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
//                                                                                                    progressDialog.cancel();
//                                                                                                    showWonDialog();
                                                                                                } else {
                                                                                                    if (level == l) {
                                                                                                        docRef.update("t"+type,(level+1)+"")
                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(Void aVoid) {
//                                                                                                                        progressDialog.cancel();
//                                                                                                                        showWonDialog();
                                                                                                                    }
                                                                                                                })
                                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                                    @Override
                                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                                        Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                                });
                                                                                                    } else {
//                                                                                                        progressDialog.cancel();
//                                                                                                        showWonDialog();
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
//                                                                                                    progressDialog.cancel();
//                                                                                                    showWonDialog();
                                                                                                } else {
                                                                                                    if (level == l) {
                                                                                                        docRef.update("t"+type,(level+1)+"")
                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(Void aVoid) {
//                                                                                                                        progressDialog.cancel();
//                                                                                                                        showWonDialog();
                                                                                                                    }
                                                                                                                })
                                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                                    @Override
                                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                                        Toast.makeText(getActivity(), "تحقق من اتصالك بالإنترنت!", Toast.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                                });
                                                                                                    } else {
//                                                                                                        progressDialog.cancel();
//                                                                                                        showWonDialog();
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
                                                showWonDialog();
                                            }
                                        });
                                    }
                                }
                            });
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
                            String image = document.getString("i");
                            String image_s = document.getString("s");
                            String choice1 = document.getString("c1");
                            String choice2 = document.getString("c2");
                            String choice3 = document.getString("c3");
                            String choice4 = document.getString("c4");
                            questionList.add(new Difference(image, image_s, choice1, choice2, choice3, choice4));
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
                            String image = document.getString("i");
                            String image_s = document.getString("s");
                            String choice1 = document.getString("c1");
                            String choice2 = document.getString("c2");
                            String choice3 = document.getString("c3");
                            String choice4 = document.getString("c4");
                            questionList.add(new Difference(image, image_s, choice1, choice2, choice3, choice4));
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
        void onCallback(List<Difference> list);
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