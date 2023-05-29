package com.adarshcreation.rapidtutor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatHistory extends AppCompatActivity {

    private int currentApiVersion;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something",uid,userType;
    List<TutorProfile> profile ;
    TextView noChat;
    Boolean fav=true;

    ImageButton favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

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

        TinyDB tinyDB = new TinyDB(this);
        userType = tinyDB.getString("type");
        uid = tinyDB.getString("uid");

        favourite = findViewById(R.id.favourite);
        noChat = findViewById(R.id.no_chat);

        nonfavList();

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fav) {
                    favourite.setImageDrawable(getDrawable(R.drawable.fill_favorite));
                    fav=false;
                    checkFav(new MyCallback() {

                        @Override
                        public void onCallback(final String  doc) {

                            Log.d("TAG", doc.toString());

                            favList(doc);

                        }
                    });
                }
                else {
                    favourite.setImageDrawable(getDrawable(R.drawable.favorite_border));
                    fav=true;
                    nonfavList();
                }
            }
        });
    }
    void checkFav(final MyCallback myCallback)
    {
        profile = new ArrayList<>();

        db.collection("chatrooms")
                .whereEqualTo(userType, uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        profile = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {

                                    if (document.getBoolean("fav")) {
                                        if (userType.equals("learn")) {
                                            myCallback.onCallback(document.getString("teach"));
                                        } else {
                                            myCallback.onCallback(document.getString("learn"));
                                        }
                                    }
                                }
                                catch (Exception e)
                                {

                                }

                            }
                            RecyclerView myrv = (RecyclerView) findViewById(R.id.chat_history_recycle);
                            ChatHistoryRecycler myAdapter = new ChatHistoryRecycler(ChatHistory.this, profile);
                            myrv.setLayoutManager(new GridLayoutManager(ChatHistory.this, 1));
                            myrv.setAdapter(myAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    void nonfavList()
    {

        db.collection("chatrooms")
                .whereEqualTo(userType, uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        profile = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                getUserDetails(document.getString("teach"));
                             }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        }

        void getUserDetails(String u)
        {
            DocumentReference docRef = db.collection("user").document(u);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            profile.add(new TutorProfile(document.getId(), document.getString("name"), document.getBoolean("status"), document.getString("rating"), document.getString("order"), document.getString("profile_pic")));

                        } else {
                            Log.d(TAG, "No such document");
                        }
                        RecyclerView myrv = (RecyclerView) findViewById(R.id.chat_history_recycle);
                        ChatHistoryRecycler myAdapter = new ChatHistoryRecycler(ChatHistory.this, profile);
                        myrv.setLayoutManager(new GridLayoutManager(ChatHistory.this, 1));
                        myrv.setAdapter(myAdapter);
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }

    void favList(String docid)
    {
            DocumentReference docRef = db.collection("user").document(docid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            final String docId =document.getId();
                            final String Rating= document.getString("rating");
                            final String Profile_pic = document.getString("profile_pic");
                             final Boolean Status=document.getBoolean("status");

                                profile.add(new TutorProfile(docId, "0", Status, Rating, "0", Profile_pic));

                            RecyclerView myrv = (RecyclerView) findViewById(R.id.chat_history_recycle);
                            ChatHistoryRecycler myAdapter = new ChatHistoryRecycler(ChatHistory.this, profile);
                            myrv.setLayoutManager(new GridLayoutManager(ChatHistory.this, 1));
                            myrv.setAdapter(myAdapter);

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }


                }
            });



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatHistory.this, LearnerHome.class);
        startActivity(intent);
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
