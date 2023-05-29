package com.adarshcreation.rapidtutor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PayReward extends AppCompatActivity {

    Dialog myDailog;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something";
    List<Chat> chat ;

    Button cancel,send;
    EditText amount, comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_reward);

        myDailog = new Dialog(this);

        myDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDailog.show();

        send = findViewById(R.id.send_reward);
        cancel = findViewById(R.id.cancel_reward_btn);
        amount = findViewById(R.id.pay_hourly_rate_num);
        comment = findViewById(R.id.pay_comment_box);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean flag = true;

                if (amount.getText().toString().isEmpty())
                {
                    flag =false;
                    amount.setError("Required");
                }else {
                    if (Float.parseFloat(amount.getText().toString()) < 20)
                    {

                        flag =false;
                        amount.setError("min ₹20");
                    }
                }

                if (comment.getText().toString().isEmpty()) {

                    flag =false;
                    comment.setError("Required");
                }

                if (flag){
                    Intent i = getIntent();

                    Intent intent = new Intent(PayReward.this, PaymentHistory.class);
                    intent.putExtra("r_uid", i.getStringExtra("r_uid"));
                    intent.putExtra("reward", true);
                    intent.putExtra("amount",amount.getText().toString());
                    intent.putExtra("comment",comment.getText().toString());
                    startActivity(intent);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}
