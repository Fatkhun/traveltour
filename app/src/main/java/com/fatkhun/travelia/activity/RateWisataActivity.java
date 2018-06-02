package com.fatkhun.travelia.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.PrefUtils;
import com.fatkhun.travelia.helper.SQLiteHandler;
import com.fatkhun.travelia.helper.SessionManager;
import com.fatkhun.travelia.service.BaseApiService;

public class RateWisataActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button btnSendFeedback;

    private EditText etFeedback, etNameFeedback;
    private TextView tvRateMessage;

    private float ratedValue;

    ProgressDialog loading;

    Context mContext;
    BaseApiService mApiService;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_wisata);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        etFeedback = (EditText) findViewById(R.id.etFeedback);
        etNameFeedback = (EditText) findViewById(R.id.etNameFeedback);

        tvRateMessage = (TextView) findViewById(R.id.tvRatingScale);

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

        /**
         * Check for stored Api Key in shared preferences
         * If not present, make api call to register the user
         * This will be executed when app is installed for the first time
         * or data is cleared from settings
         * */
        if (TextUtils.isEmpty(PrefUtils.getApiKey(this))) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // user is already registered, fetch all notes

        }

        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etNameFeedback.getText().toString().trim();
                String message = etFeedback.getText().toString().trim();
                if (!name.isEmpty() && !message.isEmpty()){
                    requestReview(name, message);
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

    private void requestReview(String name, String message) {
        
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
