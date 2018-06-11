package com.fatkhun.travelia.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.activity.R;
import com.fatkhun.travelia.model.Rating;
import com.fatkhun.travelia.model.TourWisata;

import java.util.ArrayList;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private ArrayList<Rating> mRateWisatas;

    public RatingAdapter(ArrayList<Rating> mRateWisatas) {
        this.mRateWisatas = mRateWisatas;
    }

    @Override
    public RatingAdapter.RatingViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_rate, parent, false);
        return new RatingAdapter.RatingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RatingAdapter.RatingViewHolder holder, int position) {

        TextView tvWisataNama = holder.namaWisata;
        TextView tvWisataNamaUser = holder.namaUser;
        TextView tvWisataRateStar = holder.rateStar;
        TextView tvWisataFeedback = holder.feedbackUser;

        tvWisataNama.setText(mRateWisatas.get(position).getNama_wisata());
        tvWisataNamaUser.setText(mRateWisatas.get(position).getNama());
        tvWisataRateStar.setText(Integer.toString(mRateWisatas.get(position).getRating()));
        tvWisataFeedback.setText(mRateWisatas.get(position).getReview());

        holder.itemView.setOnClickListener(view -> Toast.makeText(
                holder.itemView.getContext(),
                mRateWisatas.get(position).getNama(),
                Toast.LENGTH_SHORT)
                .show());
    }

    @Override
    public int getItemCount() {
        return mRateWisatas.size();
    }

    public class RatingViewHolder extends RecyclerView.ViewHolder {
        private TextView namaWisata, namaUser , rateStar, feedbackUser;
        public RatingViewHolder(View view){
            super(view);

            namaWisata = (TextView) view.findViewById(R.id.tv_nama_wisata_rate);
            namaUser = (TextView) view.findViewById(R.id.tv_nama_user_rate);
            rateStar = (TextView) view.findViewById(R.id.tv_wisata_stars_rate);
            feedbackUser = (TextView) view.findViewById(R.id.tv_wisata_feedback_rate);
        }
    }
}
