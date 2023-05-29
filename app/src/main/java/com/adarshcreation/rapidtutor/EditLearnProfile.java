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

public class EditLearnProfile extends AppCompatActivity {

    private int currentApiVersion;
    String TAG="Something";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button Ldone;
    EditText name,about_me,mobile_num,currently,communication;
    ImageView profile_pic;
    String uid,imageUrl;
    ImageView load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_learn_profile);


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

        name = findViewById(R.id.learn_name);
        about_me = findViewById(R.id.learn_about_me);
        mobile_num = findViewById(R.id.learn_mobile_num);
        currently = findViewById(R.id.learn_currently);
        communication = findViewById(R.id.learn_communication);
        Ldone = findViewById(R.id.learn_done);
        profile_pic = findViewById(R.id.learn_profile_pic_upload);



        Intent i= getIntent();
        uid = i.getStringExtra("uid");
        imageUrl =i.getStringExtra("userPhoto");
        //Loading image using Picasso
        Picasso.with(this).load(imageUrl).into(profile_pic);

        getValues();

        Ldone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validation
                Boolean mobile_check=true, name_check=true, communication_check=true, about_me_check=true, tags_check=true;
                Boolean currently_check=true;

                if ( TextUtils.isEmpty(mobile_num.getText()) && mobile_num.getText().toString().length()!=10) {

                    mobile_num.setError("Required");
                    mobile_check=false;

                }

                if( TextUtils.isEmpty(name.getText())){

                    name.setError("Required");
                    name_check=false;
                }
                if( TextUtils.isEmpty(communication.getText())){

                    communication.setError( "Required" );
                    communication_check=false;
                }

                if( TextUtils.isEmpty(about_me.getText())){

                    about_me.setError( "Required" );
                    about_me_check=false;
                }
                if( TextUtils.isEmpty(currently.getText())){

                    currently.setError( "Required" );
                    currently_check=false;
                }

                // validation completes

                String[] language = splitValues(communication.getText().toString());

                if (mobile_check && name_check && communication_check && tags_check && currently_check && about_me_check) {
                    if (makeUser(uid, language)) {

                        Intent intent = new Intent(view.getContext(), LearnerHome.class);
                        startActivity(intent);

                    }


                }

            }
        });
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
                        currently.setText(document.getString("currently"));

                        try {

                            List<String> lang = (List<String>) document.get("communication");

                            StringBuffer sb = new StringBuffer();

                            for (String s : lang) {
                                sb.append(s);
                                sb.append(" ");
                            }
                            String str = sb.toString();
                            communication.setText(str);
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

    static String[] splitValues(String value)
    {
        String[] ans = new String[2];
        ans = value.split(" ");
        return ans;
    }

    Boolean makeUser( String userid, String[] language)
    {

        //Create a new user
        Map<String, Object> user = new HashMap<>();
        user.put("name", name.getText().toString());
        user.put("mob",mobile_num.getText().toString());
        user.put("communication", Arrays.asList(language));
        user.put("about_me",about_me.getText().toString());
        user.put("currently",currently.getText().toString());


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
