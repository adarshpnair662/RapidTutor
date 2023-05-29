package com.adarshcreation.rapidtutor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeachProfile extends AppCompatActivity {

    private int currentApiVersion;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something";

    List<ReviewHolder> profile ;

    String r_uid,uid,Usertype,imageUrl;
    TextView name, order, about_me, communication, review;
    RatingBar rating;
    ImageView profile_pic,order_icon,load;
    ImageButton booking, chat,EditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach_profile);

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
        load = findViewById(R.id.animation_view);
        name = findViewById(R.id.teach_name);
        order = findViewById(R.id.teach_order_no);
        rating = findViewById(R.id.teach_rating);
        about_me = findViewById(R.id.teach_about_me);
        communication = findViewById(R.id.teach_communication);
        booking = findViewById(R.id.teach_booking);
        chat = findViewById(R.id.teach_message_icon);
        profile_pic = findViewById(R.id.teach_profile_pic);
        order_icon=findViewById(R.id.order_icon);
        EditProfile = findViewById(R.id.Profile_Edit_icon);
        review = findViewById(R.id.review_text_teach);

        Intent i= getIntent();
        r_uid = i.getStringExtra("r_uid");

        final DocumentReference docRef = db.collection("user").document(r_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        Picasso.with(TeachProfile.this).load(document.getString("profile_pic")).into(profile_pic);
                        name.setText(document.getString("name"));

                        imageUrl=document.getString("profile_pic");

                        TinyDB tinyDB = new TinyDB(TeachProfile.this);
                        Usertype=tinyDB.getString("type");

                        if (Usertype.equals(document.getString("type")))
                        {
                            chat.setVisibility(View.INVISIBLE);
                            EditProfile.setVisibility(View.VISIBLE);
                            booking.setVisibility(View.INVISIBLE);
                        }

                        if (document.getString("type").equals("teach")) {
                            order.setText(document.getString("order"));
                            rating.setRating(Float.parseFloat(document.getString("rating")));

                        }else {
                            rating.setVisibility(View.INVISIBLE);
                            order_icon.setVisibility(View.INVISIBLE);
                            review.setVisibility(View.GONE);
                            order.setText(document.getString("currently"));
                            order.setBackground(getDrawable(R.drawable.categories_stroke));
                        }

                        about_me.setText(document.getString("about_me"));

                        List<String> lang = (List<String>) document.get("communication");
                        String temp="";
                        for(String s: lang)
                        {
                            temp = temp + s +",";
                        }
                            temp = temp.substring(0, temp.length() - 1);

                        communication.setText(temp);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

                load.setVisibility(View.GONE);
            }
        });

        profile = new ArrayList<>();

        db.collection("user").document(r_uid).collection("reviews")
                .orderBy("time")
               .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                getUserName(document.getString("reviewer"),new MyCallback() {
                                    @Override
                                    public void onCallback(final String name) {
                                        Log.d("TAG", name);

                                        profile.add(new ReviewHolder(name,document.getString("content"),Float.parseFloat(document.getString("rating"))));

                                        RecyclerView myrv = (RecyclerView) findViewById(R.id.review_recycle);
                                        ReviewRecycler myAdapter = new ReviewRecycler(TeachProfile.this,profile);
                                        myrv.setLayoutManager(new GridLayoutManager(TeachProfile.this,1));
                                        myrv.setAdapter(myAdapter);

                                        load.setVisibility(View.GONE);
                                    }

                                });

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        //profile details uploaded

        TinyDB tinyDB = new TinyDB(this);
        uid = tinyDB.getString("uid");

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TeachProfile.this, ChatRoom.class);
                intent.putExtra("r_uid", r_uid);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeachProfile.this, Booking.class);
                intent.putExtra("r_uid", r_uid);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TinyDB tinyDB = new TinyDB(TeachProfile.this);
                Usertype=tinyDB.getString("type");
                if (Usertype.equals("learn"))
                {

                    Intent intent = new Intent(TeachProfile.this, EditLearnProfile.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("userPhoto",imageUrl);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(TeachProfile.this, EditTeachProfile.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("userPhoto",imageUrl);
                    startActivity(intent);
                }
            }
        });
    }

    public  void getUserName(String id,final MyCallback myCallback)
    {
        DocumentReference docRef = db.collection("user").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String Rname;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        Rname = document.getString("name");

                        myCallback.onCallback(Rname);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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