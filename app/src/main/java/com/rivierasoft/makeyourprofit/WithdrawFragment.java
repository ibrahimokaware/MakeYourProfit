package com.rivierasoft.makeyourprofit;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WithdrawFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WithdrawFragment extends Fragment {

    private TextView tv_video, statusTextView, valueTextView, dateTextView;
    private CardView cv_video;
    private LinearLayout linearLayout;
    private String points, times, value, link;
    private boolean withdraw, video;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());
    private DocumentReference generalRef = db.collection("questions").document("000General111");

    private ProgressBar progressBar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WithdrawFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgendaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WithdrawFragment newInstance(String param1, String param2) {
        WithdrawFragment fragment = new WithdrawFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_withdraw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_video = view.findViewById(R.id.tv_video);
        cv_video = view.findViewById(R.id.cv_video);
        statusTextView = view.findViewById(R.id.status_text);
        valueTextView = view.findViewById(R.id.value_text);
        dateTextView = view.findViewById(R.id.date_text);
        progressBar = view.findViewById(R.id.progressBar);
        linearLayout = view.findViewById(R.id.linearLayout);

        linearLayout.setVisibility(View.GONE);

        final DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");

        generalRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        dateTextView.setText(documentSnapshot.getString("date"));
                        value = documentSnapshot.getString("value");
                        video = documentSnapshot.getBoolean("video");
                        link = documentSnapshot.getString("video_link");
                        if (video)
                            cv_video.setVisibility(View.VISIBLE);
                        else cv_video.setVisibility(View.GONE);
                    } else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        tv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), VideoActivity.class).putExtra("link", link));
            }
        });

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        withdraw = (boolean) documentSnapshot.get("withdraw");
                        times = documentSnapshot.getString("times");
                        points = documentSnapshot.getString("points");
                        /*usersReference.whereEqualTo("withdraw", true)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        number = number+1;
                                    }*/
                                    if (!withdraw && Integer.parseInt(points) >= 1000)
                                        docRef.update("withdraw", true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        docRef.update("times", Integer.parseInt(times)+1+"")
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        docRef.update("withdraw_date", df.format(Calendar.getInstance().getTime()))
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @SuppressLint("ResourceAsColor")
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        /*withdrawRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                                                                    if (documentSnapshot.exists()) {
                                                                                                        //withdrawArrayList = (ArrayList<String>) documentSnapshot.get("withdraw");
                                                                                                        //withdrawArrayList.add(firebaseUser.getUid());
                                                                                                        dateTextView.setText(documentSnapshot.getString("date"));
                                                                                                        value1 = documentSnapshot.getString("value1");
                                                                                                        value2 = documentSnapshot.getString("value2");
                                                                                                    }
                                                                                                    withdrawRef.update("withdraw", withdrawArrayList)
                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                @SuppressLint("ResourceAsColor")
                                                                                                                @Override
                                                                                                                public void onSuccess(Void aVoid) {*/
                                                                                                                    //Toast.makeText(getActivity(), "تهانينا تم دخولك السحب بنجاح!",Toast.LENGTH_SHORT).show();
                                                                                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                                                                                if (documentSnapshot.exists()) {
                                                                                                                                    if ((boolean) documentSnapshot.get("withdraw")) {
                                                                                                                                        statusTextView.setText("تم دخولك السحب بنجاح");
                                                                                                                                    } else {
                                                                                                                                        statusTextView.setText("لم تدخل السحب بعد");
                                                                                                                                    }
                                                                                                                                    valueTextView.setText(value);
                                                                                                                                } else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                                                                                                                                    progressBar.setVisibility(View.GONE);
                                                                                                                                linearLayout.setVisibility(View.VISIBLE);
                                                                                                                            } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    });
                                                                                                    /*} else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            })
                                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                                @Override
                                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                                    Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            });
                                                                                                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });*/
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        //Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        //Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    else docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if (documentSnapshot.exists()) {
                                                    if ((boolean) documentSnapshot.get("withdraw")) {
                                                        statusTextView.setText("تم دخولك السحب بنجاح");
                                                    } else {
                                                        statusTextView.setText("لم تدخل السحب بعد");
                                                    }
                                                    valueTextView.setText(value);
                                                } else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                linearLayout.setVisibility(View.VISIBLE);
                                            } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                /*} else {
                                    Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/
                    }
                    else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}