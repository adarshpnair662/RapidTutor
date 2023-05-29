package com.adarshcreation.rapidtutor;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Notification extends AppCompatActivity {

    private int currentApiVersion;
    String TAG="Something";


    List<Book> book1,book2,book3 ;
    String uid,bookid;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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

        TinyDB tinydb = new TinyDB(Notification.this);
        uid = tinydb.getString("uid");

        getUserID(new MyCallback() {
            @Override
            public void onCallback(final String room) {
                Log.d("TAG", room.toString());

                bookid=room;

                AllDoc();
            }
        });

    }

    void getUserID(final MyCallback myCallback)
    {
        db.collection("booking")
                .whereEqualTo("sender", uid).orderBy("timestamp")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                myCallback.onCallback(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    void AllDoc()
    {
        DocumentReference docRef = db.collection("booking").document(bookid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                book1 = new ArrayList<>();
                book2 = new ArrayList<>();
                book3 = new ArrayList<>();

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                         final String Rid,DocID,Bdate,Btime;
                         final boolean Read, Status;
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


                         DocID = document.getId();
                         Rid = document.getString("receiver");
                         Bdate = document.getString("date");
                         Btime = document.getString("time");
                         Read = document.getBoolean("read");
                         Status = document.getBoolean("status");

                        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                        Boolean validDate=true,validTime=true;
                        try {
                            Date d1 = sdformat.parse(currentDate);
                            Date d2 = sdformat.parse(Bdate);
                            if(d1.compareTo(d2) > 0) {
                                validDate=false;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat stdformat = new SimpleDateFormat("HH:mm");

                        String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
                        try {
                            Date d1 = stdformat.parse(currentTime);
                            Date d2 = sdformat.parse( Btime);

                            if(d1.compareTo(d2) > 0) {
                                validDate=false;
                                validTime=true;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                         if ((document.getBoolean("read"))) {

                             if (currentDate.equals(Bdate)) {
                                 if (validTime) {
                                     UserNameGet(Rid, new MyCallback() {
                                         @Override
                                         public void onCallback(final String name) {
                                             Log.d("TAG", name.toString());


                                             book1.add(new Book(DocID, uid, Rid, Bdate, Btime, Read, Status, name));

                                             RecyclerView myrv = (RecyclerView) findViewById(R.id.notification_today_recycle);
                                             NotificationRecycler myAdapter = new NotificationRecycler(Notification.this, book1, 2);
                                             myrv.setLayoutManager(new GridLayoutManager(Notification.this, 1));
                                             myrv.setAdapter(myAdapter);
                                         }
                                     });
                                 }
                             }
                             else if (validDate){
                                 UserNameGet(Rid, new MyCallback() {
                                     @Override
                                     public void onCallback(final String name) {
                                         Log.d("TAG", name.toString());


                                         book2.add(new Book(DocID, uid, Rid, Bdate, Btime, Read, Status, name));

                                         RecyclerView myrv = (RecyclerView) findViewById(R.id.notification_upcoming_recycle);
                                         NotificationRecycler myAdapter = new NotificationRecycler(Notification.this, book2, 2);
                                         myrv.setLayoutManager(new GridLayoutManager(Notification.this, 1));
                                         myrv.setAdapter(myAdapter);
                                     }
                                 });
                             }
                         }else {

                             if (validDate) {
                                 UserNameGet(Rid, new MyCallback() {
                                     @Override
                                     public void onCallback(final String name) {
                                         Log.d("TAG", name.toString());


                                         book3.add(new Book(DocID, uid, Rid, Bdate, Btime, Read, Status, name));

                                         RecyclerView myrv = (RecyclerView) findViewById(R.id.notification_recycle);
                                         NotificationRecycler myAdapter = new NotificationRecycler(Notification.this, book3, 1);
                                         myrv.setLayoutManager(new GridLayoutManager(Notification.this, 1));
                                         myrv.setAdapter(myAdapter);
                                     }
                                 });
                             }
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

    void UserNameGet(String r_uid,final MyCallback myCallback)
    {
        DocumentReference docRef = db.collection("user").document(r_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        myCallback.onCallback(document.getString("name"));
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
