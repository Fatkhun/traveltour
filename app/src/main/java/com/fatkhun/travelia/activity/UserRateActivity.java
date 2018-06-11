package com.fatkhun.travelia.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.ApiClient;
import com.fatkhun.travelia.adapter.RatingAdapter;
import com.fatkhun.travelia.helper.SQLiteHandler;
import com.fatkhun.travelia.helper.SessionManager;
import com.fatkhun.travelia.model.Rating;
import com.fatkhun.travelia.service.BaseApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list_rate);

        // getSupportFragmentManager() is use to return the fragment manager for interacting with
        // fragment associated with this activity.
        // beginTransaction() is use to start operations on the current fragment manager.
        // replace() is use to replace the the content with mountain list fragment.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ListRateFragment())
                .commit();

    }
}
