package com.fatkhun.travelia.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.ApiClient;
import com.fatkhun.travelia.helper.SQLiteHandler;
import com.fatkhun.travelia.helper.SessionManager;
import com.fatkhun.travelia.model.Rating;
import com.fatkhun.travelia.model.User;
import com.fatkhun.travelia.service.BaseApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateWisataActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button btnSendFeedback;

    private EditText etFeedback, etNameFeedback;
    private TextView tvRateMessage, tvNamaWisataRate;

    private float ratedValue;

    ProgressDialog loading;

    Context mContext;
    BaseApiService mApiService;
    String TAG = "@@@";
    private SessionManager session;
    private SQLiteHandler db;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_wisata);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        etFeedback = (EditText) findViewById(R.id.etFeedback);
        etNameFeedback = (EditText) findViewById(R.id.etNameFeedback);

        tvRateMessage = (TextView) findViewById(R.id.tvRatingScale);
        tvNamaWisataRate = (TextView) findViewById(R.id.tvNamaWisataRate);

        btnSendFeedback = (Button) findViewById(R.id.btnSubmit);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratedValue = ratingBar.getRating();
                if(ratedValue == 1){
                    tvRateMessage.setText("ohh ho...");
                }else if(ratedValue == 2){
                    tvRateMessage.setText("Not bad.");
                }else if(ratedValue == 3){
                    tvRateMessage.setText("Ok.");
                }else if(ratedValue == 4){
                    tvRateMessage.setText("Nice");
                }else{
                    tvRateMessage.setText("Awesome");
                }
            }
        });

        Intent intents = getIntent();
        String namaWisata = intents.getStringExtra("nama_wisata");

        tvNamaWisataRate.setText(namaWisata);

        mContext = this;
        mApiService = ApiClient.getClient(getApplicationContext()).create(BaseApiService.class);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
        String apitoken = user.get("api_token");
        Log.i(TAG, "onResponse: Rate " + apitoken);

        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etNameFeedback.getText().toString().trim();
                String message = etFeedback.getText().toString().trim();
                float ratingbar = ratingBar.getRating();

                if (!name.isEmpty() && !message.isEmpty()){
                    requestReview(apitoken, name, ratingbar, message);
                }else if(!isEmptyField(etNameFeedback.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Name is empty",Toast.LENGTH_LONG).show();
                }else if (!isEmptyField(etFeedback.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Message is empty",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Field is empty",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void requestReview(String apitoken, String name, float ratingbar, String message) {
        loading = ProgressDialog.show(mContext, null, "Please Wait...", true, true);
        mApiService.reviewRequest(apitoken, tvNamaWisataRate.getText().toString().trim(), name, ratingbar, message)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            Log.i("debug", "onResponse: Connected");
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("success").equals("true")){
                                    Boolean error = jsonRESULTS.getBoolean("success");

                                    // Check for error node in json
                                    if(!error){
                                        // Error in login. Get the error message
                                        String errorMsg = jsonRESULTS.getString("message");
                                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                                    } else {
                                        String errorMsg = jsonRESULTS.getString("message");
                                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RateWisataActivity.this, TabNavigationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                    }
                                }else {
                                    // Error in login. Get the error message
                                    String errorMsg = jsonRESULTS.getString("message");
                                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                String error_message = e.getMessage() + ", try again";
                                Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("debug", "onResponse: Review Failed");
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                        Toast.makeText(mContext, "Problem Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     *
     * @param yourField
     * Method ini digunakan untuk validasi field kosong atau tidak
     */
    private boolean isEmptyField(String yourField){
        return !TextUtils.isEmpty(yourField);
    }
}
