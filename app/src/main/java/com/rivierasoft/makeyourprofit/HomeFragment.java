package com.rivierasoft.makeyourprofit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    TextView pointsTextView;

    RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pointsTextView = getActivity().findViewById(R.id.pointsTextView);
        recyclerView = view.findViewById(R.id.recyclerView);

        final ArrayList<Profit> profits = new ArrayList<>();
        profits.add(new Profit(R.drawable.image_category_knowledge_raster, "أسئلة ثقافية"));
        profits.add(new Profit(R.drawable.mosque, "أسئلة إسلامية"));
        profits.add(new Profit(R.drawable.image_category_geography_raster, "أسئلة جغرافية"));
        profits.add(new Profit(R.drawable.image_category_sports_raster, "أسئلة رياضية"));
        profits.add(new Profit(R.drawable.flag, "أعلام الدول"));
        profits.add(new Profit(R.drawable.image_category_history_raster, "معالم ومدن"));
        profits.add(new Profit(R.drawable.athlete, "من اللاعب"));
        profits.add(new Profit(R.drawable.club, "خمن النادي"));
        profits.add(new Profit(R.drawable.people, "الفرق بين صورتين"));
        profits.add(new Profit(R.drawable.apple, "الشعار الصحيح"));
        profits.add(new Profit(R.drawable.squares, "لعبة مطابقة الصور"));
        profits.add(new Profit(R.drawable.tic_tac_toe, "لعبة XO"));

        ProfitAdapter adapter = new ProfitAdapter(profits, new OnProfitRecyclerViewItemClickListener() {
            @Override
            public void OnClickListener(int p) {
                if (p == 11)
                    startActivity(new Intent(getActivity(), QuizActivity.class).putExtra("title", "لعبة XO")
                            .putExtra("type", 12).putExtra("level", 1));
                else startActivity(new Intent(getActivity(), LevelsActivity.class).putExtra("title", profits.get(p).getName())
                            .putExtra("type",p+1).putExtra("back", 1));
            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}