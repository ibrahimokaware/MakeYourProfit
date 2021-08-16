package com.rivierasoft.makeyourprofit;

import android.app.Activity;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LevelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LevelsFragment extends Fragment {

    RepeatClass repeatClass = new RepeatClass();
    private int r = 0, no_levels, repeat = 0;
    private String type;

    private TextView pointsTextView, titleTextView, randomTextView;
    RecyclerView recyclerView;
    private ProgressBar progressBar;

    List<String> levelList = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference generalRef = db.collection("questions").document("000General111");
    private DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private int mParam2;

    public LevelsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LevelsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LevelsFragment newInstance(String param1, int param2) {
        LevelsFragment fragment = new LevelsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_levels, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pointsTextView = getActivity().findViewById(R.id.pointsTextView);
        randomTextView = view.findViewById(R.id.randomTextView);
        titleTextView = view.findViewById(R.id.textView6);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setVisibility(View.INVISIBLE);
        randomTextView.setVisibility(View.INVISIBLE);
        titleTextView.setVisibility(View.INVISIBLE);

        progressBar = getActivity().findViewById(R.id.progressBar);

        readData(new MyCallback() {
            @Override
            public void onCallback(final String t) {

                recyclerView.setVisibility(View.VISIBLE);
                randomTextView.setVisibility(View.VISIBLE);
                titleTextView.setVisibility(View.VISIBLE);

                //titleTextView.setText(mParam1);

                randomTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), QuizActivity.class).putExtra("title", mParam1)
                                .putExtra("type", mParam2).putExtra("level", 0));
                    }
                });

                final ArrayList<Level> levels = new ArrayList<>();
                levels.add(new Level(R.drawable.ic_lock_open, "1", true));
                for (int i=2; i<=no_levels; i++)
                    levels.add(new Level(R.drawable.ic_lock, i+"", false));

                final int tt = Integer.parseInt(t);

                if (tt!=1 && tt<=no_levels) {
                        levels.clear();
                        for (int i=1; i<=tt; i++)
                            levels.add(new Level(R.drawable.ic_lock_open, i+"", true));
                        for (int i=tt+1; i<=no_levels; i++)
                            levels.add(new Level(R.drawable.ic_lock, i+"", false));
                } else if (tt==no_levels+1){
                    levels.clear();
                    for (int i=1; i<=no_levels; i++)
                        levels.add(new Level(R.drawable.ic_lock_open, i+"", true));
                }


                LevelAdapter adapter = new LevelAdapter(levels, new OnRecyclerViewItemClickListener() {
                    @Override
                    public void OnClickListener(final int p, LinearLayout linearLayout) {
                        /*if (r == 0)
                            repeatClass.setP1(p);
                        else if (repeatClass.getP1() != p)
                            r = 0;
                        if (r == 0)
                            repeatClass.setP1(p);
                        else if (r == 1) {
                            if (repeatClass.getP1() == p)
                                repeatClass.setP2(p);
                            else {
                                r = 0;
                                repeatClass.setP1(-1);
                            }
                        } else if (r == 2) {
                             if (repeatClass.getP2() != p)
                                 r = 0;
                        }*/

                        if (p <= tt-1) {
                            startActivity(new Intent(getActivity(), QuizActivity.class).putExtra("title", mParam1)
                                    .putExtra("type", mParam2).putExtra("level", (p+1)));
                        }
                        else {
                            //Toast.makeText(getActivity(), "أكمل المستوى الحالي لفتح مستوى جديد!", Toast.LENGTH_SHORT).show();
                            //linearLayout.setEnabled(false);
                        }
                    }
                });

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }
        });

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists())
                        pointsTextView.setText(documentSnapshot.getString("points"));
                    else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setDialog (String titl, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(titl)
                .setIcon(icon);

        builder.setPositiveButton("حسناً", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //getActivity().finish();
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
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        type = documentSnapshot.getString("t"+mParam2);
                    } else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                    generalRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    levelList = (List<String>) documentSnapshot.get("levels");
                                    no_levels = Integer.parseInt(levelList.get(mParam2-1));
                                }
                                else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                            } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                            myCallback.onCallback(type);
                        }
                    });
                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface MyCallback {
        void onCallback(String t);
    }
}
