package com.adarshcreation.rapidtutor;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatRecycler extends RecyclerView.Adapter<ChatRecycler.MyViewHolder> {

    private Context mContext ;
    private List<Chat> mData ;
    String document,r_uid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="Something";

    public ChatRecycler(Context mContext, List<Chat> mData, String document, String r_uid) {
        this.mContext = mContext;
        this.mData = mData;
        this.document = document;
        this.r_uid = r_uid;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.chat_box_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        TinyDB tinyDB = new TinyDB(mContext);
        final String uid = tinyDB.getString("uid");
        final String userType = tinyDB.getString("type");

        if (mData.get(position).getUid() != null && uid != null) {
            if (mData.get(position).getOffer()) {

                String price ="₹ " + mData.get(position).getAmount();

                if (mData.get(position).getStatus()) {

                    if (mData.get(position).getPay()) {

                if (uid.equals(mData.get(position).getUid())) {
                    holder.receiver.setVisibility(View.GONE);
                    holder.Roffer.setVisibility(View.GONE);

                    holder.Smessage.setText(mData.get(position).getMessage());
                    holder.Sprice.setText(price);
                    holder.Saccept.setText("Accepted :");

                } else {
                    holder.sender.setVisibility(View.GONE);
                    holder.Soffer.setVisibility(View.GONE);
                    holder.Rreject.setVisibility(View.GONE);

                    holder.Rmessage.setText(mData.get(position).getMessage());
                    holder.Rprice.setText(price);
                    holder.Raccept.setText("Accepted");
                    holder.Raccept.setClickable(false);
                    holder.Raccept.setEnabled(false);
                }
            } else {
                if (uid.equals(mData.get(position).getUid())) {
                    holder.receiver.setVisibility(View.GONE);
                    holder.Roffer.setVisibility(View.GONE);

                    holder.Smessage.setText(mData.get(position).getMessage());
                    holder.Sprice.setText(price);
                    holder.Saccept.setText("Rejected :");
                } else {
                    holder.sender.setVisibility(View.GONE);
                    holder.Soffer.setVisibility(View.GONE);
                    holder.Raccept.setVisibility(View.GONE);

                    holder.Rmessage.setText(mData.get(position).getMessage());
                    holder.Rprice.setText(price);
                    holder.Rreject.setText("Rejected");
                    holder.Rreject.setClickable(false);
                    holder.Rreject.setEnabled(false);
                }
            }
        } //if nothing done show offer else part here
                else {
                    if (uid.equals(mData.get(position).getUid())) {
                        holder.receiver.setVisibility(View.GONE);
                        holder.Roffer.setVisibility(View.GONE);

                        holder.Smessage.setText(mData.get(position).getMessage());
                        holder.Sprice.setText(price);


                    } else {
                        holder.sender.setVisibility(View.GONE);
                        holder.Soffer.setVisibility(View.GONE);

                        holder.Rmessage.setText(mData.get(position).getMessage());
                        holder.Rprice.setText(price);
                    }
                }
                }
                else {

                    holder.Soffer.setVisibility(View.GONE);
                    holder.Roffer.setVisibility(View.GONE);

                    if (uid.equals(mData.get(position).getUid())) {
                        holder.receiver.setVisibility(View.GONE);
                        holder.Smessage.setText(mData.get(position).getMessage());
                    } else {
                        holder.sender.setVisibility(View.GONE);
                        holder.Rmessage.setText(mData.get(position).getMessage());
                    }

                }
            }

        //button clicks
        holder.Rreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> chat = new HashMap<>();
                chat.put("status", true);
                chat.put("payment", false);

                db.collection("chatrooms").document(document).collection("chat").document(mData.get(position).getDoc())
                        .set(chat, SetOptions.merge())
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
        });

        holder.Raccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userType.equals("learn"))
                {
                    Intent intent = new Intent(mContext, PaymentHistory.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("amount",Float.toString( mData.get(position).getAmount()));
                    intent.putExtra("r_uid", r_uid);
                    intent.putExtra("roomid", document);
                    intent.putExtra("chatid",mData.get(position).getDoc());
                    intent.putExtra("reward", false);
                    mContext.startActivity(intent);
                }
                else {

                    Map<String, Object> chat = new HashMap<>();
                    chat.put("status", true);
                    chat.put("payment", false);

                    db.collection("chatrooms").document(document).collection("chat").document(mData.get(position).getDoc())
                            .set(chat, SetOptions.merge())
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout sender,receiver;
        TextView Smessage,Rmessage,Sprice,Rprice,Raccept,Rreject,Saccept;
        LinearLayout Soffer,Roffer;

        public MyViewHolder(View itemView) {
            super(itemView);

            sender = (RelativeLayout) itemView.findViewById(R.id.chat_box_sender);
            receiver = (RelativeLayout) itemView.findViewById(R.id.chat_box_receiver);
            Smessage = (TextView) itemView.findViewById(R.id.chat_content_sender);
            Rmessage = (TextView) itemView.findViewById(R.id.chat_content_receiver);

            Saccept = itemView.findViewById(R.id.Saccept);
            Sprice = itemView.findViewById(R.id.sender_price);
            Rprice = itemView.findViewById(R.id.receiver_price);
            Raccept = itemView.findViewById(R.id.receiver_accept);
            Rreject = itemView.findViewById(R.id.receiver_reject);
            Soffer = itemView.findViewById(R.id.sender_offer_box);
            Roffer = itemView.findViewById(R.id.receiver_offer_box);
        }
    }


}
