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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TutorHome extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something",uid;
    private int currentApiVersion;

    TextView order,earning;
    Button checkOut;
    Switch onlineOffline;
    RelativeLayout statusBar,l1;
    String CHANNEL_ID,CHANNEL_ID2;
    int count=0;

    ImageButton home,booking;
    ImageButton chat,paymentHistory,EditProfile;
    TextView bookingValue,messagesValue;
    LinearLayout l2;
    TextView block;
    ImageView load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);

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

        order = findViewById(R.id.tutor_num_order);
        earning = findViewById(R.id.tutor_amount);
        onlineOffline = findViewById(R.id.online_offline_switch);
        checkOut = findViewById(R.id.Transaction_check);
        statusBar = findViewById(R.id.status_bar);
        bookingValue = findViewById(R.id.tutor_booking_value);
        messagesValue = findViewById(R.id.tutor_message_value);
        chat = findViewById(R.id.menu_chat);
        EditProfile = findViewById(R.id.menu_profile);
        paymentHistory = findViewById(R.id.menu_payment);
        booking = findViewById(R.id.menu_booking);
        home = findViewById(R.id.menu_home);
        block = findViewById(R.id.account_block_text);
        l1 = findViewById(R.id.linear_tutor_1);
        l2 = findViewById(R.id.linear_tutor_2);
        load = findViewById(R.id.animation_view);


        TinyDB tinyDB = new TinyDB(this);
        uid = tinyDB.getString("uid");

        getDetails();

        CHANNEL_ID=uid;
        CHANNEL_ID2="0202rjfj";
        createNotificationChannel();
        createNotificationChannel2();

        bookingValue.setText(String.valueOf(count));

        count =0;
        notificationCountCount(new CallBack() {
            @Override
            public void onCallback(final Boolean docid) {
                Log.d("TAG", docid.toString());


                if (!(docid)) {
                    count = count+1;
                   }

                bookingValue.setText(String.valueOf(count));

            }
        });

        spotNotifyIcon(new CallBack() {
            @Override
            public void onCallback(final Boolean docid) {
                Log.d("TAG", docid.toString());

                Boolean triggerNotify = false;

                if (!(docid)) {
                   triggerNotify=true;
                }

                if (triggerNotify)
                {
                    addNotification();
                }

            }
        });

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

        getTodayEarn(new EarnCallback(){
            @Override
            public void onCallback(final String docid,String am) {
                Log.d("TAG", docid.toString());
                float sum=0;
                sum = sum + Float.parseFloat(am);
                earning.setText(String.valueOf(sum));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TutorHome.this, ChatTeachHistory.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TinyDB tinyDB = new TinyDB(TutorHome.this);
                uid = tinyDB.getString("uid");

                Intent intent = new Intent(TutorHome.this, TeachProfile.class);
                intent.putExtra("r_uid",uid);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            }
        });

        paymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TutorHome.this, EarningHistory.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  Intent intent = new Intent(TutorHome.this, TutorHome.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_out); */
            }
        });

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TutorHome.this, BookingHistory.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            }
        });

        onlineOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                makeOnlineOrOffline(b);
                if (b)
                {
                    statusBar.setVisibility(View.VISIBLE);
                }
                else {
                    statusBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TutorHome.this, TransactionHistory.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        String str = messagesValue.getText().toString();
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 1);
        }
        int m = Integer.parseInt(result);

        if (m < 60)
        {
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.GONE);
            block.setVisibility(View.VISIBLE);
        }



    }

    void getTodayEarn(final EarnCallback myCallback)
    {
        db.collection("payment")
                .whereEqualTo("receiver", true).orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());

                                if (currentDate.equals(document.getString("date"))) {
                                    myCallback.onCallback(document.getId(), document.getString("amount"));
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void createNotificationChannel2() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name2);
            String description = getString(R.string.channel_description2);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID2, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void addNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Booking")
                .setContentText("You have got a new bookings")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    void notificationCountCount(final CallBack myCallback)
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

    private void addChatNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID2)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Messages")
                .setContentText("You have got a new messages")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(2, builder.build());
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

    void spotNotifyIcon(final CallBack myCallback)
    {
        db.collection("booking")
                .whereEqualTo("receiver", uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        count =0;
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("read") != null) {
                                myCallback.onCallback(doc.getBoolean("read"));
                            }
                        }
                        Log.d(TAG, "Current cites in CA: " );
                    }
                });

    }

    void makeOnlineOrOffline(boolean status)
    {
        Map<String, Object> city = new HashMap<>();
        city.put("status", status);

        db.collection("user").document(uid)
                .set(city, SetOptions.merge())
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

    void getDetails()
    {
        final DocumentReference docRef = db.collection("user").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        order.setText(document.getString("order"));
                        earning.setText(document.getString("balance"));
                        messagesValue.setText(document.getString("merit")+"%");

                        try {

                            if (document.getBoolean("status")) {
                                onlineOffline.setChecked(true);
                                statusBar.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (Exception e)
                        {}

                        load.setVisibility(View.GONE);
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