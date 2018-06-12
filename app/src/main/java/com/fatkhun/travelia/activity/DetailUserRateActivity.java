package com.fatkhun.travelia.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.ApiClient;
import com.fatkhun.travelia.helper.SQLiteHandler;
import com.fatkhun.travelia.service.BaseApiService;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailUserRateActivity extends AppCompatActivity {

    TextView namaWisata, namaUser, rateUser, feedbackUser;
    Button tvDeleteRate;
    private SQLiteHandler db;
    ProgressDialog loading;
    BaseApiService mApiService;
    String TAG = "DetailUserRateActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user_rate);

        mApiService = ApiClient.getClient(getApplicationContext()).create(BaseApiService.class);
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
        String apitoken = user.get("api_token");
        Log.i(TAG, "onResponse: Rate " + apitoken);

        namaWisata = (TextView) findViewById(R.id.tv_nama_rate);
        namaUser = (TextView) findViewById(R.id.tv_user_rate);
        rateUser = (TextView) findViewById(R.id.tv_stars_rate);
        feedbackUser = (TextView) findViewById(R.id.tv_feedback_rate);
        tvDeleteRate = (Button) findViewById(R.id.tv_delete_rate);

        // get intent from UserRate
        Intent intent = getIntent();
        int mId = intent.getIntExtra("id",0);
        String namaWisatas = intent.getStringExtra("nama_wisata");
        String namaUsers = intent.getStringExtra("nama");
        int rateUsers = intent.getIntExtra("rating", 0);
        String feedbackUsers = intent.getStringExtra("review");

        namaWisata.setText(namaWisatas);
        namaUser.setText(namaUsers);
        rateUser.setText(String.valueOf(rateUsers));
        feedbackUser.setText(feedbackUsers);

        tvDeleteRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                requestDeleteRate();
                loading = ProgressDialog.show(DetailUserRateActivity.this, null, "Please Wait...", true, true);
                // SQLite database handler

                mApiService.deteleRate(mId, apitoken).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            loading.dismiss();
                            Toast.makeText(DetailUserRateActivity.this, "Success delete", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DetailUserRateActivity.this, UserRateActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            loading.dismiss();
                            Toast.makeText(DetailUserRateActivity.this, "Failed delete", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading.dismiss();
                        Toast.makeText(DetailUserRateActivity.this, "Problem connection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
