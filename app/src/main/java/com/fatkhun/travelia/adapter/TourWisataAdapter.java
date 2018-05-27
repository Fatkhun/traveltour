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

public class TourWisataAdapter extends RecyclerView.Adapter<TourWisataAdapter.TourWisataHolder> {
    private static final String BASE_IMAGE_URL = "http://192.168.43.36:8000/uploads/file/";

    private ArrayList<TourWisata> mTourWisatas;

    public TourWisataAdapter(ArrayList<TourWisata> mTourWisatas) {
        this.mTourWisatas = mTourWisatas;
    }

    @Override
    public TourWisataAdapter.TourWisataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wisata, parent, false);
        return new TourWisataAdapter.TourWisataHolder(v);
    }

    @Override
    public void onBindViewHolder(final TourWisataHolder holder, final int position) {
        Uri imageUrl = Uri.parse(BASE_IMAGE_URL + mTourWisatas.get(position).getGambar());

        ImageView ivWisataImage = holder.ivWisataImage;
        TextView tvWisataName = holder.tvWisataName;
        TextView tvWisataLocation = holder.tvWisataLokasi;
        TextView tvWisataPrice = holder.tvWisataPrice;

        GlideApp.with(ivWisataImage)
                .load(imageUrl)
                .thumbnail(.25f)
                .placeholder(R.drawable.anggrek)
                .into(ivWisataImage);
        tvWisataName.setText(mTourWisatas.get(position).getNama());
        tvWisataLocation.setText(mTourWisatas.get(position).getLokasi());
        String WisataPrice = "Rp." + String.valueOf(mTourWisatas.get(position).getHarga());
        tvWisataPrice.setText(WisataPrice);

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

    public void setTourWisatas(ArrayList<TourWisata> mTourWisatas) {
        this.mTourWisatas = mTourWisatas;
    }

    static class TourWisataHolder extends RecyclerView.ViewHolder {

        ImageView ivWisataImage;
        TextView tvWisataName;
        TextView tvWisataLokasi;
        TextView tvWisataPrice;

        TourWisataHolder(View itemView) {
            super(itemView);
            this.ivWisataImage = itemView.findViewById(R.id.iv_wisata_list_image);
            this.tvWisataName = itemView.findViewById(R.id.tv_wisata_list_name);
            this.tvWisataLokasi = itemView.findViewById(R.id.tv_wisata_list_lokasi);
            this.tvWisataPrice = itemView.findViewById(R.id.tv_wisata_list_price);
        }
    }
}
