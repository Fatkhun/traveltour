package com.fatkhun.travelia.model;


import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("id_rating")
    private int id_rating;

    @SerializedName("nama")
    private String nama;

    @SerializedName("rating")
    private int rating;

    @SerializedName("review")
    private String review;

    public int getId_rating() {
        return id_rating;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setId_rating(int id_rating) {
        this.id_rating = id_rating;
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
}
