package com.rivierasoft.makeyourprofit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpFragment extends Fragment {

    EditText problemEditText;
    ImageView screenImageView, screenImageView2, imageView, imageView2;
    Button sendButton;
    boolean p1 = false, p2 = false;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference helpReference = db.collection("help");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://make-your-profit.appspot.com");
    StorageReference spaceRef;
    StorageReference spaceRef2;

    ProgressDialog progressDialog;

    Map<String, Object> help;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HelpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HelpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpFragment newInstance(String param1, String param2) {
        HelpFragment fragment = new HelpFragment();
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
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        problemEditText = view.findViewById(R.id.editTextTextMultiLine);
        screenImageView = view.findViewById(R.id.imageView30);
        screenImageView2 = view.findViewById(R.id.imageView32);
        imageView = view.findViewById(R.id.imageView31);
        imageView2 = view.findViewById(R.id.imageView33);
        sendButton = view.findViewById(R.id.send_bt);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("جار الإرسال...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        help = new HashMap<>();

        screenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
            }
        });

        screenImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 2);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(problemEditText))
                    Toast.makeText(getActivity(), "اكتب المشكلة الخاصة بك!", Toast.LENGTH_SHORT).show();
                else {
                    help.put("problem", problemEditText.getText().toString());
                    help.put("UID", firebaseUser.getUid());
                    if (!areDrawablesIdentical(getResources().getDrawable(R.drawable.linear_layout_background), screenImageView.getDrawable())) {
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
                        help.put("photo1", "Yes");
                        p1 = true;
                    } else {
                        help.put("photo1", "No");
                        p1 = false;
                    }
                    if (!areDrawablesIdentical(getResources().getDrawable(R.drawable.linear_layout_background), screenImageView2.getDrawable())) {
                        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
                        help.put("photo2", "Yes");
                        p2 = true;
                    } else {
                        help.put("photo2", "No");
                        p2 = false;
                    }
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
                                        if (!p1 && !p2) {
                                            sendProblem();
                                        } else if (p1 && p2) {
                                            sendProblem2();
                                        } else if (p1) {
                                            sendProblem3();
                                        } else if (p2) {
                                            sendProblem4();
                                        }
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
        });
    }

    private boolean isEmpty(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString()))
            return true;

        return false;
    }

    public void uploadPhoto () {
        // Get the data from an ImageView as bytes
        screenImageView.setDrawingCacheEnabled(true);
        screenImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) screenImageView.getDrawable()).getBitmap();
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
                uploadPhoto2();
            }
        });
    }

    public void uploadPhoto2 () {
        // Get the data from an ImageView as bytes
        screenImageView2.setDrawingCacheEnabled(true);
        screenImageView2.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) screenImageView2.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = spaceRef2.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.cancel();
                setDialog2("تم الإرسال بنجاح", "تم إرسال المشكلة بنجاح وسيتم حلها في أسرع وقت", R.drawable.ic_baseline_done);
            }
        });
    }

    public void uploadPhoto3 () {
        // Get the data from an ImageView as bytes
        screenImageView.setDrawingCacheEnabled(true);
        screenImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) screenImageView.getDrawable()).getBitmap();
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
                progressDialog.cancel();
                setDialog2("تم الإرسال بنجاح", "تم إرسال المشكلة بنجاح وسيتم حلها في أسرع وقت", R.drawable.ic_baseline_done);
            }
        });
    }

    public void sendProblem() {
        helpReference
                .add(help).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.cancel();
                        setDialog2("تم الإرسال بنجاح", "تم إرسال المشكلة بنجاح وسيتم حلها في أسرع وقت", R.drawable.ic_baseline_done);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void sendProblem2() {
        helpReference
                .add(help).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                spaceRef = storageRef.child("HelpImages/"+documentReference.getId()+"-P1.jpg");
                spaceRef2 = storageRef.child("HelpImages/"+documentReference.getId()+"-P2.jpg");
                uploadPhoto();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void sendProblem3() {
        helpReference
                .add(help).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                spaceRef = storageRef.child("HelpImages/"+documentReference.getId()+"-P1.jpg");
                uploadPhoto3();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void sendProblem4() {
        helpReference
                .add(help).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                spaceRef2 = storageRef.child("HelpImages/"+documentReference.getId()+"-P2.jpg");
                uploadPhoto2();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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

    public void setDialog2 (String title, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(title)
                .setIcon(icon);

        builder.setPositiveButton("تم", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                getActivity().finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
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
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    screenImageView.setImageURI(selectedImage);
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    screenImageView2.setImageURI(selectedImage);
                }
                break;
        }
        if (!areDrawablesIdentical(getResources().getDrawable(R.drawable.linear_layout_background), screenImageView.getDrawable())) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
        }
        if (!areDrawablesIdentical(getResources().getDrawable(R.drawable.linear_layout_background), screenImageView2.getDrawable())) {
            imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
        }
    }
}