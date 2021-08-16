package com.rivierasoft.makeyourprofit;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String facebookS, facebookLink, instagramS, twitterS, youtubeS, playStore, message;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference generalRef = db.collection("questions").document("000General111");

    ImageView photo, badge;
    TextView nameTextView, completeTextView;

    LinearLayout profileLinearLayout, instructionsLinearLayout, agendaLinearLayout, inviteLinearLayout, helpLinearLayout, followLinearLayout,
            rateLinearLayout, aboutLinearLayout, logOutLinearLayout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;
    private boolean mParam6;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2, String param3, String param4, String param5, boolean param6) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putBoolean(ARG_PARAM6, param6);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
            mParam6 = getArguments().getBoolean(ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("login" , MODE_PRIVATE);
        editor = sharedPreferences.edit();

        photo = view.findViewById(R.id.profileImageView);
        badge = view.findViewById(R.id.badge);

        nameTextView = view.findViewById(R.id.nameTextView);
        completeTextView = view.findViewById(R.id.completeProfileTextView);

        nameTextView.setText(mParam1);

        if (mParam6)
            badge.setVisibility(View.VISIBLE);
        else badge.setVisibility(View.GONE);

        String url = "https://firebasestorage.googleapis.com/v0/b/make-your-profit.appspot.com/o/profileImages%2F"+firebaseUser.getUid()+".jpg?alt=media&token=a3d71cd4-1d54-4ddc-800a-63300b78b711";
        Picasso.with(getActivity())
                .load(url)
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(photo);

        profileLinearLayout = view.findViewById(R.id.profileLinearLayout);
        instructionsLinearLayout = view.findViewById(R.id.instructionsLinearLayout);
        agendaLinearLayout = view.findViewById(R.id.agendaLinearLayout);
        inviteLinearLayout = view.findViewById(R.id.inviteLinearLayout);
        helpLinearLayout = view.findViewById(R.id.helpLinearLayout);
        followLinearLayout = view.findViewById(R.id.followLinearLayout);
        rateLinearLayout = view.findViewById(R.id.rateLinearLayout);
        aboutLinearLayout = view.findViewById(R.id.aboutLinearLayout);
        logOutLinearLayout = view.findViewById(R.id.logOutLinearLayout);

        generalRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        facebookS = documentSnapshot.getString("facebook");
                        facebookLink = documentSnapshot.getString("facebook2");
                        instagramS = documentSnapshot.getString("instagram");
                        twitterS = documentSnapshot.getString("twitter");
                        youtubeS = documentSnapshot.getString("youtube");
                        playStore = documentSnapshot.getString("play");
                        message = documentSnapshot.getString("message");
                    } else Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();

                } else Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        profileLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class).putExtra("s", 1).putExtra("name", mParam1)
                        .putExtra("dob", mParam2).putExtra("gender", mParam3).putExtra("phone", mParam4).putExtra("address", mParam5));
            }
        });

        instructionsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class).putExtra("s", 2));
            }
        });

        agendaLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class).putExtra("s", 3));
            }
        });

        helpLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class).putExtra("s", 4));
            }
        });

        followLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class).putExtra("s", 5));
            }
        });

        inviteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), SettingsActivity.class).putExtra("s", 4));
                shareDialog();
            }
        });

        rateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.android.chrome")));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(playStore));
                    startActivity(intent);
                } catch (Exception ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStore)));
                }
            }
        });

        logOutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog("تسجيل الخروج!", "هل تريد بالتأكيد تسجيل الخروج؟", R.drawable.ic_warning);
            }
        });
    }

    public void setDialog (String title, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(title)
                .setIcon(icon);

        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                editor.putInt("status",1);
                editor.apply();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        builder.setNegativeButton("لا", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
    }


    public void followDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.follow);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView cancelTextView = dialog.findViewById(R.id.cancelTextView);
        ImageView facebook = dialog.findViewById(R.id.facebook_iv);
        final ImageView twitter = dialog.findViewById(R.id.twitter_iv);
        ImageView instagram = dialog.findViewById(R.id.instagram_iv);
        ImageView youtube = dialog.findViewById(R.id.youtube_iv);

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookS)));
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100009703926502")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookLink)));
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(" https://www.facebook.com/profile.php?id=100009703926502")));
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(twitterS));
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterS)));
                }
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("com.instagram.android");
                    intent.setData(Uri.parse(instagramS));
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(instagramS)));
                }
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(youtubeS));
                    //intent.setPackage("com.google.android.youtube");
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeS)));
                }
            }
        });

        dialog.show();

    }

    public void shareDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.share);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView cancelTextView = dialog.findViewById(R.id.cancelTextView);
        ImageView gmail = dialog.findViewById(R.id.gmail_iv);
        ImageView whatsapp = dialog.findViewById(R.id.whatsapp_iv);
        ImageView messenger = dialog.findViewById(R.id.messenger_iv);

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_EMAIL, "");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "دعوة صديق");
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    intent.setPackage("com.google.android.gm");
                    intent.setType("message/rfc822");
                    //intent.setData(Uri.parse("mailto:your.email@gmail.com"));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "التطبيق غير مثبت!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(whatsappIntent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "التطبيق غير مثبت!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.facebook.orca");
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "التطبيق غير مثبت!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();

    }
}