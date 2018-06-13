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
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.ApiClient;
import com.fatkhun.travelia.model.User;
import com.fatkhun.travelia.service.BaseApiService;
import com.fatkhun.travelia.helper.SQLiteHandler;
import com.fatkhun.travelia.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView link_btnRegister;
    ProgressDialog loading;

    Context mContext;
    BaseApiService mApiService;
    String TAG = "@@@";
    private SessionManager session;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        mApiService = ApiClient.getClient(getApplicationContext()).create(BaseApiService.class); // meng-init yang ada di package apihelper
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        link_btnRegister = (TextView) findViewById(R.id.link_signup);

        //SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(getApplicationContext(), TabNavigationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    requestLogin(email, password);
                }if (!isValidateEmail(etEmail.getText().toString())){
                    etEmail.setError("Enter a valid email address");
                }else if (!isEmptyField(etPassword.getText().toString()) || password.length() < 4 || password.length() > 10){
                    etPassword.setError("Between 4 and 10 alphanumeric characters");
                }
            }
        });

        link_btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

    }

    private void requestLogin(final String email, final String password){
            loading = ProgressDialog.show(mContext, null, "Please Wait...", true, true);
            mApiService.loginRequest(etEmail.getText().toString(), etPassword.getText().toString())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()){
                                loading.dismiss();
                                try {
                                    JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                    if (jsonRESULTS.getString("success").equals("true")){
                                        if (jsonRESULTS.getString("password") == String.valueOf(jsonRESULTS.getString("password"))){

                                            Boolean error = jsonRESULTS.getBoolean("success");
                                            if(!error){
                                                session.setLogin(false);
                                                String errorMsg = jsonRESULTS.getString("message");
                                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                                            }else{
                                                // Create login session true
                                                session.setLogin(true);
                                                // user successfully logged in
                                                String errorMsg = jsonRESULTS.getString("message");


                                                // Now store the user in SQLite
                                                String username = jsonRESULTS.getString("username");
                                                String email = jsonRESULTS.getString("email");
                                                String password = jsonRESULTS.getString("password");
                                                String api_token = jsonRESULTS.getString("api_token");

                                                // Inserting row in users table in SQLite
                                                db.addUser(username, email, password, api_token);
                                                userLogin(username, email, password, api_token);
                                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    } else {
                                        // Jika login gagal
                                        String error_message = jsonRESULTS.getString("message");
                                        Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    String error_message = e.getMessage() + ", try again";
                                    Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                loading.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("debug", "onFailure: ERROR > " + t.toString());
                            loading.dismiss();
                        }
                    });

    }

    public void userLogin(String username, String email, String password, String api_token) {

        Intent intent = new Intent(mContext, TabNavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("api_token", api_token);
        startActivity(intent);
        finish();
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
    /**
     *
     * @param password
     * @param retypePassword
     * method ini digunakan untuk mencocokan password dengan retype password
     */
    private boolean isMatch(String password, String retypePassword){
        return password.equals(retypePassword);
    }
}
