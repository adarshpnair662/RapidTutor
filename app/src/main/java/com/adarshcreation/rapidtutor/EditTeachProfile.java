package com.adarshcreation.rapidtutor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditTeachProfile extends AppCompatActivity {

    private int currentApiVersion;
    String TAG="Something";

    Button Tdone;
    EditText name,about_me,mobile_num,tags,hourly_rate,communication,age,ac_num,ac_name,ac_ifsc;
    ImageView profile_pic;
    String uid;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teach_profile);


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

        Intent i= getIntent();
        profile_pic = findViewById(R.id.tutor_profile_pic_upload);
        uid = i.getStringExtra("uid");
        String imageUrl =i.getStringExtra("userPhoto");
        //Loading image using Picasso
        Picasso.with(this).load(imageUrl).into(profile_pic);


        //initialization
        name = findViewById(R.id.tutor_name);
        about_me = findViewById(R.id.tutor_about_me);
        mobile_num = findViewById(R.id.tutor_mobile_num);
        tags = findViewById(R.id.tutor_tags);
        hourly_rate = findViewById(R.id.tutor_hourly_rate);
        Tdone = findViewById(R.id.teach_done);
        communication = findViewById(R.id.tutor_communication);
        age = findViewById(R.id.tutor_age);
        ac_num = findViewById(R.id.payment_account_no);
        ac_ifsc = findViewById(R.id.payment_ifsc);
        ac_name = findViewById(R.id.payment_holder_name);

        TinyDB tinyDB = new TinyDB(this);
        if(!(tinyDB.getString("uid").isEmpty())) {
            getValues();
        }

        Tdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validation
                Boolean flag = true;

                if ( TextUtils.isEmpty(mobile_num.getText()) && mobile_num.getText().toString().length()!=10) {

                    mobile_num.setError("Required");
                    flag=false;

                }

                if( TextUtils.isEmpty(name.getText())){

                    name.setError("Required");
                    flag=false;
                }
                if( TextUtils.isEmpty(communication.getText())){

                    communication.setError( "Required" );
                    flag=false;
                }
                if( TextUtils.isEmpty(tags.getText())){

                    tags.setError( "Required" );
                    flag=false;
                }

                if( TextUtils.isEmpty(about_me.getText())){

                    about_me.setError( "Required" );
                    flag=false;
                }
                if( TextUtils.isEmpty(hourly_rate.getText())){

                    hourly_rate.setError( "Required" );
                    flag=false;
                }
                if (TextUtils.isEmpty(age.getText()))
                {
                    age.setError( "Required" );
                    flag=false;
                }
                if( TextUtils.isEmpty(ac_name.getText())){

                    ac_name.setError( "Required" );
                    flag=false;
                } if( TextUtils.isEmpty(ac_num.getText())){

                    ac_num.setError( "Required" );
                    flag=false;
                } if( TextUtils.isEmpty(ac_ifsc.getText())){

                    ac_ifsc.setError( "Required" );
                    flag=false;
                }
                // validation completes

                String[] tag = splitValues(tags.getText().toString());
                String[] language = splitValues(communication.getText().toString());
                //attend.put("subject", Arrays.asList(subject)) ;

                if (flag) {
                    int checkAge = Integer.parseInt(age.getText().toString());
                    if (checkAge > 18) {
                        if (makeUser(uid, tag, language)) {

                            Intent intent = new Intent(view.getContext(), TutorHome.class);
                            startActivity(intent);

                        }
                    }
                    else
                    {

                        makeMeritLow();
                        Toast.makeText(EditTeachProfile.this,"Sorry, your not eligible",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }

            }

            }
        });

    }

    void makeMeritLow()
    {
        Map<String, Object> user = new HashMap<>();
        user.put("merit", "0");

        db.collection("user").document(uid)
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
    }

    static String[] splitValues(String value)
    {
        String[] ans = new String[2];
        ans = value.split(" ");
        return ans;
    }

    Boolean makeUser( String userid, String[] tag, String[] language)
    {

        //Create a new user
        Map<String, Object> user = new HashMap<>();
        user.put("name", name.getText().toString());
        user.put("mob",mobile_num.getText().toString());
        user.put("communication", Arrays.asList(language));
        user.put("tags", Arrays.asList(tag));
        user.put("about_me",about_me.getText().toString());
        user.put("hourly_rate",hourly_rate.getText().toString());
        user.put("ac_no", ac_num.getText().toString());
        user.put("ac_name",ac_name.getText().toString());
        user.put("ac_ifsc",ac_ifsc.getText().toString());
        user.put("age",age.getText().toString());

        // Add a new document with a generated ID
        db.collection("user").document(userid)
                .set(user, SetOptions.merge())
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

        return true;
    }

    void getValues()
    {
        final DocumentReference docRef = db.collection("user").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        name.setText(document.getString("name"));
                        mobile_num.setText(document.getString("mob"));
                        about_me.setText(document.getString("about_me"));

                        try {
                            List<String> searchTags = (List<String>) document.get("tags");
                            StringBuffer sb = new StringBuffer();

                            for (String s : searchTags) {
                                sb.append(s);
                                sb.append(" ");
                            }
                            String str = sb.toString();
                            tags.setText(str);

                            List<String> language = (List<String>) document.get("communication");
                            sb = new StringBuffer();
                            for (String s : language) {
                                sb.append(s);
                                sb.append(" ");
                            }
                            str = sb.toString();
                            communication.setText(str);

                            hourly_rate.setText(document.getString("hourly_rate"));
                            ac_ifsc.setText(document.getString("ac_ifsc"));
                            ac_name.setText(document.getString("ac_name"));
                            ac_num.setText(document.getString("ac_no"));
                            age.setText(document.getString("age"));

                        }
                    catch (Exception e)
                    {}

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