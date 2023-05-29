package com.adarshcreation.rapidtutor;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class EarningRecycler extends RecyclerView.Adapter<EarningRecycler.MyViewHolder> {

    private Context mContext ;
    private List<Payment> mData ;


    public EarningRecycler(Context mContext, List<Payment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.payment_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String payment_details;
        payment_details ="₹ " + mData.get(position).getAmount() + " received from " +mData.get(position).getName();
        holder.payment_sender.setText(payment_details);

        holder.payment_time.setText(mData.get(position).getTime());

        holder.payment_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) mContext;
                Intent intent = new Intent(mContext,TransactionDetails.class);
                intent.putExtra("transaction",mData.get(position).getTransactionID());
                intent.putExtra("amount", mData.get(position).getAmount());
                intent.putExtra("sender", mData.get(position).getSender());
                intent.putExtra("receiver", mData.get(position).getReceiver());
                intent.putExtra("time", mData.get(position).getTime());
                intent.putExtra("Rname", mData.get(position).getName());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView payment_sender, payment_time;
        RelativeLayout payment_card;

        public MyViewHolder(View itemView) {
            super(itemView);


            payment_time = (TextView) itemView.findViewById(R.id.payment_card_time);
            payment_sender = (TextView) itemView.findViewById(R.id.payment_card_sender);
            payment_card = itemView.findViewById(R.id.payment_card);


        }
    }


}
