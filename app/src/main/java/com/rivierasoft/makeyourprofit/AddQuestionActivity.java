package com.rivierasoft.makeyourprofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddQuestionActivity extends AppCompatActivity {

    // Write a message to the database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();

    //Spinner typeSpinner, levelSpinner;
    TextInputEditText tl, type;
    Button add, button50, button100;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference QuestionReference = db.collection("questions");
    private CollectionReference notebookRef = db.collection("users");
    Map<String, Object> question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        tl = findViewById(R.id.tl);
        type = findViewById(R.id.type);
        add = findViewById(R.id.add);
        button50 = findViewById(R.id.button50);
        button100 = findViewById(R.id.button100);

        /*ArrayList<String> typesList = new ArrayList<>();
        typesList.add("النوع");
        for (int i=1; i<22; i++)
            typesList.add(i+"");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.spinner_item_dropdown_design, typesList );
        typeSpinner.setAdapter(adapter);

        ArrayList<String> levelsList = new ArrayList<>();
        levelsList.add("المستوى");
        for (int i=1; i<=50; i++)
            levelsList.add(i+"");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.spinner_item_dropdown_design, levelsList );
        levelSpinner.setAdapter(adapter2);*/

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notebookRef.whereEqualTo("ads", true)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference docRef = db.collection("users").document(document.getString("UID"));
                                docRef.update("ads", false)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                notebookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if (document.getId().equals(firebaseUser.getUid()))
//                                    Toast.makeText(getApplicationContext(), document.getString("name"), Toast.LENGTH_SHORT).show();
////                                if (!document.getBoolean("ads")) {
////                                    db.collection("users").document(document.getString("UID"))
////                                            .update("ads", true)
////                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
////                                                @Override
////                                                public void onSuccess(Void aVoid) {
////                                                    //Toast.makeText(getActivity(), "تم الحفظ", Toast.LENGTH_SHORT).show();
////                                                }
////                                            })
////                                            .addOnFailureListener(new OnFailureListener() {
////                                                @Override
////                                                public void onFailure(@NonNull Exception e) {
////                                                    //Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
////                                                }
////                                            });
////                                }
//                            }
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                question = new HashMap<>();
//                question.put("tl", tl.getText().toString());
//                question.put("type", type.getText().toString());
//                question.put("image", "");
//                question.put("orientation", "");
//                question.put("answer", "");
//                question.put("i", "");
//                question.put("s", "");
//                question.put("c1", "");
//                question.put("c2", "");
//                question.put("c3", "");
//                question.put("c4", "");
//                question.put("text", "");
//                question.put("choice1", "");
//                question.put("choice2", "");
//                question.put("choice3", "");
//                question.put("choice4", "");
//                question.put("answer", "");
//                question.put("image", "");

//                QuestionReference.add(question)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Toast.makeText(getApplicationContext(), "تم إضافة السؤال بنجاح.", Toast.LENGTH_SHORT).show();
//                                //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getApplicationContext(), "Error adding document", Toast.LENGTH_SHORT).show();
//                                //Log.w(TAG, "Error adding document", e);
//                            }
//                        });
            }
        });

        button50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notebookRef.whereGreaterThanOrEqualTo("p", 500)
                        .whereLessThan("p", 1000)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(getApplicationContext(), document.getString("name"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        button100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notebookRef.whereGreaterThanOrEqualTo("p", 1000)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(getApplicationContext(), document.getString("name"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void reset() {
        notebookRef.whereEqualTo("winner", "Yes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = db.collection("users").document(document.getString("UID"));
                        docRef.update("points", "100", "p", 100, "withdraw", "No", "winner", "No")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        notebookRef.whereEqualTo("withdraw", "Yes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = db.collection("users").document(document.getString("UID"));
                        int points = Integer.parseInt(document.getString("points"));
                        if (points < 750) {
                            docRef.update("points", (points-250)+"", "p", points-250, "withdraw", "No")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(getActivity(), "تم الحفظ", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            docRef.update("points", (points-250)+"", "p", points-250)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(getActivity(), "تم الحفظ", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(getActivity(), "سيتم الحفظ عند توفر الإنترنت.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setWithdraw() {
        notebookRef.whereGreaterThanOrEqualTo("p", 500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Toast.makeText(getApplicationContext(), document.getString("name"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}