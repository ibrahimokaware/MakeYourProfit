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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicTacToeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicTacToeFragment extends Fragment {

    TableLayout tableLayout;
    LinearLayout linearLayout;
    TextView s1, s2, s3, s4, s5, s6, s7, s8, s9, counterTextView, userTextView, deviceTextView, titleTextView, helpTextView;
    List<TextView> deviceChoices = new ArrayList<>();

    int counter = 1, user = 0, device = 0, points = 0, p, po;
    boolean isEarn = false, isAds;
    String today;

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
    private int max;

    public TicTacToeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TicTacToeFragment newInstance(String title, int max, int level) {
        TicTacToeFragment fragment = new TicTacToeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putInt(ARG_PARAM2, max);
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
            max = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tic_tac_toe, container, false);
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

        s1 = view.findViewById(R.id.s1);
        s2 = view.findViewById(R.id.s2);
        s3 = view.findViewById(R.id.s3);
        s4 = view.findViewById(R.id.s4);
        s5 = view.findViewById(R.id.s5);
        s6 = view.findViewById(R.id.s6);
        s7 = view.findViewById(R.id.s7);
        s8 = view.findViewById(R.id.s8);
        s9 = view.findViewById(R.id.s9);

        counterTextView = view.findViewById(R.id.counterTextView);
        userTextView = view.findViewById(R.id.scoreTextView);
        deviceTextView = view.findViewById(R.id.attemptsTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        helpTextView = view.findViewById(R.id.helpTextView);
        linearLayout = view.findViewById(R.id.linearLayout5);
        tableLayout = view.findViewById(R.id.tableLayout);
        progressBar = getActivity().findViewById(R.id.progressBar);

        linearLayout.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.INVISIBLE);
        helpTextView.setVisibility(View.INVISIBLE);
        counterTextView.setVisibility(View.INVISIBLE);

        readData(new MyCallback() {
            @Override
            public void onCallback(int pp, int popo, String t) {

                p = pp; po = popo; today = t;

                linearLayout.setVisibility(View.VISIBLE);
                tableLayout.setVisibility(View.VISIBLE);
                if (isAds)
                    helpTextView.setVisibility(View.VISIBLE);
                counterTextView.setVisibility(View.VISIBLE);

                titleTextView.setText(title);
                counterTextView.setText("("+counter+"/"+max+")");

                deviceChoices.add(s1); deviceChoices.add(s2); deviceChoices.add(s3);
                deviceChoices.add(s4); deviceChoices.add(s5); deviceChoices.add(s6);
                deviceChoices.add(s7); deviceChoices.add(s8); deviceChoices.add(s9);

                helpTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpDialog("إضافة نقطة","قم بمشاهدة إعلان واحصل على مكافأة بإضافة نقطة.", R.drawable.ic_tip);
                    }
                });

                s1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s1.setText("X");
                        s1.setTextColor(getResources().getColor(R.color.colorYalow));
                        s1.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s1);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });

                s2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s2.setText("X");
                        s2.setTextColor(getResources().getColor(R.color.colorYalow));
                        s2.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s2);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });

                s3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s3.setText("X");
                        s3.setTextColor(getResources().getColor(R.color.colorYalow));
                        s3.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s3);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });

                s4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s4.setText("X");
                        s4.setTextColor(getResources().getColor(R.color.colorYalow));
                        s4.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s4);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });

                s5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s5.setText("X");
                        s5.setTextColor(getResources().getColor(R.color.colorYalow));
                        s5.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s5);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });

                s6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s6.setText("X");
                        s6.setTextColor(getResources().getColor(R.color.colorYalow));
                        s6.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s6);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });

                s7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s7.setText("X");
                        s7.setTextColor(getResources().getColor(R.color.colorYalow));
                        s7.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s7);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });

                s8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s8.setText("X");
                        s8.setTextColor(getResources().getColor(R.color.colorYalow));
                        s8.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s8);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });

                s9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s9.setText("X");
                        s9.setTextColor(getResources().getColor(R.color.colorYalow));
                        s9.setEnabled(false);
                        //verify();
                        deviceChoices.remove(s9);
                        Collections.shuffle(deviceChoices);
                        deviceChoices.get(0).setText("O");
                        deviceChoices.get(0).setTextColor(getResources().getColor(R.color.colorCheck));
                        deviceChoices.get(0).setEnabled(false);
                        deviceChoices.remove(0);
                        verify();
                    }
                });
            }
        });
    }

    public void verify() {
        if (s1.getText().toString().equals("X") && s2.getText().toString().equals("X") && s3.getText().toString().equals("X")) {
            s1.setTextColor(getResources().getColor(R.color.colorGreen));
            s2.setTextColor(getResources().getColor(R.color.colorGreen));
            s3.setTextColor(getResources().getColor(R.color.colorGreen));
            counter++; user++;
            userTextView.setText("أنت: "+ user);
            //Toast.makeText(getActivity(), "لقد فزت", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s4.getText().toString().equals("X") && s5.getText().toString().equals("X") && s6.getText().toString().equals("X")) {
            s4.setTextColor(getResources().getColor(R.color.colorGreen));
            s5.setTextColor(getResources().getColor(R.color.colorGreen));
            s6.setTextColor(getResources().getColor(R.color.colorGreen));
            counter++; user++;
            userTextView.setText("أنت: "+ user);
            //Toast.makeText(getActivity(), "لقد فزت", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s7.getText().toString().equals("X") && s8.getText().toString().equals("X") && s9.getText().toString().equals("X")) {
            s7.setTextColor(getResources().getColor(R.color.colorGreen));
            s8.setTextColor(getResources().getColor(R.color.colorGreen));
            s9.setTextColor(getResources().getColor(R.color.colorGreen));
            counter++; user++;
            userTextView.setText("أنت: "+ user);
            //Toast.makeText(getActivity(), "لقد فزت", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s1.getText().toString().equals("X") && s4.getText().toString().equals("X") && s7.getText().toString().equals("X")) {
            s1.setTextColor(getResources().getColor(R.color.colorGreen));
            s4.setTextColor(getResources().getColor(R.color.colorGreen));
            s7.setTextColor(getResources().getColor(R.color.colorGreen));
            counter++; user++;
            userTextView.setText("أنت: "+ user);
            //Toast.makeText(getActivity(), "لقد فزت", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s2.getText().toString().equals("X") && s5.getText().toString().equals("X") && s8.getText().toString().equals("X")) {
            s2.setTextColor(getResources().getColor(R.color.colorGreen));
            s5.setTextColor(getResources().getColor(R.color.colorGreen));
            s8.setTextColor(getResources().getColor(R.color.colorGreen));
            counter++; user++;
            userTextView.setText("أنت: "+ user);
            //Toast.makeText(getActivity(), "لقد فزت", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s3.getText().toString().equals("X") && s6.getText().toString().equals("X") && s9.getText().toString().equals("X")) {
            s3.setTextColor(getResources().getColor(R.color.colorGreen));
            s6.setTextColor(getResources().getColor(R.color.colorGreen));
            s9.setTextColor(getResources().getColor(R.color.colorGreen));
            counter++; user++;
            userTextView.setText("أنت: "+ user);
            //Toast.makeText(getActivity(), "لقد فزت", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s1.getText().toString().equals("X") && s5.getText().toString().equals("X") && s9.getText().toString().equals("X")) {
            s1.setTextColor(getResources().getColor(R.color.colorGreen));
            s5.setTextColor(getResources().getColor(R.color.colorGreen));
            s9.setTextColor(getResources().getColor(R.color.colorGreen));
            counter++; user++;
            userTextView.setText("أنت: "+ user);
            //Toast.makeText(getActivity(), "لقد فزت", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s3.getText().toString().equals("X") && s5.getText().toString().equals("X") && s7.getText().toString().equals("X")) {
            s3.setTextColor(getResources().getColor(R.color.colorGreen));
            s5.setTextColor(getResources().getColor(R.color.colorGreen));
            s7.setTextColor(getResources().getColor(R.color.colorGreen));
            counter++; user++;
            userTextView.setText("أنت: "+ user);
            //Toast.makeText(getActivity(), "لقد فزت", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);


        } else if (s1.getText().toString().equals("O") && s2.getText().toString().equals("O") && s3.getText().toString().equals("O")) {
            s1.setTextColor(getResources().getColor(R.color.colorRed));
            s2.setTextColor(getResources().getColor(R.color.colorRed));
            s3.setTextColor(getResources().getColor(R.color.colorRed));
            counter++; device++;
            deviceTextView.setText("الجهاز: "+ device);
            //Toast.makeText(getActivity(), "فاز الجهاز", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s4.getText().toString().equals("O") && s5.getText().toString().equals("O") && s6.getText().toString().equals("O")) {
            s4.setTextColor(getResources().getColor(R.color.colorRed));
            s5.setTextColor(getResources().getColor(R.color.colorRed));
            s6.setTextColor(getResources().getColor(R.color.colorRed));
            counter++; device++;
            deviceTextView.setText("الجهاز: "+ device);
            //Toast.makeText(getActivity(), "فاز الجهاز", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s7.getText().toString().equals("O") && s8.getText().toString().equals("O") && s9.getText().toString().equals("O")) {
            s7.setTextColor(getResources().getColor(R.color.colorRed));
            s8.setTextColor(getResources().getColor(R.color.colorRed));
            s9.setTextColor(getResources().getColor(R.color.colorRed));
            counter++; device++;
            deviceTextView.setText("الجهاز: "+ device);
            //Toast.makeText(getActivity(), "فاز الجهاز", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s1.getText().toString().equals("O") && s4.getText().toString().equals("O") && s7.getText().toString().equals("O")) {
            s1.setTextColor(getResources().getColor(R.color.colorRed));
            s4.setTextColor(getResources().getColor(R.color.colorRed));
            s7.setTextColor(getResources().getColor(R.color.colorRed));
            counter++; device++;
            deviceTextView.setText("الجهاز: "+ device);
            //Toast.makeText(getActivity(), "فاز الجهاز", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s2.getText().toString().equals("O") && s5.getText().toString().equals("O") && s8.getText().toString().equals("O")) {
            s2.setTextColor(getResources().getColor(R.color.colorRed));
            s5.setTextColor(getResources().getColor(R.color.colorRed));
            s8.setTextColor(getResources().getColor(R.color.colorRed));
            counter++; device++;
            deviceTextView.setText("الجهاز: "+ device);
            //Toast.makeText(getActivity(), "فاز الجهاز", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s3.getText().toString().equals("O") && s6.getText().toString().equals("O") && s9.getText().toString().equals("O")) {
            s3.setTextColor(getResources().getColor(R.color.colorRed));
            s6.setTextColor(getResources().getColor(R.color.colorRed));
            s9.setTextColor(getResources().getColor(R.color.colorRed));
            counter++; device++;
            deviceTextView.setText("الجهاز: "+ device);
            //Toast.makeText(getActivity(), "فاز الجهاز", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s1.getText().toString().equals("O") && s5.getText().toString().equals("O") && s9.getText().toString().equals("O")) {
            s1.setTextColor(getResources().getColor(R.color.colorRed));
            s5.setTextColor(getResources().getColor(R.color.colorRed));
            s9.setTextColor(getResources().getColor(R.color.colorRed));
            counter++; device++;
            deviceTextView.setText("الجهاز: "+ device);
            //Toast.makeText(getActivity(), "فاز الجهاز", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (s3.getText().toString().equals("O") && s5.getText().toString().equals("O") && s7.getText().toString().equals("O")) {
            s3.setTextColor(getResources().getColor(R.color.colorRed));
            s5.setTextColor(getResources().getColor(R.color.colorRed));
            s7.setTextColor(getResources().getColor(R.color.colorRed));
            counter++; device++;
            deviceTextView.setText("الجهاز: "+ device);
            //Toast.makeText(getActivity(), "فاز الجهاز", Toast.LENGTH_SHORT).show();
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        } else if (deviceChoices.size() == 1) {
            s1.setTextColor(getResources().getColor(R.color.colorDraw));
            s2.setTextColor(getResources().getColor(R.color.colorDraw));
            s3.setTextColor(getResources().getColor(R.color.colorDraw));
            s4.setTextColor(getResources().getColor(R.color.colorDraw));
            s5.setTextColor(getResources().getColor(R.color.colorDraw));
            s6.setTextColor(getResources().getColor(R.color.colorDraw));
            s7.setTextColor(getResources().getColor(R.color.colorDraw));
            s8.setTextColor(getResources().getColor(R.color.colorDraw));
            s9.setTextColor(getResources().getColor(R.color.colorDraw));
            //Toast.makeText(getActivity(), "لا يوجد فائز", Toast.LENGTH_SHORT).show();
            counter++;
            reset();
            //s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
            //s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
            /*s1.setTextColor(getResources().getColor(R.color.colorRed));
            s2.setTextColor(getResources().getColor(R.color.colorRed));
            s3.setTextColor(getResources().getColor(R.color.colorRed));
            s4.setTextColor(getResources().getColor(R.color.colorRed));
            s5.setTextColor(getResources().getColor(R.color.colorRed));
            s6.setTextColor(getResources().getColor(R.color.colorRed));
            s7.setTextColor(getResources().getColor(R.color.colorRed));
            s8.setTextColor(getResources().getColor(R.color.colorRed));
            s9.setTextColor(getResources().getColor(R.color.colorRed));*/
        }
    }

    public void reset(){
        s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
        s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 5000ms
                if (counter <(max+1)) {
                    s1.setEnabled(true); s2.setEnabled(true); s3.setEnabled(true); s4.setEnabled(true); s5.setEnabled(true); s6.setEnabled(true);
                    s7.setEnabled(true); s8.setEnabled(true); s9.setEnabled(true);
                    deviceChoices.clear();
                    deviceChoices.add(s1); deviceChoices.add(s2); deviceChoices.add(s3);
                    deviceChoices.add(s4); deviceChoices.add(s5); deviceChoices.add(s6);
                    deviceChoices.add(s7); deviceChoices.add(s8); deviceChoices.add(s9);
                    s1.setText(""); s2.setText(""); s3.setText(""); s4.setText(""); s5.setText(""); s6.setText(""); s7.setText(""); s8.setText("");
                    s9.setText("");
                    //counter++;
                    counterTextView.setText("("+counter+"/"+max+")");
                } else {
                    if (user > device) {
                        //Toast.makeText(getActivity(), "You Win.", Toast.LENGTH_SHORT).show();
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
//                                                                                                progressDialog.cancel();
//                                                                                                showWonDialog();
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
//                                                                                                progressDialog.cancel();
//                                                                                                showWonDialog();
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
                    else if (user < device)
                        //Toast.makeText(getActivity(), "Device Win.", Toast.LENGTH_SHORT).show();
                        showLostDialog();
                    else  //Toast.makeText(getActivity(), "No Winner!", Toast.LENGTH_SHORT).show();
                        setDialog2("تعادل","النتيجة تعادل، هل تريد اللعب مجدداً؟", R.drawable.ic_baseline_info_24);
                    s1.setEnabled(false); s2.setEnabled(false); s3.setEnabled(false); s4.setEnabled(false); s5.setEnabled(false); s6.setEnabled(false);
                    s7.setEnabled(false); s8.setEnabled(false); s9.setEnabled(false);
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
        dialog.setContentView(R.layout.wons);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button newBT = dialog.findViewById(R.id.new_bt);
        Button exitBT = dialog.findViewById(R.id.exit_bt);

        newBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                s1.setEnabled(true); s2.setEnabled(true); s3.setEnabled(true); s4.setEnabled(true); s5.setEnabled(true); s6.setEnabled(true);
                s7.setEnabled(true); s8.setEnabled(true); s9.setEnabled(true);
                deviceChoices.clear();
                deviceChoices.add(s1); deviceChoices.add(s2); deviceChoices.add(s3);
                deviceChoices.add(s4); deviceChoices.add(s5); deviceChoices.add(s6);
                deviceChoices.add(s7); deviceChoices.add(s8); deviceChoices.add(s9);
                s1.setText(""); s2.setText(""); s3.setText(""); s4.setText(""); s5.setText(""); s6.setText(""); s7.setText(""); s8.setText("");
                s9.setText("");
                user = 0; device = 0; counter = 1;
                counterTextView.setText("("+counter+"/"+max+")");
                userTextView.setText("أنت: "+ user);
                deviceTextView.setText("الجهاز: "+ device);
//                startActivity(new Intent(getActivity(), LevelsActivity.class).putExtra("title",title)
//                        .putExtra("type", type).putExtra("back", 2));
            }
        });

        exitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
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
                            counter--;
                            if (device > user+1)
                                device = user+1;
                            userTextView.setText("أنت: "+ user);
                            deviceTextView.setText("الجهاز: "+ device);
                            reset();
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
                startActivity(new Intent(getActivity(), LevelsActivity.class).putExtra("title",title)
                        .putExtra("type", type).putExtra("back", 2));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void setDialog2 (String titl, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(titl)
                .setIcon(icon);

        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                s1.setEnabled(true); s2.setEnabled(true); s3.setEnabled(true); s4.setEnabled(true); s5.setEnabled(true); s6.setEnabled(true);
                s7.setEnabled(true); s8.setEnabled(true); s9.setEnabled(true);
                deviceChoices.clear();
                deviceChoices.add(s1); deviceChoices.add(s2); deviceChoices.add(s3);
                deviceChoices.add(s4); deviceChoices.add(s5); deviceChoices.add(s6);
                deviceChoices.add(s7); deviceChoices.add(s8); deviceChoices.add(s9);
                s1.setText(""); s2.setText(""); s3.setText(""); s4.setText(""); s5.setText(""); s6.setText(""); s7.setText(""); s8.setText("");
                s9.setText("");
                user = 0; device = 0; counter = 1;
                counterTextView.setText("("+counter+"/"+max+")");
                userTextView.setText("أنت: "+ user);
                deviceTextView.setText("الجهاز: "+ device);
            }
        });

        builder.setNegativeButton("لا", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(getActivity(), LevelsActivity.class).putExtra("title",title)
                        .putExtra("type", type).putExtra("back", 2));
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
                            user++;
                            userTextView.setText("أنت: "+ user);
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