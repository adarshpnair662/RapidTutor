package com.adarshcreation.rapidtutor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatHistoryRecycler extends RecyclerView.Adapter<ChatHistoryRecycler.MyViewHolder> {

    private Context mContext ;
    private List<TutorProfile> mData ;
    String TAG="Something";
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ChatHistoryRecycler(Context mContext, List<TutorProfile> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.chat,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Picasso.with(mContext).load(mData.get(position).getProfile_pic()).fit().into(holder.profile_pic);

        holder.name.setText(mData.get(position).getPrice());

        TinyDB tinyDB = new TinyDB(mContext);

        if (tinyDB.getString("type").equals("learn")) {
        holder.rating.setRating(Float.parseFloat(mData.get(position).getRating()));
        //Loading image using Picasso

        final String uid = tinyDB.getString("uid");

        final String r_uid = mData.get(position).getUid();

        if (mData.get(position).getStatus())
        {
            holder.status.setVisibility(View.VISIBLE);
        }
        else {
            holder.status.setVisibility(View.INVISIBLE);
        }


        String roomid = r_uid + "_" + uid;

                    DocumentReference docRef = db.collection("chatrooms").document(roomid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            Boolean fav = document.getBoolean("fav");
                            try {
                                if (fav) {
                                    holder.fav_icon.setVisibility(View.INVISIBLE);
                                    holder.fav_fill_icon.setVisibility(View.VISIBLE);
                                }
                            }
                            catch (Exception e)
                            {
                                
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
        else{

            holder.status.setVisibility(View.INVISIBLE);
            holder.rating.setVisibility(View.INVISIBLE);
            holder.fav_icon.setVisibility(View.INVISIBLE);
        }

        holder.fav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.fav_icon.setVisibility(View.INVISIBLE);

                TinyDB tinyDB = new TinyDB(mContext);

                String roomid = mData.get(position).getUid() + "_" + tinyDB.getString("uid");

                Map<String, Object> fav = new HashMap<>();
                fav.put("fav", true);

                db.collection("chatrooms").document(roomid)
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

                holder.fav_fill_icon.setVisibility(View.VISIBLE);

            }
        });

        holder.fav_fill_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.fav_fill_icon.setVisibility(View.INVISIBLE);
                TinyDB tinyDB = new TinyDB(mContext);

                String roomid = mData.get(position).getUid() + "_" + tinyDB.getString("uid");

                Map<String, Object> fav = new HashMap<>();
                fav.put("fav", false);

                db.collection("chatrooms").document(roomid)
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
                holder.fav_icon.setVisibility(View.VISIBLE);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,ChatRoom.class);
                intent.putExtra("r_uid",mData.get(position).getUid());
                mContext.startActivity(intent);

            }
        });

        String roomid;

        if (tinyDB.getString("type").equals("learn")) {
             roomid = mData.get(position).getUid() + "_" + tinyDB.getString("uid");
        }
        else {
            roomid =  tinyDB.getString("uid") + "_" + mData.get(position).getUid();
        }

        final DocumentReference docRef = db.collection("chatrooms").document(roomid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    String type;
                    TinyDB tinyDB = new TinyDB(mContext);
                    type = tinyDB.getString("type");
                    if (type.equals("learn"))
                    {
                        type="learn_read";
                    }
                    else {
                        type="teach_read";
                    }
                    if (!(snapshot.getBoolean(type)))
                    {
                        holder.chatRead.setVisibility(View.VISIBLE);
                    }else {
                        holder.chatRead.setVisibility(View.INVISIBLE);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RatingBar rating;
        RelativeLayout cardView ;
        ImageView profile_pic,chatRead;
        ImageButton fav_icon, fav_fill_icon;
        TextView name,status;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.chat_history_chat_card);
            profile_pic =(ImageView) itemView.findViewById(R.id.chat_history_profile_pic);;
            rating = itemView.findViewById(R.id.chat_history_teach_rating);
            status =  itemView.findViewById(R.id.chat_history_online);
            fav_icon = (ImageButton) itemView.findViewById(R.id.chat_history_fav_icon);
            fav_fill_icon = (ImageButton) itemView.findViewById(R.id.chat_history_filled_fav_icon);
            name = itemView.findViewById(R.id.chat_history_name);
            chatRead = itemView.findViewById(R.id.chat_read);


        }
    }


}
