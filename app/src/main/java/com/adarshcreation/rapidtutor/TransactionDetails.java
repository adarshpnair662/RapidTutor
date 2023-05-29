package com.adarshcreation.rapidtutor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TransactionDetails extends AppCompatActivity {

    Dialog myDailog;

    TextView Transaction, Sender, Reciver, Amount, Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        myDailog = new Dialog(this);

        myDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDailog.show();

        Reciver = findViewById(R.id.transaction_to);
        Amount = findViewById(R.id.transaction_amount);
        Time = findViewById(R.id.transaction_time);
        Sender = findViewById(R.id.transaction_from);
        Transaction = findViewById(R.id.transaction_id);

        Intent i= getIntent();
        String time = i.getStringExtra("time");
        String sender = i.getStringExtra("sender");
        String receiver = i.getStringExtra("receiver");
        String amount = i.getStringExtra("amount");
        String transaction = i.getStringExtra("transaction");

        Sender.setText(sender);
        Reciver.setText(receiver);
        Amount.setText(amount);
        Transaction.setText(transaction);
        Time.setText(time);

    }
}
