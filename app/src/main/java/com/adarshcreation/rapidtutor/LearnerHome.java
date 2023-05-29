package com.adarshcreation.rapidtutor;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LearnerHome extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {
    private SimpleGestureFilter detector;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something",uid;

    private int currentApiVersion;

    List<Categories> lstBook ;
    EditText search;
    String CHANNEL_ID;

    ImageButton searchButton,notification;
    ImageButton chat,paymentHistory,EditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learner_home);
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

        //end hider

        chat = findViewById(R.id.menu_chat);
        EditProfile = findViewById(R.id.menu_profile);
        paymentHistory = findViewById(R.id.menu_payment);
        notification = findViewById(R.id.notification_btn);

        TinyDB tinyDB = new TinyDB(LearnerHome.this);
        uid = tinyDB.getString("uid");

        CHANNEL_ID=uid;
        createNotificationChannel();

        ChatNotify(new CallBack() {
            @Override
            public void onCallback(final Boolean docid) {
                Log.d("TAG", docid.toString());

                Boolean triggerNotify = false;

                if (!(docid)) {
                    triggerNotify=true;
                }

                if (triggerNotify)
                {
                    addChatNotification();
                }
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LearnerHome.this, ChatHistory.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LearnerHome.this, TeachProfile.class);
                intent.putExtra("r_uid",uid);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            }
        });

        paymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LearnerHome.this, PaymentHistory.class);
                intent.putExtra("payHide",true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            }
        });

        // Detect touched area
        detector = new SimpleGestureFilter(this,this);

        lstBook = new ArrayList<>();
        lstBook.add(new Categories("Programming"));
        lstBook.add(new Categories("Business "));
        lstBook.add(new Categories("Hardware "));
        lstBook.add(new Categories("IT"));
        lstBook.add(new Categories("Lifestyle"));
        lstBook.add(new Categories("Medical"));
        lstBook.add(new Categories("Auto"));
        lstBook.add(new Categories("Sports"));
        lstBook.add(new Categories("Arts"));
        lstBook.add(new Categories("Mathematics"));
        lstBook.add(new Categories("Language"));
        lstBook.add(new Categories("History"));
        lstBook.add(new Categories("Geography"));
        lstBook.add(new Categories("Politics"));
        lstBook.add(new Categories("Space"));
        lstBook.add(new Categories("Environment"));
        lstBook.add(new Categories("Cooking"));
        lstBook.add(new Categories("Travel"));
        lstBook.add(new Categories("Motivation"));
        lstBook.add(new Categories("Consultancy"));

        RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
        Categories_Recycler myAdapter = new Categories_Recycler(this,lstBook);
        myrv.setLayoutManager(new GridLayoutManager(this,2));
        myrv.setAdapter(myAdapter);

        //categories uploaded

       searchButton = findViewById(R.id.search_btn);
       search = findViewById(R.id.search_text);

       searchButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Intent intent = new Intent(view.getContext(), SearchResult.class);
               intent.putExtra("query", search.getText().toString());
               startActivity(intent);
           }
       });

       notification.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(view.getContext(), Notification.class);
               startActivity(intent);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT : str = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
            break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";

            break;

        }
    }

    @Override
    public void onDoubleTap() {
    }

    private void addChatNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Messages")
                .setContentText("You have got a new messages")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    void ChatNotify(final CallBack myCallback)
    {

        db.collection("booking")
                .whereEqualTo("receiver", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                myCallback.onCallback(document.getBoolean("read"));
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this,R.style.AlertDialogStyle)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
