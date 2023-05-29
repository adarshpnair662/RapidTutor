package com.adarshcreation.rapidtutor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.type.Date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentHistory extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] paymentMethod = { "UPI", "Net Banking", "Debit Card", "Credit Card", "Wallet"};
    String memberTag;
    private int currentApiVersion;
    String TAG="Something";

    List<Payment> payments ;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @ServerTimestamp
    Date time;

    private String TransactionID,Sender,Receiver;
     String Ttime;
    private String Amount,Name;

    RelativeLayout payCard;
    TextView amount, toAddress;
    Boolean reward;
    Button pay;
    ImageView load;
    String comment;
    String payment_type="",price,roomid,chatid,uid,r_uid,userType,R_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
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

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.payment_type);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_card,paymentMethod);
        //aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        amount = findViewById(R.id.payment_amount);
        toAddress = findViewById(R.id.payment_to);
        pay = findViewById(R.id.payment_pay_btn);
        payCard = findViewById(R.id.payment_card_make);
        load = findViewById(R.id.payment_sucess);

        Intent i = getIntent();
        r_uid = i.getStringExtra("r_uid");
        reward = i.getBooleanExtra("reward",false);
        price = i.getStringExtra("amount");

        if (i.getBooleanExtra("payHide",false))
        {
            amount.setVisibility(View.GONE);
            payCard.setVisibility(View.GONE);
        }

        if (price != null) {
            amount.setText("₹ " + price);
        }

        TinyDB tinyDB = new TinyDB(this);
        uid = tinyDB.getString("uid");
        userType = tinyDB.getString("type");

        getTransactionHistory(new TransactionCallback() {
            @Override
            public void onCallback(String time, String amount, String sender, String receiver, String transaction, String date) {

                getUserName(receiver,new MyCallback() {
                    @Override
                    public void onCallback(final String room) {

                        Toast. makeText(getApplicationContext(),Ttime,Toast. LENGTH_SHORT);

                        payments.add(new Payment(transaction,amount,sender,receiver,time,room));

                        RecyclerView myrv = (RecyclerView) findViewById(R.id.recycler_payment);
                        PaymentRecycler myAdapter = new PaymentRecycler(PaymentHistory.this,payments);
                        myrv.setLayoutManager(new GridLayoutManager(PaymentHistory.this,1));
                        myrv.setAdapter(myAdapter);
                    }

                });

            }
        });


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TinyDB tinyDB = new TinyDB(PaymentHistory.this);
                tinyDB.putBoolean("payment",true);

                if (reward)
                {
                    if ( tinyDB.getString("type").equals("learn"))
                        memberTag = r_uid + "_" + uid;
                    else memberTag = uid + "_" + r_uid;

                    roomid=memberTag;

                    Intent i = getIntent();
                    comment = i.getStringExtra("comment");

                    sendMessage();
                    addTransaction();

            }
                else {
                    Intent i = getIntent();
                    comment = i.getStringExtra("comment");
                    roomid = i.getStringExtra("roomid");
                    chatid=i.getStringExtra("chatid");

                    updateMessage();
                    addTransaction();
                }

                load.setVisibility(View.VISIBLE);

                getBalance(new MyCallback() {
                    @Override
                    public void onCallback(final String room) {
                        Log.d("TAG", room);

                        getOrder(new MyCallback() {
                            @Override
                            public void onCallback(final String order) {
                                Log.d("TAG", order);

                                updateBalance(room,order);
                            }

                        });
                    }

                });
            }
        });

    }

    void updateBalance(String b, String o)
    {
        float bal = Float.parseFloat(b);
        bal = bal + Float.parseFloat(price);

        int ord = Integer.parseInt(o);
        ord = ord+1;
        o = String.valueOf(ord);
        b = String.valueOf(bal);

        Map<String, Object> user = new HashMap<>();
        user.put("pending_balance", b);
        user.put("order", o);

        db.collection("user").document(r_uid)
                .set(user,SetOptions.merge())
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

        Toast.makeText(getApplicationContext(),"Payment Successful",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this,ChatRoom.class);
        intent.putExtra("uid",uid);
        intent.putExtra("r_uid",r_uid);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public  void getBalance(final MyCallback myCallback)
    {
        DocumentReference docRef = db.collection("user").document(r_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String balance;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        balance = document.getString("pending_balance");

                        myCallback.onCallback(balance);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public  void getOrder(final MyCallback myCallback)
    {
        DocumentReference docRef = db.collection("user").document(r_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String balance;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        balance = document.getString("order");

                        myCallback.onCallback(balance);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
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

    public void getTransactionHistory(final TransactionCallback myCallback)
    {

        payments = new ArrayList<>();

        db.collection("payment")
                .whereEqualTo("sender", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Ttime = document.getString("date") + " " + document.getString("time");
                                Amount = document.getString("amount");
                                Sender = document.getString("sender");
                                TransactionID = document.getId();
                                Receiver = document.getString("receiver");
                            myCallback.onCallback(Ttime,Amount,Sender,Receiver,TransactionID,document.getString("date"));
                                }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



    }

    public void addTransaction()
    {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        String currentTime = new SimpleDateFormat("HH:mm").format(new java.util.Date());

        // Add a new document with a generated id.
        Map<String, Object> data = new HashMap<>();
        data.put("amount", price.toString());
        data.put("sender", uid);
        data.put("receiver", r_uid);
        data.put("date",currentDate);
        data.put("time",  currentTime);

        db.collection("payment")
                .add(data)
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

    }

    public void updateMessage()
    {

        Map<String, Object> m = new HashMap<>();
        m.put("payment",true);
        m.put("status",true);

        db.collection("chatrooms").document(roomid).collection("chat").document(chatid)
                .set(m, SetOptions.merge())
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
        m.put("message", comment);
        m.put("user",uid);
        m.put("offer",true);
        m.put("payment",true);
        m.put("status",false);
        m.put("amount",price);

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
    }



    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        payment_type = paymentMethod[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        payment_type ="";
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

