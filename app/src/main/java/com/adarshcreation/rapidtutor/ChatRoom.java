package com.adarshcreation.rapidtutor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.type.Date;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatRoom extends AppCompatActivity {

    private int currentApiVersion;

    String uid,r_uid;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something";
    List<Chat> chat ;

    String memberTag="name";
    String roomid="";
    String message,userType;

    @ServerTimestamp
    Date time;

    ImageView profile_pic,order_icon;
    TextView name,order;
    RatingBar rating;
    EditText messageType;
    ImageButton rewardOrOffer, videoCall, send, photoUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

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

        TinyDB tinyDB = new TinyDB(ChatRoom.this);
        tinyDB.putBoolean("payment",false);
        uid = tinyDB.getString("uid");
        userType = tinyDB.getString("type");

        Intent i= getIntent();
        r_uid = i.getStringExtra("r_uid");

        //getting chatroom id

        if ( tinyDB.getString("type").equals("learn")) {
            memberTag = r_uid + "_" + uid;

            addRoomMember(memberTag);
        }
        else {memberTag = uid + "_" + r_uid;}

        roomid = memberTag;

        updateChatHistory();

        setLayoutContents(); //profile details display

        videoCall = findViewById(R.id.chat_room_video_call);
        messageType = findViewById(R.id.chat_room_typing);
        rewardOrOffer = findViewById(R.id.chat_room_offer);
        photoUpload = findViewById(R.id.chat_room_photo_upload);
        send = findViewById(R.id.chat_room_send);

        final RecyclerView myrv = (RecyclerView) findViewById(R.id.chat_recycle);

        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChatRoom.this, VideoCall.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        rewardOrOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TinyDB tinyDB = new TinyDB(ChatRoom.this);
                if (tinyDB.getString("type").equals("learn"))
                {

                    tinyDB.putBoolean("payment",true);

                    Intent intent = new Intent(ChatRoom.this, PayReward.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("r_uid", r_uid);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
                else {
                    Intent intent = new Intent(ChatRoom.this, SendOffer.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("r_uid", r_uid);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

     send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                message = messageType.getText().toString();
                sendMessage();

                messageType.getText().clear();
                // Then just use the following:
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(myrv.getWindowToken(), 0);
            }
        });


    }
    public void setLayoutContents()
    {
        name= findViewById(R.id.chat_room_name);
         profile_pic= findViewById(R.id.chat_room_profile_pic);
        order = findViewById(R.id.chat_room_order_no);
        rating = findViewById(R.id.chat_room_rating);
        order_icon = findViewById(R.id.reciver_order_icon);

        DocumentReference docRef = db.collection("user").document(r_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name.setText(document.getString("name"));

                        String imageUrl = document.getString("profile_pic");
                        //Loading image using Picasso
                        Picasso.with(ChatRoom.this).load(imageUrl).into(profile_pic);

                        TinyDB tinyDB = new TinyDB(ChatRoom.this);
                        if (tinyDB.getString("type").equals("learn")) {
                            order.setText(document.getString("order"));
                            rating.setRating(Float.parseFloat(document.getString("rating")));
                        }
                        else{
                            order.setText(document.getString("currently"));
                            order.setBackground(getDrawable(R.drawable.categories_stroke));
                            order_icon.setVisibility(View.GONE);
                            rating.setVisibility(View.INVISIBLE);
                        }

                        } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void addRoomMember(String r)
    {

        Map<String, Object> chatroom = new HashMap<>();
        chatroom.put("learn",  uid);
        chatroom.put("teach",  r_uid);
        if (userType.equals("teach")) {
            chatroom.put("learn_read", false);
            chatroom.put("teach_read", true);
        }
        else {
        chatroom.put("teach_read", false);
        chatroom.put("learn_read", true);
        }

        db.collection("chatrooms").document(r)
                .set(chatroom, SetOptions.merge())
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
    }

    public void sendMessage()
    {

        Map<String, Object> m = new HashMap<>();
        m.put("message", message);
        m.put("user",uid);
        m.put("offer",false);
        /*
        m.put("status", false);
        m.put("payment",false);
        */
        m.put("time",  FieldValue.serverTimestamp());

        db.collection("chatrooms").document(roomid).collection("chat")
                .add(m)
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

        addRoomMember(roomid);
    }

    public void updateChatHistory()
    {

        db.collection("chatrooms").document(roomid).collection("chat")
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        chat = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("message") != null) {
                                try {
                                    boolean check = doc.getBoolean("offer");
                                    float amount = 0;
                                    boolean payment = false, status = true;
                                    if (check) {
                                        if (check) {
                                            amount = Float.parseFloat(doc.getString("amount"));
                                            payment = doc.getBoolean("payment");
                                            status = doc.getBoolean("status");
                                        }
                                    }
                                    chat.add(new Chat(doc.getId(), doc.getString("message"), doc.getString("user"), check, amount, status, payment));
                                }
                                catch (Exception f){

                                }
                            }
                        }
                        RecyclerView myrv = (RecyclerView) findViewById(R.id.chat_recycle);
                        ChatRecycler myAdapter = new ChatRecycler(ChatRoom.this, chat, roomid, r_uid);
                        myrv.setLayoutManager(new GridLayoutManager(ChatRoom.this, 1));
                        myrv.setAdapter(myAdapter);

                        Log.d(TAG, "Current cites in CA: " + chat);
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        TinyDB tinyDB = new TinyDB(ChatRoom.this);
        Boolean payment = tinyDB.getBoolean("payment");

        if (tinyDB.getString("type").equals("learn")) {
            if (payment) {
                tinyDB.putBoolean("payment", false);
                Intent intent = new Intent(this, Review.class);
                intent.putExtra("uid", uid);
                intent.putExtra("r_uid", r_uid);
                intent.putExtra("name", name.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            else {
                Intent intent = new Intent(this, ChatHistory.class);
                intent.putExtra("uid", uid);
                intent.putExtra("r_uid", r_uid);
                startActivity(intent);
            }

            }
        else {

            Intent intent = new Intent(this, ChatTeachHistory.class);
            startActivity(intent);
        }

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
