package com.adarshcreation.rapidtutor;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationRecycler extends RecyclerView.Adapter<NotificationRecycler.MyViewHolder> {

    private Context mContext ;
    private List<Book> mData ;
    private int n;

    String TAG="Something";

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public NotificationRecycler(Context mContext, List<Book> mData,int n) {
        this.mContext = mContext;
        this.mData = mData;
        this.n=n;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.notification_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.name.setText(mData.get(position).getName());
        String time;
        time = mData.get(position).getBdate() + " " + mData.get(position).getBtime();
        holder.timing.setText(time);

        switch (n)
        {
            case 1:
                holder.delete.setVisibility(View.VISIBLE);
                holder.cancel.setVisibility(View.GONE);
                break;
            case 2:
                holder.delete.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.VISIBLE);
                if (!(mData.get(position).getRead()))
                {
                    holder.card.setVisibility(View.GONE);
                }
                break;
        }

        if (mData.get(position).getRead()) {
            if (!(mData.get(position).getStatus())) {
                holder.reject.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
            }
        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> city = new HashMap<>();
                city.put("read", true);
                city.put("status", false);

                db.collection("booking").document(mData.get(position).getDocID())
                        .set(city, SetOptions.merge())
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

                Toast.makeText(mContext,"Cancelled",Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(mContext, Notification.class);
                ((Activity)mContext).startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                ((Activity)mContext).finish();
            }

        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("booking").document(mData.get(position).getDocID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

                Toast.makeText(mContext,"Deleted",Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(mContext, Notification.class);
                ((Activity)mContext).startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                ((Activity)mContext).finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,timing, reject;
        ImageButton cancel, delete ;
        LinearLayout card;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.learner_name) ;
            timing = itemView.findViewById(R.id.booking_time);
            delete = itemView.findViewById(R.id.cancel_booking_icon);
            cancel = itemView.findViewById(R.id.today_cancel_icon);
            card = itemView.findViewById(R.id.notification_card);
            reject = itemView.findViewById(R.id.booking_reject);
        }
    }


}
