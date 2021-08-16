package com.rivierasoft.makeyourprofit;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
 * Use the {@link PointsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointsFragment extends Fragment {

    TextView todayPoints, text_view_progress, text_view_progress2;
    int points, check_amount = 0, check_amount2 = 500;
    String today_points;
    ProgressBar progressBar, progress_bar, progress_bar2;
    ConstraintLayout constraintLayout;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PointsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PointsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PointsFragment newInstance(String param1, String param2) {
        PointsFragment fragment = new PointsFragment();
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
        return inflater.inflate(R.layout.fragment_points, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        todayPoints = view.findViewById(R.id.textView42);
        progress_bar = view.findViewById(R.id.progress_bar);
        progress_bar2 = view.findViewById(R.id.progress_bar2);
        text_view_progress = view.findViewById(R.id.text_view_progress);
        text_view_progress2 = view.findViewById(R.id.text_view_progress2);
        progressBar = view.findViewById(R.id.progressBar);
        constraintLayout = view.findViewById(R.id.constraintLayout);

        constraintLayout.setVisibility(View.GONE);

        @SuppressLint("SimpleDateFormat") final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        points = Integer.parseInt(documentSnapshot.getString("points"));
                        today_points = documentSnapshot.getString("today_points");
                    } else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);

                    String[] strings = today_points.split("/");
                    String p = strings[0];
                    String t = strings[1];
                    if (dateFormat.format(Calendar.getInstance().getTime()).equals(t))
                        todayPoints.setText(""+p);
                    else todayPoints.setText("0");

                    progress_bar.setProgress(points/10);
                    progress_bar2.setProgress(points/10);
                    text_view_progress.setText(points+"");
                    text_view_progress2.setText(1000-points+"");
                    if (points>=1000) {
                        text_view_progress2.setText(0+"");
                    }

                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*public void increment() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check_amount = check_amount + 1;// increment by 100
                currentPoints.setText("" + check_amount); // show continues incrementing value
                if (check_amount != points) {
                    increment();
                }
            }
        }, 10); //1 ms for fast incrementing
    }

    public void decrement() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check_amount2 = check_amount2 - 1;// increment by 100
                remainingPoints.setText("" + check_amount2); // show continues incrementing value
                if (check_amount2 != 1000-points) {
                    decrement();
                }
            }
        }, 10); //1 ms for fast incrementing
    }*/
}