package com.fatkhun.travelia.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TourWisata implements Parcelable {
    private int id;
    private String nama;
    private String deskripsi;
    private String info;
    private String penginapan;
    private String transportasi;
    private String makan;
    private String lokasi;
    private String gambar;
    private int tiket;
    private int harga;

    public TourWisata(int id, String nama, String deskripsi, String info, String penginapan, String transportasi, String makan, String lokasi, String gambar, int tiket, int harga) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.info = info;
        this.penginapan = penginapan;
        this.transportasi = transportasi;
        this.makan = makan;
        this.lokasi = lokasi;
        this.gambar = gambar;
        this.tiket = tiket;
        this.harga = harga;
    }

    protected TourWisata(Parcel in){
        id = in.readInt();
        nama = in.readString();
        deskripsi = in.readString();
        info = in.readString();
        penginapan = in.readString();
        transportasi = in.readString();
        makan = in.readString();
        lokasi = in.readString();
        gambar = in.readString();
        tiket = in.readInt();
        harga = in.readInt();
    }

    public static final Creator<TourWisata> CREATOR = new Creator<TourWisata>() {
        @Override
        public TourWisata createFromParcel(Parcel source) {
            return new TourWisata(source);
        }

        @Override
        public TourWisata[] newArray(int size) {
            return new TourWisata[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getInfo() {
        return info;
    }

    public String getPenginapan() {
        return penginapan;
    }

    public String getTransportasi() {
        return transportasi;
    }

    public String getMakan() {
        return makan;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getGambar() {
        return gambar;
    }

    public int getTiket() {
        return tiket;
    }

    public int getHarga() {
        return harga;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeInt(id);
        parcel.writeString(nama);
        parcel.writeString(deskripsi);
        parcel.writeString(info);
        parcel.writeString(penginapan);
        parcel.writeString(transportasi);
        parcel.writeString(makan);
        parcel.writeString(lokasi);
        parcel.writeString(gambar);
        parcel.writeInt(tiket);
        parcel.writeInt(harga);
    }
}
