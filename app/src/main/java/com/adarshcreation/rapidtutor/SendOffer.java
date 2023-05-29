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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendOffer extends AppCompatActivity {

    Dialog myDailog;
    Button send,cancel;

EditText hourlyRate,Comment;

    String TAG="Something",uid,roomid,r_uid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_offer);

        myDailog = new Dialog(this);

        myDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDailog.show();

        hourlyRate = findViewById(R.id.hourly_rate_num);
        Comment = findViewById(R.id.comment_box);
        send = findViewById(R.id.send_offer);
        cancel = findViewById(R.id.cancel_offer_btn);

        TinyDB tinyDB = new TinyDB(this);
        uid = tinyDB.getString("uid");

        Intent i = getIntent();
        r_uid=i.getStringExtra("r_uid");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean flag = true;

                if (hourlyRate.getText().toString().isEmpty())
                {
                    flag =false;
                    hourlyRate.setError("Required");
                }else {
                    if (Float.parseFloat(hourlyRate.getText().toString()) < 20)
                    {

                        flag =false;
                        hourlyRate.setError("min ₹20");
                    }
                }

                if (Comment.getText().toString().isEmpty()) {

                    flag =false;
                    Comment.setError("Required");
                }

                if (flag){
                   sendMessage();
                }

            }
        });
    }

    public void sendMessage()
    {
        TinyDB tinyDB = new TinyDB(this);
        String memberTag;
        if ( tinyDB.getString("type").equals("learn"))
            memberTag = r_uid + "_" + uid;
        else memberTag = uid + "_" + r_uid;

        roomid=memberTag;

        Map<String, Object> m = new HashMap<>();
        m.put("message", Comment.getText().toString());
        m.put("user",uid);
        m.put("offer",true);
        m.put("payment",false);
        m.put("status",false);
        m.put("amount",hourlyRate.getText().toString());

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

        onBackPressed();
        finish();
    }

}
