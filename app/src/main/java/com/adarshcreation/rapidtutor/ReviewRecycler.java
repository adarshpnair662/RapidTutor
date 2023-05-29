package com.adarshcreation.rapidtutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewRecycler extends RecyclerView.Adapter<ReviewRecycler.MyViewHolder> {

    private Context mContext ;
    private List<ReviewHolder> mData ;


    public ReviewRecycler(Context mContext, List<ReviewHolder> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.review_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.reviewer_name.setText(mData.get(position).getName());
        holder.review_content.setText(mData.get(position).getContent());
        holder.reviewRating.setRating(mData.get(position).getRating());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView reviewer_name, review_content;
        RatingBar reviewRating;

        public MyViewHolder(View itemView) {
            super(itemView);

            reviewer_name = (TextView) itemView.findViewById(R.id.reviewer) ;
            review_content = (TextView) itemView.findViewById(R.id.reviewer_text);
            reviewRating = itemView.findViewById(R.id.review_rating);


        }
    }


}
