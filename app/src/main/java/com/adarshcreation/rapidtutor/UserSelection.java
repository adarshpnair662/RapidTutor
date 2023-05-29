package com.adarshcreation.rapidtutor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserSelection extends AppCompatActivity {

    private int currentApiVersion;
    int RC_SIGN_IN =0;
    String TAG="Something";
    Boolean userUpdate;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button learn,teach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        //hider
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
        //hider_end

        learn=findViewById(R.id.learn_btn);

        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TinyDB tinydb = new TinyDB(UserSelection.this);
                userUpdate = makeUser("learn");
                tinydb.putString("type","learn");

                if (userUpdate) {

                    Intent i= getIntent();
                    Intent intent = new Intent(view.getContext(), EditLearnProfile.class);
                    intent.putExtra("userPhoto", i.getStringExtra("userPhoto"));
                    intent.putExtra("uid", i.getStringExtra("uid"));
                    startActivity(intent);
                }
            }
        });

        teach = findViewById(R.id.teach_btn);

        teach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TinyDB tinydb = new TinyDB(UserSelection.this);
                userUpdate = makeUser("teach");
                tinydb.putString("type","teach");

                if (userUpdate) {
                    Intent i= getIntent();
                    Intent intent = new Intent(view.getContext(), EditTeachProfile.class);
                    intent.putExtra("userPhoto", i.getStringExtra("userPhoto"));
                    intent.putExtra("uid", i.getStringExtra("uid"));
                    startActivity(intent);
                }
            }
        });
    }

    Boolean makeUser(String type)
    {
        Intent i= getIntent();
        //Create a new user
        Map<String, Object> user = new HashMap<>();
        user.put("name", i.getStringExtra("userName"));
        user.put("email", i.getStringExtra("userEmail"));
        user.put("profile_pic", i.getStringExtra("userPhoto"));
        user.put("type", type);
        user.put("rating", "0");
        if (type.equals("teach")) {
            user.put("balance","0");
            user.put("pending_balance","0");
            user.put("status", false);
            user.put("fav", false);
            user.put("merit","100");
            user.put("order", "0");
            user.put("hourly_rate", "0");
        }

            // Add a new document with a generated ID
            db.collection("user").document(i.getStringExtra("uid"))
                    .set(user)
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

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
