package com.rivierasoft.makeyourprofit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    DateFormat dateFormat;

    ImageView photo, add_photo, big_icon, small_icon;
    Button create;
    TextInputEditText nameInputEditText, emailInputEditText, passwordInputEditText;
    TextView login;
    FirebaseAuth mAuth;

    String email, password;

    ProgressDialog progressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersReference = db.collection("users");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://make-your-profit.appspot.com");
    StorageReference spaceRef;

    Map<String, Object> user;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateFragment newInstance(String param1, String param2) {
        CreateFragment fragment = new CreateFragment();
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
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("login" , MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        big_icon = view.findViewById(R.id.big_icon);
        small_icon = view.findViewById(R.id.small_icon);
        login = view.findViewById(R.id.login_tv);
        create = view.findViewById(R.id.create_bt);
        nameInputEditText = view.findViewById(R.id.input_name);
        emailInputEditText = view.findViewById(R.id.input_email);
        passwordInputEditText = view.findViewById(R.id.input_password);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("جار إنشاء الحساب...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        small_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()
                        .setTitle("تحديد صورة")
                        .setCameraButtonText("التقط صورة")
                        .setGalleryButtonText("اختر من المعرض")
                        .setCancelText("إلغاء")
                        .setProgressText("جار التحميل...")
                        .setIconGravity(Gravity.RIGHT))
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                big_icon.setImageURI(r.getUri());
                            }
                        })
                        .setOnPickCancel(new IPickCancel() {
                            @Override
                            public void onCancelClick() {

                            }
                        }).show(getActivity());
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        @SuppressLint("SimpleDateFormat")
        final DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isReady = true;

                if ( isEmpty(nameInputEditText) || isEmpty(emailInputEditText) || isEmpty(passwordInputEditText)) {
                    if (isEmpty(nameInputEditText))
                        nameInputEditText.setError("أدخل الاسم!");
                    if (isEmpty(emailInputEditText))
                        emailInputEditText.setError("أدخل بريد إلكتروني!");
                    if (isEmpty(passwordInputEditText))
                        passwordInputEditText.setError("أدخل كلمة مرور!");
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
                        email = emailInputEditText.getText().toString();
                        password = passwordInputEditText.getText().toString();
                        user = new HashMap<>();
                        user.put("name", nameInputEditText.getText().toString());
                        user.put("email", email);
                        user.put("password", password);
                        user.put("date_and_time", df.format(Calendar.getInstance().getTime()));
                        if (areDrawablesIdentical(getResources().getDrawable(R.drawable.ic_account), big_icon.getDrawable())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            builder.setMessage("لم تقم بتحديد صورة للملف الشخصي، سيتم استخدام الصورة الافتراضية مع إمكانية تغييرها لاحقاً في الإعدادات.\nهل أنت موافق؟")
                                    .setTitle("صورة الملف الشخصي")
                                    .setIcon(R.drawable.ic_account);

                            builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //progressDialog.show();

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
                                                        signupUser();
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

//                                    mAuth.signInWithEmailAndPassword("ahmedmohd8822@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<AuthResult> task) {
//                                            if (task.isSuccessful()) {
//                                                signupUser();
//                                            } else {
//                                                progressDialog.cancel();
//                                                setDialog("لا يوجد اتصال بالإنترنت!", "الرجاء التحقق من الاتصال بالإنترنت ثم إعادة المحاولة", R.drawable.ic_wifi_off);
//                                            }
//                                        }
//                                    });
                                }
                            });

                            builder.setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            dialog.show();
                        } else {
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
                                                signupUser();
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

    private void signupUser() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    spaceRef = storageRef.child("profileImages/"+firebaseUser.getUid()+".jpg");
                    user.put("UID", firebaseUser.getUid());
                    user.put("points", "0");
                    user.put("p", 0);
                    user.put("today_points", "0/"+dateFormat.format(Calendar.getInstance().getTime()));
                    user.put("withdraw", false);
                    user.put("withdraw_date", "0");
                    user.put("winner", false);
                    user.put("times", "0");
                    user.put("t1", "1");
                    user.put("t2", "1");
                    user.put("t3", "1");
                    user.put("t4", "1");
                    user.put("t5", "1");
                    user.put("t6", "1");
                    user.put("t7", "1");
                    user.put("t8", "1");
                    user.put("t9", "1");
                    user.put("t10", "1");
                    user.put("phone", "أدخل رقم الهاتف");
                    user.put("date_of_birth", "أدخل تاريخ الميلاد");
                    user.put("gender", "أدخل النوع");
                    user.put("address", "أدخل العنوان");
                    user.put("notification", true);
                    user.put("ads", false);
                    //String[] strings = new String[3];
                    //for (int i=1; i<=3; i++)
                      //  strings[i-1] = i+"-0";
                    //user.put("stories", Arrays.asList(strings));
                    addNewUser();
                    //startActivity(new Intent(getActivity(),MainActivity.class));
                    /*Toast.makeText(getActivity(), "Authentication success.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    progressDialog.cancel();
                    setDialog("انتباه!", "البريد الإلكتروني المستخدم موجود مسبقاً، حاول القيام بتسجيل الدخول بدلاً من إنشاء حساب جديد", R.drawable.ic_warning);
                }
            }
        });
    }

    public void addNewUser () {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (!areDrawablesIdentical(getResources().getDrawable(R.drawable.ic_account), big_icon.getDrawable())) {
            user.put("photo?", "Yes");
            user.put("photo", "https://firebasestorage.googleapis.com/v0/b/make-your-profit.appspot.com/o/profileImages%2F"+firebaseUser.getUid()+".jpg?alt=media&token=a3d71cd4-1d54-4ddc-800a-63300b78b711");
        } else {
            user.put("photo?", "No");
            user.put("photo", "https://firebasestorage.googleapis.com/v0/b/make-your-profit.appspot.com/o/profileImages%2F"+firebaseUser.getUid()+".jpg?alt=media&token=a3d71cd4-1d54-4ddc-800a-63300b78b711");
        }
        usersReference
                .document(firebaseUser.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (!areDrawablesIdentical(getResources().getDrawable(R.drawable.ic_account), big_icon.getDrawable())) {
                            uploadPhoto();
                        } else {
                            editor.putInt("status",0);
                            editor.apply();
                            startActivity(new Intent(getActivity(), ContainerActivity.class));
                            progressDialog.cancel();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getActivity(), "Error adding document", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void uploadPhoto () {
        // Get the data from an ImageView as bytes
        big_icon.setDrawingCacheEnabled(true);
        big_icon.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) big_icon.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = spaceRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                editor.putInt("status", 0);
                editor.apply();
                startActivity(new Intent(getActivity(), ContainerActivity.class));
                progressDialog.cancel();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public void setDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("تحديد صورة")
                .setIcon(R.drawable.ic_baseline_insert_photo)
                .setItems(R.array.select_photo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                try {
                                    startActivityForResult(takePictureIntent, 1);
                                } catch (ActivityNotFoundException e) {
                                    // display error state to the user
                                }
                                break;
                            case 1: Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pickPhoto , 2);
                                break;
                            case 3: dialog.cancel();
                                break;
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 1:
                if (requestCode == 1 && resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    big_icon.setImageBitmap(imageBitmap);
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    big_icon.setImageURI(selectedImage);
                }
                break;
        }
    }
}