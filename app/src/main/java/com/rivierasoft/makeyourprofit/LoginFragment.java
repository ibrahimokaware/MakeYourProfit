package com.rivierasoft.makeyourprofit;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button login, create;
    TextInputEditText emailInputEditText, passwordInputEditText;
    TextView forget;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog, progressDialog2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("login" , MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        login = view.findViewById(R.id.login_bt);
        create = view.findViewById(R.id.create_bt);
        emailInputEditText = view.findViewById(R.id.input_email);
        passwordInputEditText = view.findViewById(R.id.input_password);
        forget = view.findViewById(R.id.forget_password);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("جار تسجيل الدخول...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        progressDialog2 = new ProgressDialog(getActivity());
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("جار الإرسال...");
        progressDialog2.setCanceledOnTouchOutside(false);
        progressDialog2.setCancelable(false);
        progressDialog2.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPasswordDialog();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //email = emailInputEditText.getText().toString();
                //password = passwordInputEditText.getText().toString();
                /*if (strUserName.trim().equals("")) {   //OR .length() == 0
                    if(TextUtils.isEmpty(strUserName)) {
                }*/

                boolean isReady = true;

                if ( isEmpty(emailInputEditText) || isEmpty(passwordInputEditText)) {
                    if (isEmpty(emailInputEditText))
                        emailInputEditText.setError("أدخل البريد الإلكتروني!");
                    if (isEmpty(passwordInputEditText))
                        passwordInputEditText.setError("أدخل كلمة المرور!");
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(emailInputEditText.getText().toString()).matches()) {
                        emailInputEditText.setError("الرجاء إدخال عنوان بريد إلكتروني صحيح!");
                        isReady = false;
                    }
                    if (passwordInputEditText.getText().toString().length() < 6) {
                        passwordInputEditText.setError("يجب أن تتكون كلمة المرور من 6 أحرف على الأقل!");
                        isReady = false;
                    }
                    if (passwordInputEditText.getText().toString().length() > 20) {
                        passwordInputEditText.setError("يجب ألا تتجاوز كلمة المرور أكثر من 20 حرفاً!");
                        isReady = false;
                    }
                    if (isReady) {
                        Thread thread = new Thread(new Runnable() {
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
                                            loginUser(emailInputEditText.getText().toString(), passwordInputEditText.getText().toString());
                                        }
                                    }
                                });
                            }
                        });

                        progressDialog.show();

                        if (Connectivity.isConnected(getActivity()).equals("yes"))
                            thread.start();
                        else {
                            progressDialog.cancel();
                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                        }
                    }
                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateActivity.class));
            }
        });
    }

    private void loginUser(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    //Toast.makeText(getApplicationContext(), "Authentication success", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                    editor.putInt("status",0);
                    editor.apply();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                } else {
                    progressDialog.cancel();
                    setDialog("خطأ!", "تحقق من البريد الإلكتروني وكلمة المرور", R.drawable.ic_error);
                }
            }
        });
    }

    public void setDialog (String title, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(title)
                .setIcon(icon);

        builder.setPositiveButton("حسناً", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private boolean isEmpty(TextInputEditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString()))
            return true;

        return false;
    }

    public void forgetPasswordDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.forget_password);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextInputEditText emailInputEditText = dialog.findViewById(R.id.input_email);
        Button sendButton = dialog.findViewById(R.id.send_bt);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isReady = true;
                if (isEmpty(emailInputEditText))
                    emailInputEditText.setError("أدخل البريد الإلكتروني!");
                else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(emailInputEditText.getText().toString()).matches()) {
                        emailInputEditText.setError("الرجاء إدخال عنوان بريد إلكتروني صحيح!");
                        isReady = false;
                    }
                    if (isReady) {
                        //progressDialog2.show();

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String finalRes =  Connectivity.executeCmd();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalRes.equals("no")) {
                                            progressDialog2.cancel();
                                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                                        } else {
                                            mAuth.sendPasswordResetEmail(emailInputEditText.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog2.cancel();
                                                                dialog.cancel();
                                                                setDialog2("تم الإرسال بنجاح", "قم بمراجعة البريد الإلكتروني واتبع التعليمات لإعادة تعيين كلمة المرور", R.drawable.ic_baseline_done);
                                                            } else {
                                                                progressDialog2.cancel();
                                                                setDialog("خطأ!", "البريد الإلكتروني الذي قمت بإدخاله غير مرتبط بأي حساب، حاول إدخال بريد إلكتروني آخر.", R.drawable.ic_error);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                        });

                        progressDialog2.show();

                        if (Connectivity.isConnected(getActivity()).equals("yes"))
                            thread.start();
                        else {
                            progressDialog.cancel();
                            setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
                        }

//                        mAuth.signInWithEmailAndPassword("ahmedmohd8822@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    mAuth.sendPasswordResetEmail(emailInputEditText.getText().toString())
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        progressDialog2.cancel();
//                                                        setDialog2("تم الإرسال بنجاح", "قم بمراجعة البريد الإلكتروني واتبع التعليمات لإعادة تعيين كلمة المرور", R.drawable.ic_baseline_done);
//                                                    } else {
//                                                        progressDialog2.cancel();
//                                                        setDialog("خطأ!", "البريد الإلكتروني الذي قمت بإدخاله غير مرتبط بأي حساب، حاول إدخال بريد إلكتروني آخر.", R.drawable.ic_error);
//                                                    }
//                                                }
//                                            });
//                                } else {
//                                    progressDialog.cancel();
//                                    setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
//                                }
//                            }
//                        });
                    }
                }
            }
        });

        dialog.show();

    }

    public void setDialog2 (String title, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(title)
                .setIcon(icon);

        builder.setPositiveButton("فتح البريد الإلكتروني", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mail.google.com/")));
                }
            }
        });

        builder.setNegativeButton("تم", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


}