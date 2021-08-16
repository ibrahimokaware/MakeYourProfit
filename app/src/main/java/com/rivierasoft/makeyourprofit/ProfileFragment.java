package com.rivierasoft.makeyourprofit;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    LinearLayout nameLinearLayout, ageLinearLayout, genderLinearLayout, phoneLinearLayout, addressLinearLayout;
    RelativeLayout relativeLayout;
    ImageView photoImageView, add_photo, big_icon, small_icon;
    TextView nameTextView, ageTextView, genderTextView, phoneTextView, addressTextView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://make-your-profit.appspot.com");
    StorageReference spaceRef = storageRef.child("profileImages/"+firebaseUser.getUid()+".jpg");

    Drawable drawable;
    String gender;

    ProgressDialog progressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2, String param3, String param4, String param5) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        nameLinearLayout = view.findViewById(R.id.nameLinearLayout);
        ageLinearLayout = view.findViewById(R.id.ageLinearLayout);
        genderLinearLayout = view.findViewById(R.id.genderLinearLayout);
        phoneLinearLayout = view.findViewById(R.id.phoneLinearLayout);
        addressLinearLayout = view.findViewById(R.id.addressLinearLayout);
        nameTextView = view.findViewById(R.id.nameTextView);
        ageTextView = view.findViewById(R.id.ageTextView);
        genderTextView = view.findViewById(R.id.genderTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        addressTextView = view.findViewById(R.id.addressTextView);
        photoImageView = view.findViewById(R.id.photo);
        add_photo = view.findViewById(R.id.add_photo);
        relativeLayout = view.findViewById(R.id.icon_container);
        big_icon = view.findViewById(R.id.big_icon);
        small_icon = view.findViewById(R.id.small_icon);

        String url = "https://firebasestorage.googleapis.com/v0/b/make-your-profit.appspot.com/o/profileImages%2F"+firebaseUser.getUid()+".jpg?alt=media&token=a3d71cd4-1d54-4ddc-800a-63300b78b711";
        Picasso.with(getActivity())
                .load(url)
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(big_icon);

        nameTextView.setText(mParam1);
        ageTextView.setText(mParam2);
        genderTextView.setText(mParam3);
        phoneTextView.setText(mParam4);
        addressTextView.setText(mParam5);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("جار الحفظ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        small_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 2);*/
                //setDialog();
                //startActivity(new Intent(getActivity(), MainActivity2.class));
//                ImagePicker.Companion.with(getActivity())
//                        //.crop()
//                        .compress(1024)
//                        .maxResultSize(1080, 1080)
//                        .start();
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
                                progressDialog.show();
                                checkInternet();
                            }
                        })
                        .setOnPickCancel(new IPickCancel() {
                            @Override
                            public void onCancelClick() {

                            }
                        }).show(getActivity());
            }
        });

        nameLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameDialog(mParam1);
            }
        });

        ageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dobDialog();
            }
        });

        genderLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderDialog();
            }
        });

        phoneLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneDialog();
            }
        });

        addressLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressDialog();
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
                Toast.makeText(getActivity(),"لا يوجد اتصال بالإنترنت!",Toast.LENGTH_SHORT).show();
                //photoImageView.setImageDrawable(drawable);
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                update("photo?", "Yes");
                update("photo", "https://firebasestorage.googleapis.com/v0/b/make-your-profit.appspot.com/o/profileImages%2F"+firebaseUser.getUid()+".jpg?alt=media&token=a3d71cd4-1d54-4ddc-800a-63300b78b711");

                progressDialog.cancel();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public void nameDialog(final String name){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.name);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText nameEditText = dialog.findViewById(R.id.editTextTextPersonName);
        TextView saveTextView = dialog.findViewById(R.id.textView43);

        nameEditText.setText(name);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(nameEditText))
                    nameEditText.setError("أدخل الاسم!");
                else {
                    if (nameEditText.getText().toString().equals(nameTextView.getText().toString()))
                        Toast.makeText(getActivity(), "لم يتم إجراء أي تعديل!", Toast.LENGTH_SHORT).show();
                    else {
                        update("name", nameEditText.getText().toString());
                        nameTextView.setText(nameEditText.getText().toString());
                    }
                    dialog.cancel();
                }
            }
        });
        dialog.show();

    }

    public void dobDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dob);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final DatePicker datePicker = dialog.findViewById(R.id.datePicker2);
        TextView saveTextView = dialog.findViewById(R.id.textView43);

        if (ageTextView.getText().toString().equals("أدخل تاريخ الميلاد"))
            datePicker.updateDate(2000,0,1);
        else {
            String[] strings = ageTextView.getText().toString().split("/");
            int day = Integer.parseInt(strings[0]);
            int month = Integer.parseInt(strings[1]);
            int year = Integer.parseInt(strings[2]);
            datePicker.updateDate(year, month-1, day);
        }
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dob = datePicker.getDayOfMonth()+"/"+ (datePicker.getMonth() + 1)+"/"+datePicker.getYear();
                if (dob.equals(ageTextView.getText().toString()))
                    Toast.makeText(getActivity(), "لم يتم إجراء أي تعديل!", Toast.LENGTH_SHORT).show();
                else {
                    update("date_of_birth", dob);
                    ageTextView.setText(dob);
                }
                dialog.cancel();
            }
        });
        dialog.show();

    }

    public void genderDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.gender);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final RadioButton maleRadioButton = dialog.findViewById(R.id.radioButton);
        final RadioButton femaleRadioButton = dialog.findViewById(R.id.radioButton2);
        TextView saveTextView = dialog.findViewById(R.id.textView43);

        if (genderTextView.getText().toString().equals("ذكر"))
            maleRadioButton.setChecked(true);
        else if (genderTextView.getText().toString().equals("أنثى"))
            femaleRadioButton.setChecked(true);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maleRadioButton.isChecked() || femaleRadioButton.isChecked()) {
                    if (maleRadioButton.isChecked())
                        gender = "ذكر";
                    else gender = "أنثى";
                    if (gender.equals(genderTextView.getText().toString()))
                        Toast.makeText(getActivity(), "لم يتم إجراء أي تعديل!", Toast.LENGTH_SHORT).show();
                    else {
                        update("gender", gender);
                        genderTextView.setText(gender);
                    }
                    dialog.cancel();
                } else {
                    Toast.makeText(getActivity(),"اختر النوع!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

    }

    public void phoneDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.phone);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText phoneEditText = dialog.findViewById(R.id.editTextPhone);
        TextView saveTextView = dialog.findViewById(R.id.textView43);
        if (!phoneTextView.getText().toString().equals("أدخل رقم الهاتف"))
            phoneEditText.setText(phoneTextView.getText().toString());
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(phoneEditText))
                    phoneEditText.setError("أدخل رقم الهاتف!");
                else if (phoneEditText.getText().toString().length() < 5)
                    phoneEditText.setError("الرجاء إدخال رقم هاتف صحيح!");
                else {
                    if (phoneTextView.getText().toString().equals(phoneEditText.getText().toString()))
                        Toast.makeText(getActivity(), "لم يتم إجراء أي تعديل!", Toast.LENGTH_SHORT).show();
                    else {
                        update("phone", phoneEditText.getText().toString());
                        phoneTextView.setText(phoneEditText.getText().toString());
                    }
                    dialog.cancel();
                }
            }
        });
        dialog.show();

    }

    public void addressDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.address);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextInputEditText countryInputEditText = dialog.findViewById(R.id.input_country);
        final TextInputEditText stateInputEditText = dialog.findViewById(R.id.input_state);
        final TextInputEditText cityInputEditText = dialog.findViewById(R.id.input_city);
        final TextInputEditText streetInputEditText = dialog.findViewById(R.id.input_street);
        TextView saveTextView = dialog.findViewById(R.id.textView43);

        if (!addressTextView.getText().toString().equals("أدخل العنوان")) {
            String[] strings = addressTextView.getText().toString().split("، ");
            String country = strings[0];
            String state = strings[1];
            String city = strings[2];
            String street = strings[3];
            countryInputEditText.setText(country);
            stateInputEditText.setText(state);
            cityInputEditText.setText(city);
            streetInputEditText.setText(street);
        }
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( isEmpty(countryInputEditText) || isEmpty(stateInputEditText) || isEmpty(cityInputEditText) || isEmpty(streetInputEditText)) {
                    if (isEmpty(countryInputEditText))
                        countryInputEditText.setError(getString(R.string.complete_field));
                    if (isEmpty(stateInputEditText))
                        stateInputEditText.setError(getString(R.string.complete_field));
                    if (isEmpty(cityInputEditText))
                        cityInputEditText.setError(getString(R.string.complete_field));
                    if (isEmpty(streetInputEditText))
                        streetInputEditText.setError(getString(R.string.complete_field));
                } else {
                    String address = countryInputEditText.getText().toString()+ "، " +
                            stateInputEditText.getText().toString()+ "، " + cityInputEditText.getText().toString()+ "، " +
                            streetInputEditText.getText().toString();
                    if (address.equals(addressTextView.getText().toString()))
                        Toast.makeText(getActivity(), "لم يتم إجراء أي تعديل!", Toast.LENGTH_SHORT).show();
                    else {
                        update("address", address);
                        addressTextView.setText(address);
                    }
                    dialog.cancel();
                }
            }
        });
        dialog.show();

    }


    private boolean isEmpty(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString()))
            return true;

        return false;
    }

    public void update(String variable, String value) {
        docRef.update(variable, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "تم الحفظ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                    }
                });
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
//        if (requestCode == RESULT_OK) {
//            Uri selectedImage = imageReturnedIntent.getData();
//            big_icon.setImageURI(selectedImage);
//        } else if (requestCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(getContext(), ImagePicker.RESULT_ERROR+"", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
//        }
//        drawable = big_icon.getDrawable();
//        switch(requestCode) {
//            case 1:
//                if (requestCode == 1 && resultCode == RESULT_OK) {
//                    Bundle extras = imageReturnedIntent.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
//                    big_icon.setImageBitmap(imageBitmap);
//                    progressDialog.show();
//                    checkInternet();
//                }
//                break;
//            case 2:
//                if(resultCode == RESULT_OK){
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    big_icon.setImageURI(selectedImage);
//                    if (!areDrawablesIdentical(drawable, big_icon.getDrawable())) {
//                        //Toast.makeText(getActivity(), "جار الحفظ...", Toast.LENGTH_SHORT).show();
//                        progressDialog.show();
//                        checkInternet();
//                    } else Toast.makeText(getActivity(), "تم اختيار نفس الصورة!", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
    }

    public void checkInternet() {
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
                            uploadPhoto();
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
}