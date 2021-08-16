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
import androidx.cardview.widget.CardView;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LettersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LettersFragment extends Fragment {

    DateFormat dateFormat;

    ImageView imageView;
    TextView questionTextView, questionNoTextView, titleTextView, levelTextView, helpTextView;
    Button button1, button2, button3, button4, button5, button6, button7, button8,
            button9, button10, button11, button12, button1s, button2s, button3s, button4s, button5s, button6s, button7s, button8s,
            button9s;
    LinearLayout titleLinearLayout, answerLinearLayout, questionLinearLayout;

    String userAnswer, unitAds1Id, unitAds2Id, unitAds3Id, today;
    int questionNo = 1, points = 0, correctAnswers = 0, wrongAnswers = 0, questionsSize, answerLength, visibleCharacters, p, l, po;
    boolean isEarn = false, isAds;

    Handler handler = new Handler();

    ArrayList<LettersQuestion> questions ;

    private ProgressBar progressBar;
    ProgressDialog progressDialog;

    List<LettersQuestion> questionList = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("questions");
    private DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());

    ClickedButtons clickedButtons = new ClickedButtons();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String title;
    private int type;
    private int level;

    public LettersFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LettersFragment newInstance(String title, int type, int level) {
        LettersFragment fragment = new LettersFragment();
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
        return inflater.inflate(R.layout.fragment_letters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        imageView = view.findViewById(R.id.questionImage);
        questionTextView = view.findViewById(R.id.tv);
        questionNoTextView = view.findViewById(R.id.questionsTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        levelTextView = view.findViewById(R.id.levelTextView);
        helpTextView = view.findViewById(R.id.helpTextView);
        titleLinearLayout = view.findViewById(R.id.linearLayout5);
        answerLinearLayout = view.findViewById(R.id.linearLayout);
        questionLinearLayout = view.findViewById(R.id.linearLayout1000);
        button1 = view.findViewById(R.id.button1);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        button4 = view.findViewById(R.id.button4);
        button5 = view.findViewById(R.id.button5);
        button6 = view.findViewById(R.id.button6);
        button7 = view.findViewById(R.id.button7);
        button8 = view.findViewById(R.id.button8);
        button9 = view.findViewById(R.id.button9);
        button10 = view.findViewById(R.id.button10);
        button11 = view.findViewById(R.id.button11);
        button12 = view.findViewById(R.id.button12);
        button1s = view.findViewById(R.id.button1s);
        button2s = view.findViewById(R.id.button2s);
        button3s = view.findViewById(R.id.button3s);
        button4s = view.findViewById(R.id.button4s);
        button5s = view.findViewById(R.id.button5s);
        button6s = view.findViewById(R.id.button6s);
        button7s = view.findViewById(R.id.button7s);
        button8s = view.findViewById(R.id.button8s);
        button9s = view.findViewById(R.id.button9s);

        titleLinearLayout.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        questionTextView.setVisibility(View.INVISIBLE);
        answerLinearLayout.setVisibility(View.INVISIBLE);
        questionLinearLayout.setVisibility(View.INVISIBLE);
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
            public void onCallback(List<LettersQuestion> list) {
                questions = (ArrayList<LettersQuestion>) list;

                if (level == 0) {
                    levelTextView.setText("0");
                    questionsSize = 3;
                } else questionsSize = questions.size();

                questionNoTextView.setText(questionsSize+"/1");

                Collections.shuffle(questions);

                setQuestions();

                titleLinearLayout.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                questionTextView.setVisibility(View.VISIBLE);
                answerLinearLayout.setVisibility(View.VISIBLE);
                questionLinearLayout.setVisibility(View.VISIBLE);
                if (isAds)
                    helpTextView.setVisibility(View.VISIBLE);
                helpTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (visibleCharacters == answerLength-1)
                            Toast.makeText(getActivity(), "لا يمكن كشف أحرف أخرى!", Toast.LENGTH_SHORT).show();
                        else helpDialog("كشف حرف","قم بمشاهدة إعلان واحصل على مكافأة بكشف حرف.", R.drawable.ic_tip);
                    }
                });

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button1);
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button2);
                    }
                });

                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button3);
                    }
                });

                button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button4);
                    }
                });

                button5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button5);
                    }
                });

                button6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button6);
                    }
                });

                button7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button7);
                    }
                });

                button8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button8);
                    }
                });

                button9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button9);
                    }
                });

                button10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button10);
                    }
                });

                button11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button11);
                    }
                });

                button12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkButtons(button12);
                    }
                });

                button1s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton1().setVisibility(View.VISIBLE);
                        button1s.setText("");
                        button1s.setEnabled(false);
                    }
                });

                button2s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton2().setVisibility(View.VISIBLE);
                        button2s.setText("");
                        button2s.setEnabled(false);
                    }
                });

                button3s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton3().setVisibility(View.VISIBLE);
                        button3s.setText("");
                        button3s.setEnabled(false);
                    }
                });

                button4s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton4().setVisibility(View.VISIBLE);
                        button4s.setText("");
                        button4s.setEnabled(false);
                    }
                });

                button5s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton5().setVisibility(View.VISIBLE);
                        button5s.setText("");
                        button5s.setEnabled(false);
                    }
                });

                button6s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton6().setVisibility(View.VISIBLE);
                        button6s.setText("");
                        button6s.setEnabled(false);
                    }
                });

                button7s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton7().setVisibility(View.VISIBLE);
                        button7s.setText("");
                        button7s.setEnabled(false);
                    }
                });

                button8s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton8().setVisibility(View.VISIBLE);
                        button8s.setText("");
                        button8s.setEnabled(false);
                    }
                });

                button9s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedButtons.getButton9().setVisibility(View.VISIBLE);
                        button9s.setText("");
                        button9s.setEnabled(false);
                    }
                });
            }
        });
    }

    public void checkButtons(Button button) {
        switch (answerLength) {
            case 2: if (button1s.getText().toString().equals("")) {
                button1s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button1s.setEnabled(true);
                clickedButtons.setButton1(button);
            }
            else if (button2s.getText().toString().equals("")) {
                button2s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button2s.setEnabled(true);
                clickedButtons.setButton2(button);
                verify();
            }
            break;
            case 3: if (button1s.getText().toString().equals("")) {
                button1s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button1s.setEnabled(true);
                clickedButtons.setButton1(button);
            }
            else if (button2s.getText().toString().equals("")) {
                button2s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button2s.setEnabled(true);
                clickedButtons.setButton2(button);
            } else if (button3s.getText().toString().equals("")) {
                button3s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button3s.setEnabled(true);
                clickedButtons.setButton3(button);
                verify();
            }
            break;
            case 4: if (button1s.getText().toString().equals("")) {
                button1s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button1s.setEnabled(true);
                clickedButtons.setButton1(button);
            }
            else if (button2s.getText().toString().equals("")) {
                button2s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button2s.setEnabled(true);
                clickedButtons.setButton2(button);
            } else if (button3s.getText().toString().equals("")) {
                button3s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button3s.setEnabled(true);
                clickedButtons.setButton3(button);
            } else if (button4s.getText().toString().equals("")) {
                button4s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button4s.setEnabled(true);
                clickedButtons.setButton4(button);
                verify();
            }
            break;
            case 5: if (button1s.getText().toString().equals("")) {
                button1s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button1s.setEnabled(true);
                clickedButtons.setButton1(button);
            }
            else if (button2s.getText().toString().equals("")) {
                button2s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button2s.setEnabled(true);
                clickedButtons.setButton2(button);
            } else if (button3s.getText().toString().equals("")) {
                button3s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button3s.setEnabled(true);
                clickedButtons.setButton3(button);
            } else if (button4s.getText().toString().equals("")) {
                button4s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button4s.setEnabled(true);
                clickedButtons.setButton4(button);
            } else if (button5s.getText().toString().equals("")) {
                button5s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button5s.setEnabled(true);
                clickedButtons.setButton5(button);
                verify();
            }
            break;
            case 6: if (button1s.getText().toString().equals("")) {
                button1s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button1s.setEnabled(true);
                clickedButtons.setButton1(button);
            }
            else if (button2s.getText().toString().equals("")) {
                button2s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button2s.setEnabled(true);
                clickedButtons.setButton2(button);
            } else if (button3s.getText().toString().equals("")) {
                button3s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button3s.setEnabled(true);
                clickedButtons.setButton3(button);
            } else if (button4s.getText().toString().equals("")) {
                button4s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button4s.setEnabled(true);
                clickedButtons.setButton4(button);
            } else if (button5s.getText().toString().equals("")) {
                button5s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button5s.setEnabled(true);
                clickedButtons.setButton5(button);
            }  else if (button6s.getText().toString().equals("")) {
                button6s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button6s.setEnabled(true);
                clickedButtons.setButton6(button);
                verify();
            }
            break;
            case 7: if (button1s.getText().toString().equals("")) {
                button1s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button1s.setEnabled(true);
                clickedButtons.setButton1(button);
            }
            else if (button2s.getText().toString().equals("")) {
                button2s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button2s.setEnabled(true);
                clickedButtons.setButton2(button);
            } else if (button3s.getText().toString().equals("")) {
                button3s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button3s.setEnabled(true);
                clickedButtons.setButton3(button);
            } else if (button4s.getText().toString().equals("")) {
                button4s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button4s.setEnabled(true);
                clickedButtons.setButton4(button);
            } else if (button5s.getText().toString().equals("")) {
                button5s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button5s.setEnabled(true);
                clickedButtons.setButton5(button);
            }  else if (button6s.getText().toString().equals("")) {
                button6s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button6s.setEnabled(true);
                clickedButtons.setButton6(button);
            } else if (button7s.getText().toString().equals("")) {
                button7s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button7s.setEnabled(true);
                clickedButtons.setButton7(button);
                verify();
            }
            break;
            case 8: if (button1s.getText().toString().equals("")) {
                button1s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button1s.setEnabled(true);
                clickedButtons.setButton1(button);
            }
            else if (button2s.getText().toString().equals("")) {
                button2s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button2s.setEnabled(true);
                clickedButtons.setButton2(button);
            } else if (button3s.getText().toString().equals("")) {
                button3s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button3s.setEnabled(true);
                clickedButtons.setButton3(button);
            } else if (button4s.getText().toString().equals("")) {
                button4s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button4s.setEnabled(true);
                clickedButtons.setButton4(button);
            } else if (button5s.getText().toString().equals("")) {
                button5s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button5s.setEnabled(true);
                clickedButtons.setButton5(button);
            }  else if (button6s.getText().toString().equals("")) {
                button6s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button6s.setEnabled(true);
                clickedButtons.setButton6(button);
            } else if (button7s.getText().toString().equals("")) {
                button7s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button7s.setEnabled(true);
                clickedButtons.setButton7(button);
            } else if (button8s.getText().toString().equals("")) {
                button8s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button8s.setEnabled(true);
                clickedButtons.setButton8(button);
                verify();
            }
            break;
            case 9: if (button1s.getText().toString().equals("")) {
                button1s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button1s.setEnabled(true);
                clickedButtons.setButton1(button);
            }
            else if (button2s.getText().toString().equals("")) {
                button2s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button2s.setEnabled(true);
                clickedButtons.setButton2(button);
            } else if (button3s.getText().toString().equals("")) {
                button3s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button3s.setEnabled(true);
                clickedButtons.setButton3(button);
            } else if (button4s.getText().toString().equals("")) {
                button4s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button4s.setEnabled(true);
                clickedButtons.setButton4(button);
            } else if (button5s.getText().toString().equals("")) {
                button5s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button5s.setEnabled(true);
                clickedButtons.setButton5(button);
            }  else if (button6s.getText().toString().equals("")) {
                button6s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button6s.setEnabled(true);
                clickedButtons.setButton6(button);
            } else if (button7s.getText().toString().equals("")) {
                button7s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button7s.setEnabled(true);
                clickedButtons.setButton7(button);
            } else if (button8s.getText().toString().equals("")) {
                button8s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button8s.setEnabled(true);
                clickedButtons.setButton8(button);
            } else if (button9s.getText().toString().equals("")) {
                button9s.setText(button.getText().toString());
                button.setVisibility(View.INVISIBLE);
                button9s.setEnabled(true);
                clickedButtons.setButton9(button);
                verify();
            }
            break;
        }
    }

    public void setQuestions() {
        visibleCharacters = 0;
        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        button3.setVisibility(View.VISIBLE);
        button4.setVisibility(View.VISIBLE);
        button5.setVisibility(View.VISIBLE);
        button6.setVisibility(View.VISIBLE);
        button7.setVisibility(View.VISIBLE);
        button8.setVisibility(View.VISIBLE);
        button9.setVisibility(View.VISIBLE);
        button10.setVisibility(View.VISIBLE);
        button11.setVisibility(View.VISIBLE);
        button12.setVisibility(View.VISIBLE);
        button1s.setEnabled(false);
        button2s.setEnabled(false);
        button3s.setEnabled(false);
        button4s.setEnabled(false);
        button5s.setEnabled(false);
        button6s.setEnabled(false);
        button7s.setEnabled(false);
        button8s.setEnabled(false);
        button9s.setEnabled(false);
        button1s.setText("");
        button2s.setText("");
        button3s.setText("");
        button4s.setText("");
        button5s.setText("");
        button6s.setText("");
        button7s.setText("");
        button8s.setText("");
        button9s.setText("");
        LettersQuestion question = questions.get(0);
        questionTextView.setText(question.getText());
        Picasso.with(getActivity())
                .load(question.getImage())
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_broken_image)
                .into(imageView);
        //String[] strings = question.getAnswer().split("");
        //List<String> list = Arrays.asList(strings);
        ArrayList<String> list = question.getCharacters();
        answerLength = list.size();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i=0; i< answerLength; i++)
            arrayList.add(list.get(i));
        for (int i=answerLength; i<12; i++)
            arrayList.add(getRestCharacters().get(i));
        Collections.shuffle(arrayList);
        button1.setText(arrayList.get(1));
        button2.setText(arrayList.get(2));
        button3.setText(arrayList.get(3));
        button4.setText(arrayList.get(4));
        button5.setText(arrayList.get(5));
        button6.setText(arrayList.get(6));
        button7.setText(arrayList.get(7));
        button8.setText(arrayList.get(8));
        button9.setText(arrayList.get(9));
        button10.setText(arrayList.get(10));
        button11.setText(arrayList.get(11));
        button12.setText(arrayList.get(0));
        switch (answerLength) {
            case 2: button1s.setVisibility(View.VISIBLE);
                button2s.setVisibility(View.VISIBLE);
                button3s.setVisibility(View.GONE);
                button4s.setVisibility(View.GONE);
                button5s.setVisibility(View.GONE);
                button6s.setVisibility(View.GONE);
                button7s.setVisibility(View.GONE);
                button8s.setVisibility(View.GONE);
                button9s.setVisibility(View.GONE);
                break;
            case 3: button1s.setVisibility(View.VISIBLE);
                button2s.setVisibility(View.VISIBLE);
                button3s.setVisibility(View.VISIBLE);
                button4s.setVisibility(View.GONE);
                button5s.setVisibility(View.GONE);
                button6s.setVisibility(View.GONE);
                button7s.setVisibility(View.GONE);
                button8s.setVisibility(View.GONE);
                button9s.setVisibility(View.GONE);
                break;
            case 4: button1s.setVisibility(View.VISIBLE);
                button2s.setVisibility(View.VISIBLE);
                button3s.setVisibility(View.VISIBLE);
                button4s.setVisibility(View.VISIBLE);
                button5s.setVisibility(View.GONE);
                button6s.setVisibility(View.GONE);
                button7s.setVisibility(View.GONE);
                button8s.setVisibility(View.GONE);
                button9s.setVisibility(View.GONE);
                break;
            case 5: button1s.setVisibility(View.VISIBLE);
                button2s.setVisibility(View.VISIBLE);
                button3s.setVisibility(View.VISIBLE);
                button4s.setVisibility(View.VISIBLE);
                button5s.setVisibility(View.VISIBLE);
                button6s.setVisibility(View.GONE);
                button7s.setVisibility(View.GONE);
                button8s.setVisibility(View.GONE);
                button9s.setVisibility(View.GONE);
                break;
            case 6: button1s.setVisibility(View.VISIBLE);
                button2s.setVisibility(View.VISIBLE);
                button3s.setVisibility(View.VISIBLE);
                button4s.setVisibility(View.VISIBLE);
                button5s.setVisibility(View.VISIBLE);
                button6s.setVisibility(View.VISIBLE);
                button7s.setVisibility(View.GONE);
                button8s.setVisibility(View.GONE);
                button9s.setVisibility(View.GONE);
                break;
            case 7: button1s.setVisibility(View.VISIBLE);
                button2s.setVisibility(View.VISIBLE);
                button3s.setVisibility(View.VISIBLE);
                button4s.setVisibility(View.VISIBLE);
                button5s.setVisibility(View.VISIBLE);
                button6s.setVisibility(View.VISIBLE);
                button7s.setVisibility(View.VISIBLE);
                button8s.setVisibility(View.GONE);
                button9s.setVisibility(View.GONE);
                break;
            case 8:  button1s.setVisibility(View.VISIBLE);
                button2s.setVisibility(View.VISIBLE);
                button3s.setVisibility(View.VISIBLE);
                button4s.setVisibility(View.VISIBLE);
                button5s.setVisibility(View.VISIBLE);
                button6s.setVisibility(View.VISIBLE);
                button7s.setVisibility(View.VISIBLE);
                button8s.setVisibility(View.VISIBLE);
                button9s.setVisibility(View.GONE);
            break;
            case 9:  button1s.setVisibility(View.VISIBLE);
                button2s.setVisibility(View.VISIBLE);
                button3s.setVisibility(View.VISIBLE);
                button4s.setVisibility(View.VISIBLE);
                button5s.setVisibility(View.VISIBLE);
                button6s.setVisibility(View.VISIBLE);
                button7s.setVisibility(View.VISIBLE);
                button8s.setVisibility(View.VISIBLE);
                button9s.setVisibility(View.VISIBLE);
                break;
        }
    }

    public ArrayList<String> getRestCharacters () {
        ArrayList<String> characters = new ArrayList<>();
        characters.add("أ");characters.add("ب");characters.add("ت");characters.add("ث");characters.add("ج");characters.add("ح");characters.add("خ");
        characters.add("د");characters.add("ذ");characters.add("ر");characters.add("ز");characters.add("س");characters.add("ش");characters.add("ص");
        characters.add("ض");characters.add("ط");characters.add("ظ");characters.add("ع");characters.add("غ");characters.add("ف");characters.add("ق");
        characters.add("ك");characters.add("ل");characters.add("م");characters.add("ن");characters.add("ه");characters.add("و");characters.add("ي");
        characters.add("ا");characters.add("إ");characters.add("آ");characters.add("ى");characters.add("ئ");characters.add("ؤ");characters.add("ء");
        characters.add("ة");
        Collections.shuffle(characters);
        return characters;
    }

    public String getAnswer() {
        LettersQuestion question = questions.get(0);
        return question.getAnswer();
    }

    public ArrayList<String> getCharacters() {
        LettersQuestion question = questions.get(0);
        return question.getCharacters();
    }

    public void verify() {
        switch (answerLength) {
            case 2: userAnswer = button1s.getText().toString()+ button2s.getText().toString();
                break;
            case 3: userAnswer = button1s.getText().toString()+ button2s.getText().toString()+ button3s.getText().toString();
                break;
            case 4: userAnswer = button1s.getText().toString()+ button2s.getText().toString()+ button3s.getText().toString()+ button4s.getText().toString();
                break;
            case 5: userAnswer = button1s.getText().toString()+ button2s.getText().toString()+ button3s.getText().toString()+
                    button4s.getText().toString()+ button5s.getText().toString();
                break;
            case 6: userAnswer = button1s.getText().toString()+ button2s.getText().toString()+ button3s.getText().toString()+
                    button4s.getText().toString()+ button5s.getText().toString()+ button6s.getText().toString();
                break;
            case 7: userAnswer = button1s.getText().toString()+ button2s.getText().toString()+ button3s.getText().toString()+
                    button4s.getText().toString()+ button5s.getText().toString()+ button6s.getText().toString()+ button7s.getText().toString();
                break;
            case 8: userAnswer = button1s.getText().toString()+ button2s.getText().toString()+ button3s.getText().toString()+
                    button4s.getText().toString()+ button5s.getText().toString()+ button6s.getText().toString()+ button7s.getText().toString()+
                    button8s.getText().toString();
                break;
            case 9: userAnswer = button1s.getText().toString()+ button2s.getText().toString()+ button3s.getText().toString()+
                    button4s.getText().toString()+ button5s.getText().toString()+ button6s.getText().toString()+ button7s.getText().toString()+
                    button8s.getText().toString()+ button9s.getText().toString();
            break;
        }
        //disableButtons();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userAnswer.equals(getAnswer())) {
                    button1s.setBackground(getResources().getDrawable(R.drawable.correct_l));
                    button2s.setBackground(getResources().getDrawable(R.drawable.correct_l));
                    button3s.setBackground(getResources().getDrawable(R.drawable.correct_l));
                    button4s.setBackground(getResources().getDrawable(R.drawable.correct_l));
                    button5s.setBackground(getResources().getDrawable(R.drawable.correct_l));
                    button6s.setBackground(getResources().getDrawable(R.drawable.correct_l));
                    button7s.setBackground(getResources().getDrawable(R.drawable.correct_l));
                    button8s.setBackground(getResources().getDrawable(R.drawable.correct_l));
                    button9s.setBackground(getResources().getDrawable(R.drawable.correct_l));
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
                                //enableButtons();
                                button1s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                                button2s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                                button3s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                                button4s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                                button5s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                                button6s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                                button7s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                                button8s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                                button9s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
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
                                        showWonDialog();
                                    }
                                });
                            }
                        }
                    }, 500);
                }
                else {
                    button1s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    button2s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    button3s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    button4s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    button5s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    button6s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    button7s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    button8s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    button9s.setBackground(getResources().getDrawable(R.drawable.wrong_l));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showLostDialog();
                            //points = 0;
                            //pointsTextView.setText(points+"");
                            //wrongAnswers+=1;
                            /*FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            LostFragment lostFragment = LostFragment.newInstance(title, type, false);
                            fragmentTransaction.replace(R.id.fragment_container, lostFragment);
                            //fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();*/
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
                            button1s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button2s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button3s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button4s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button5s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button6s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button7s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button8s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button9s.setBackground(getResources().getDrawable(R.drawable.letters_selector));
                            button1s.setEnabled(false);
                            button2s.setEnabled(false);
                            button3s.setEnabled(false);
                            button4s.setEnabled(false);
                            button5s.setEnabled(false);
                            button6s.setEnabled(false);
                            button7s.setEnabled(false);
                            button8s.setEnabled(false);
                            button9s.setEnabled(false);
                            button1s.setText("");
                            button2s.setText("");
                            button3s.setText("");
                            button4s.setText("");
                            button5s.setText("");
                            button6s.setText("");
                            button7s.setText("");
                            button8s.setText("");
                            button9s.setText("");
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.VISIBLE);
                            button4.setVisibility(View.VISIBLE);
                            button5.setVisibility(View.VISIBLE);
                            button6.setVisibility(View.VISIBLE);
                            button7.setVisibility(View.VISIBLE);
                            button8.setVisibility(View.VISIBLE);
                            button9.setVisibility(View.VISIBLE);
                            button10.setVisibility(View.VISIBLE);
                            button11.setVisibility(View.VISIBLE);
                            button12.setVisibility(View.VISIBLE);
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
                            //String[] strings = getAnswer().split("");
                            //List<String> list = Arrays.asList(strings);
                            ArrayList<String> list = getCharacters();
                            if (button1s.getVisibility() == View.VISIBLE && button1s.getText().toString().equals("")) {
                                button1s.setText(list.get(0));
                                button1s.setEnabled(false);
                                button1s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            } else if (button2s.getVisibility() == View.VISIBLE && button2s.getText().toString().equals("")) {
                                button2s.setText(list.get(1));
                                button2s.setEnabled(false);
                                button2s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            } else if (button3s.getVisibility() == View.VISIBLE && button3s.getText().toString().equals("")) {
                                button3s.setText(list.get(2));
                                button3s.setEnabled(false);
                                button3s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            } else if (button4s.getVisibility() == View.VISIBLE && button4s.getText().toString().equals("")) {
                                button4s.setText(list.get(3));
                                button4s.setEnabled(false);
                                button4s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            } else if (button5s.getVisibility() == View.VISIBLE && button5s.getText().toString().equals("")) {
                                button5s.setText(list.get(4));
                                button5s.setEnabled(false);
                                button5s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            } else if (button6s.getVisibility() == View.VISIBLE && button6s.getText().toString().equals("")) {
                                button6s.setText(list.get(5));
                                button6s.setEnabled(false);
                                button6s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            } else if (button7s.getVisibility() == View.VISIBLE && button7s.getText().toString().equals("")) {
                                button7s.setText(list.get(6));
                                button7s.setEnabled(false);
                                button7s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            } else if (button8s.getVisibility() == View.VISIBLE && button8s.getText().toString().equals("")) {
                                button8s.setText(list.get(7));
                                button8s.setEnabled(false);
                                button8s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            } else if (button9s.getVisibility() == View.VISIBLE && button9s.getText().toString().equals("")) {
                                button9s.setText(list.get(8));
                                button9s.setEnabled(false);
                                button9s.setBackground(getResources().getDrawable(R.drawable.visible_letter));
                            }
                            visibleCharacters++;
                            checkAnswers();
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

    public void checkAnswers() {
        switch (answerLength) {
            case 2: if (!button1s.getText().toString().equals("") && !button2s.getText().toString().equals(""))
                verify();
                break;
            case 3: if (!button1s.getText().toString().equals("") && !button2s.getText().toString().equals("")
                    && !button3s.getText().toString().equals(""))
                verify();
                break;
            case 4: if (!button1s.getText().toString().equals("") && !button2s.getText().toString().equals("")
                    && !button3s.getText().toString().equals("") && !button4s.getText().toString().equals(""))
                verify();
                break;
            case 5: if (!button1s.getText().toString().equals("") && !button2s.getText().toString().equals("")
                    && !button3s.getText().toString().equals("") && !button4s.getText().toString().equals("")
                    && !button5s.getText().toString().equals(""))
                verify();
                break;
            case 6: if (!button1s.getText().toString().equals("") && !button2s.getText().toString().equals("")
                    && !button3s.getText().toString().equals("") && !button4s.getText().toString().equals("")
                    && !button5s.getText().toString().equals("") && !button6s.getText().toString().equals(""))
                verify();
                break;
            case 7: if (!button1s.getText().toString().equals("") && !button2s.getText().toString().equals("")
                    && !button3s.getText().toString().equals("") && !button4s.getText().toString().equals("")
                    && !button5s.getText().toString().equals("") && !button6s.getText().toString().equals("")
                    && !button7s.getText().toString().equals(""))
                verify();
                break;
            case 8: if (!button1s.getText().toString().equals("") && !button2s.getText().toString().equals("")
                    && !button3s.getText().toString().equals("") && !button4s.getText().toString().equals("")
                    && !button5s.getText().toString().equals("") && !button6s.getText().toString().equals("")
                    && !button7s.getText().toString().equals("") && !button8s.getText().toString().equals(""))
                verify();
                break;
            case 9: if (!button1s.getText().toString().equals("") && !button2s.getText().toString().equals("")
                    && !button3s.getText().toString().equals("") && !button4s.getText().toString().equals("")
                    && !button5s.getText().toString().equals("") && !button6s.getText().toString().equals("")
                    && !button7s.getText().toString().equals("") && !button8s.getText().toString().equals("")
                    && !button9s.getText().toString().equals(""))
                verify();
                break;
        }
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
                            String type = document.getString("type");
                            String tl = document.getString("tl");
                            String image = document.getString("image");
                            String text = document.getString("text");
                            String answer = document.getString("answer");
                            ArrayList<String> characters = (ArrayList<String>) document.get("characters");
//                            Map<String, Object> question = new HashMap<>();
//                            question.put("tl", tl);
//                            question.put("type", type+"");
//                            question.put("image", image);
//                            question.put("text", text);
//                            question.put("answer", answer);
//                            String[] strings = answer.split("");
//                            List<String> list = Arrays.asList(strings);
//                            question.put("characters", list);
//                            notebookRef
//                                    .document()
//                                    .set(question)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            //Toast.makeText(getActivity(), "Success adding document", Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            //Toast.makeText(getActivity(), "Error adding document", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
                            questionList.add(new LettersQuestion(image, text, answer, characters));
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
                            String image = document.getString("image");
                            String text = document.getString("text");
                            String answer = document.getString("answer");
                            ArrayList<String> characters = (ArrayList<String>) document.get("characters");
                            questionList.add(new LettersQuestion(image, text, answer, characters));
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
        void onCallback(List<LettersQuestion> list);
    }

    static class ClickedButtons {
        private Button button1;
        private Button button2;
        private Button button3;
        private Button button4;
        private Button button5;
        private Button button6;
        private Button button7;
        private Button button8;
        private Button button9;
        private Button button10;
        private Button button11;
        private Button button12;

        public Button getButton1() {
            return button1;
        }

        public void setButton1(Button button1) {
            this.button1 = button1;
        }

        public Button getButton2() {
            return button2;
        }

        public void setButton2(Button button2) {
            this.button2 = button2;
        }

        public Button getButton3() {
            return button3;
        }

        public void setButton3(Button button3) {
            this.button3 = button3;
        }

        public Button getButton4() {
            return button4;
        }

        public void setButton4(Button button4) {
            this.button4 = button4;
        }

        public Button getButton5() {
            return button5;
        }

        public void setButton5(Button button5) {
            this.button5 = button5;
        }

        public Button getButton6() {
            return button6;
        }

        public void setButton6(Button button6) {
            this.button6 = button6;
        }

        public Button getButton7() {
            return button7;
        }

        public void setButton7(Button button7) {
            this.button7 = button7;
        }

        public Button getButton8() {
            return button8;
        }

        public void setButton8(Button button8) {
            this.button8 = button8;
        }

        public Button getButton9() {
            return button9;
        }

        public void setButton9(Button button9) {
            this.button9 = button9;
        }

        public Button getButton10() {
            return button10;
        }

        public void setButton10(Button button10) {
            this.button10 = button10;
        }

        public Button getButton11() {
            return button11;
        }

        public void setButton11(Button button11) {
            this.button11 = button11;
        }

        public Button getButton12() {
            return button12;
        }

        public void setButton12(Button button12) {
            this.button12 = button12;
        }
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