package com.fatkhun.travelia.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.GlideApp;
import com.fatkhun.travelia.activity.R;
import com.fatkhun.travelia.model.TourWisata;

import java.util.ArrayList;

public class HomeWisataAdapter extends RecyclerView.Adapter<HomeWisataAdapter.HomeWisataHolder> {
    private static final String BASE_IMAGE_URL = "http://192.168.43.36:8000/uploads/file/";

    private ArrayList<TourWisata> mTourWisatas;

    public HomeWisataAdapter(ArrayList<TourWisata> mTourWisatas) {
        this.mTourWisatas = mTourWisatas;
    }

    @Override
    public HomeWisataAdapter.HomeWisataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_wisata, parent, false);
        return new HomeWisataAdapter.HomeWisataHolder(v);
    }

    @Override
    public void onBindViewHolder(final HomeWisataAdapter.HomeWisataHolder holder, final int position) {
        Uri imageUrl = Uri.parse(BASE_IMAGE_URL + mTourWisatas.get(position).getGambar());

        ImageView ivWisataImage = holder.ivWisataImage;
        TextView tvWisataName = holder.tvWisataName;
        TextView tvWisataLocation = holder.tvWisataLokasi;
//        TextView tvWisataDesc = holder.tvWisataDesc;

        GlideApp.with(ivWisataImage)
                .load(imageUrl)
                .thumbnail(.25f)
                .placeholder(R.drawable.anggrek)
                .into(ivWisataImage);
        tvWisataName.setText(mTourWisatas.get(position).getNama());
        tvWisataLocation.setText(mTourWisatas.get(position).getLokasi());
//        tvWisataDesc.setText(mTourWisatas.get(position).getDeskripsi());

        holder.itemView.setOnClickListener(view -> Toast.makeText(
                holder.itemView.getContext(),
                mTourWisatas.get(position).getNama(),
                Toast.LENGTH_SHORT)
                .show());
    }

    @Override
    public int getItemCount() {
        return mTourWisatas.size();
    }

    public void setHomeWisatas(ArrayList<TourWisata> mTourWisatas) {
        this.mTourWisatas = mTourWisatas;
    }

    static class HomeWisataHolder extends RecyclerView.ViewHolder {

        ImageView ivWisataImage;
        TextView tvWisataName;
        TextView tvWisataLokasi;
        TextView tvWisataDesc;

        HomeWisataHolder(View itemView) {
            super(itemView);
            this.ivWisataImage = itemView.findViewById(R.id.imageView);
            this.tvWisataName = itemView.findViewById(R.id.tvTitle);
            this.tvWisataLokasi = itemView.findViewById(R.id.tvTempat);
//            this.tvWisataDesc = itemView.findViewById(R.id.tvDeskripsihome);
        }
    }
}
