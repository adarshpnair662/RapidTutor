package com.adarshcreation.rapidtutor;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TutorProfileRecycler extends RecyclerView.Adapter<TutorProfileRecycler.MyViewHolder> {

    private Context mContext ;
    private List<TutorProfile> mData ;
    String TAG="Something";
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public TutorProfileRecycler(Context mContext, List<TutorProfile> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.tutor_profile_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.price.setText(mData.get(position).getPrice());
        holder.rating.setText(mData.get(position).getRating());
        holder.order_no.setText(mData.get(position).getOrder());
        //Loading image using Picasso
        Picasso.with(mContext).load(mData.get(position).getProfile_pic()).fit().into(holder.profile_pic);

        TinyDB tinyDB = new TinyDB(mContext);
        final String uid = tinyDB.getString("uid");

        final String r_uid = mData.get(position).getUid();

        if (mData.get(position).getStatus())
        {
            holder.status.setVisibility(View.VISIBLE);
        }
        else {
            holder.status.setVisibility(View.INVISIBLE);
        }


        DocumentReference docRef = db.collection("favourite").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                           Boolean fav = document.getBoolean(r_uid);
                           if (fav) {
                               holder.fav_icon.setVisibility(View.INVISIBLE);
                               holder.fav_fill_icon.setVisibility(View.VISIBLE);
                           }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        holder.fav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.fav_icon.setVisibility(View.INVISIBLE);


                String r_uid=mData.get(position).getUid();
                r_uid = r_uid +"_"+uid;
                Map<String, Object> fav = new HashMap<>();
                fav.put("fav", true);

                db.collection("chatrooms").document(r_uid)
                        .set(fav,SetOptions.merge())
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

                holder.fav_fill_icon.setVisibility(View.VISIBLE);

            }
        });

        holder.fav_fill_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.fav_fill_icon.setVisibility(View.INVISIBLE);

                String r_uid=mData.get(position).getUid();
                r_uid = r_uid +"_"+uid;
                Map<String, Object> fav = new HashMap<>();
                fav.put("fav", false);

                db.collection("chatrooms").document(r_uid)
                        .set(fav, SetOptions.merge())
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

                holder.fav_icon.setVisibility(View.VISIBLE);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,TeachProfile.class);
                intent.putExtra("r_uid",mData.get(position).getUid());
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView price,order_no,rating;
        CardView cardView ;
        ImageView profile_pic,status;
        ImageButton fav_icon, fav_fill_icon;

        public MyViewHolder(View itemView) {
            super(itemView);

            price = (TextView) itemView.findViewById(R.id.search_price) ;
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
            profile_pic =(ImageView) itemView.findViewById(R.id.search_profile_pic);
            order_no = (TextView) itemView.findViewById(R.id.search_order_no);
            rating = (TextView) itemView.findViewById(R.id.search_rating_text);
            status = (ImageView) itemView.findViewById(R.id.search_online);
            fav_icon = (ImageButton) itemView.findViewById(R.id.fav_icon);
            fav_fill_icon = (ImageButton) itemView.findViewById(R.id.filled_fav_icon);


        }
    }


}
