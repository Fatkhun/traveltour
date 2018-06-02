package com.fatkhun.travelia.model;

import com.google.gson.annotations.SerializedName;

public class User{

        @SerializedName("api_key")
        String apiKey;

        @SerializedName("id_user")
        int id_user;

        @SerializedName("username")
        String username;

        @SerializedName("password")
        String password;

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey(String apitoken) {
            return apiKey;
        }
}
