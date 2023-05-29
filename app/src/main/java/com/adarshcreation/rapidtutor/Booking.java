package com.adarshcreation.rapidtutor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Booking extends AppCompatActivity implements View.OnClickListener {
    Dialog myDailog;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something",uid,r_uid;
    List<Chat> chat ;

    Button btnDatePicker, btnTimePicker;

    Button cancel,send;
    EditText amount, comment;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        myDailog = new Dialog(this);

        myDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDailog.show();

        send = findViewById(R.id.send_reward);
        cancel = findViewById(R.id.cancel_reward_btn);
        amount = findViewById(R.id.pay_hourly_rate_num);
        comment = findViewById(R.id.pay_comment_box);

        btnDatePicker=(Button)findViewById(R.id.date_picker);
        btnTimePicker=(Button)findViewById(R.id.time_picker);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        Intent i = getIntent();
        uid=i.getStringExtra("uid");
        r_uid=i.getStringExtra("r_uid");


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean flag=true;

                if (btnDatePicker.getText().toString().equals("DATE"))
                {
                    flag= false;
                    btnDatePicker.setError("Select");
                }
                if (btnTimePicker.getText().toString().equals("TIME"))
                {
                    flag=false;
                    btnTimePicker.setError("Select");
                }
                if (amount.getText().toString().isEmpty())
                {
                    flag=false;
                    amount.setError("Empty");
                }

                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

                try {
                Date d1 = sdformat.parse(currentDate);
                Date d2 = sdformat.parse( btnDatePicker.getText().toString());

                if(d1.compareTo(d2) > 0) {
                  flag=false;
                  btnDatePicker.setError("Invalid Date");
                }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat stdformat = new SimpleDateFormat("HH:mm");

                String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
                try {
                    Date d1 = stdformat.parse(currentTime);
                    Date d2 = sdformat.parse( btnTimePicker.getText().toString());

                    if(d1.compareTo(d2) > 0) {
                        flag=false;
                        btnDatePicker.setError("Invalid Time");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (flag) {
                    // Add a new document with a generated id.
                    Map<String, Object> data = new HashMap<>();
                    data.put("date", btnDatePicker.getText().toString());
                    data.put("time", btnTimePicker.getText().toString());
                    data.put("timestamp", FieldValue.serverTimestamp());
                    data.put("sender", uid);
                    data.put("amount", amount.getText().toString());
                    data.put("receiver", r_uid);
                    data.put("status", false);
                    data.put("read", false);

                    db.collection("booking")
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


                    Toast.makeText(Booking.this,"Booking Send",Toast.LENGTH_SHORT).show();

                    onBackPressed();

                }   }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                String m;
                monthOfYear =(monthOfYear + 1);
                            if (monthOfYear < 10) {
                                m = "0"+String.valueOf(monthOfYear);
                            } else {
                                m = String.valueOf(monthOfYear);
                            }

                            btnDatePicker.setText(year + "-" + m + "-" + dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            btnTimePicker.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}
