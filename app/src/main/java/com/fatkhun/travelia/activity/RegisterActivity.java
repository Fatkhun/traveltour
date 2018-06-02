package com.fatkhun.travelia.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.ApiClient;
import com.fatkhun.travelia.service.BaseApiService;
import com.fatkhun.travelia.helper.SQLiteHandler;
import com.fatkhun.travelia.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etNama;
    EditText etEmail;
    EditText etPassword;
    Button btnRegister;
    ProgressDialog loading;

    Context mContext;
    BaseApiService mApiService;
    private SessionManager session;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNama = (EditText) findViewById(R.id.etNama);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        mContext = this;
        mApiService = ApiClient.getClient(getApplicationContext()).create(BaseApiService.class);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            startActivity(new Intent(RegisterActivity.this, TabNavigationActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etNama.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    requestRegister(username, email, password);
                }else if(!isEmptyField(etNama.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Name is empty",Toast.LENGTH_LONG).show();
                }else if (!isValidateEmail(etEmail.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Email is empty",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Password is empty",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void requestRegister(String username, String email, String password ){
            loading = ProgressDialog.show(mContext, null, "Please Wait...", true, true);
            mApiService.registerRequest(etNama.getText().toString(),
                    etEmail.getText().toString(),
                    etPassword.getText().toString())
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

                                            // user successfully register
                                            // User successfully stored in MySQL
                                            // Now store the user in sqlite

                                            String username = jsonRESULTS.getString("username");
                                            String email = jsonRESULTS.getString("email");
                                            String password = jsonRESULTS.getString("password");
                                            String api_token = jsonRESULTS.getString("api_token");

                                            // Inserting row in users table in SQLite
                                            db.addUser(username, email, password, api_token);

                                            Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_SHORT).show();

                                            // Inserting row in users table
                                            userLogin(username, email, password, api_token);
                                        }
                                    }else {
                                        // Error in login. Get the error message
                                        String errorMsg = jsonRESULTS.getString("message");
                                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    String error_message = e.getMessage() + ", duplicate email and try again";
                                    Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.i("debug", "onResponse: Register Failed or duplicate email");
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

    public void userLogin(String username, String email, String password, String api_token) {

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("api_token", api_token);
        startActivity(intent);
    }

    /**
     *
     * @param email
     * Method dibawah ini untuk validasi email kosong atau salah
     */
    private boolean isValidateEmail(String email){
        return !TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
