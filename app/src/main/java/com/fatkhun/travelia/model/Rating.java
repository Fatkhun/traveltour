package com.fatkhun.travelia.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Rating implements Parcelable {
    private int id;
    private int user_id;
    private String nama_wisata;
    private String nama;
    private int rating;
    private String review;

    public Rating(int id, int user_id, String nama_wisata, String nama, int rating, String review) {
        this.id = id;
        this.user_id = user_id;
        this.nama_wisata = nama_wisata;
        this.nama = nama;
        this.rating = rating;
        this.review = review;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNama_wisata() {
        return nama_wisata;
    }

    public void setNama_wisata(String nama_wisata) {
        this.nama_wisata = nama_wisata;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public static Creator<Rating> getCREATOR() {
        return CREATOR;
    }

    protected Rating(Parcel in) {
        id = in.readInt();
        user_id = in.readInt();
        nama_wisata = in.readString();
        nama = in.readString();
        rating = in.readInt();
        review = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(user_id);
        dest.writeString(nama_wisata);
        dest.writeString(nama);
        dest.writeInt(rating);
        dest.writeString(review);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Rating> CREATOR = new Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
