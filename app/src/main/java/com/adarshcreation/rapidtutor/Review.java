package com.adarshcreation.rapidtutor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Review extends AppCompatActivity {

    Dialog myDailog;

    String name,uid,r_uid;
    TextView Name;
    EditText Content;
    RatingBar ratingBar;
Button send,cancel;
    String meritM;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        myDailog = new Dialog(this);

        myDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDailog.show();

        Intent i = getIntent();
        name = i.getStringExtra("name");
        uid = i.getStringExtra("uid");
        r_uid = i.getStringExtra("r_uid");


        Content = findViewById(R.id.review_content);
        Name = findViewById(R.id.review_tutor_name);
        ratingBar = findViewById(R.id.review_star);
        send = findViewById(R.id.send_review);
        cancel = findViewById(R.id.cancel_review);



        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean flag=true;

                if (ratingBar.getRating() == 0)
                {
                    Toast.makeText(getApplicationContext(),"select star rating",Toast.LENGTH_SHORT).show();
                    flag =false;
                }
                if (Content.getText().toString().isEmpty())
                {
                    flag=false;
                    Toast.makeText(getApplicationContext(),"give comment",Toast.LENGTH_SHORT).show();
                }

                if (flag)
                {
                    getMerit(new MyCallback() {
                        @Override
                        public void onCallback(final String m) {
                            Log.d("TAG", m);

                            getRating(new MyCallback() {
                                @Override
                                public void onCallback(final String r) {
                                    Log.d("TAG", r);

                                    addReview(m,r);
                                }

                            });
                        }

                    });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Review.this, LearnerHome.class);
                startActivity(intent);
            }
        });
    }
    public void getMerit(final MyCallback myCallback)
    {
        DocumentReference docRef = db.collection("user").document(r_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String merit;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        merit = document.getString("merit");

                        myCallback.onCallback(merit);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public void getRating(final MyCallback myCallback)
    {
        DocumentReference docRef = db.collection("user").document(r_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String rating;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        rating = document.getString("rating");

                        myCallback.onCallback(rating);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public void addReview(String m,String rating)
    {
        String r = String.valueOf(ratingBar.getRating());

        float merit = Float.parseFloat(m);

        if (ratingBar.getRating() < 2)
        {
            merit = merit - 5;
        }
        else {
            if (ratingBar.getRating() > 4)
            {
                merit = merit + 2;
            }
            else if (ratingBar.getRating() > 3)
            {
                merit = merit - 1;
            }
        }

        Map<String, Object> review = new HashMap<>();
        review.put("reviewer", r_uid);
        review.put("rating", r);
        review.put("content",Content.getText().toString());
        review.put("time", FieldValue.serverTimestamp());

        db.collection("user").document(r_uid).collection("reviews")
                .add(review)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        float rate = Float.parseFloat(rating);
        rate = (rate + ratingBar.getRating()) / 2;

        if (merit > 100)
        {
            merit = 100;
        }

        Map<String, Object> Meritm = new HashMap<>();
        Meritm.put("merit", String.valueOf(merit));
        Meritm.put("rating", String.valueOf(rate));

        db.collection("user").document(r_uid)
                .set(Meritm, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        Intent intent = new Intent(this, LearnerHome.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LearnerHome.class);
        startActivity(intent);
    }
}
