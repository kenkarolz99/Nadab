package com.example.karokojnr.nadab_hotels;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karokojnr.nadab_hotels.api.HotelService;
import com.example.karokojnr.nadab_hotels.api.RetrofitInstance;
import com.example.karokojnr.nadab_hotels.model.Hotel;
import com.example.karokojnr.nadab_hotels.model.Login;
import com.example.karokojnr.nadab_hotels.utils.Constants;
import com.example.karokojnr.nadab_hotels.utils.SharedPrefManager;
import com.example.karokojnr.nadab_hotels.utils.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button btn_login;
    private ProgressBar mLoading;
    private static final String TAG = "LoginActivity";
    private TextView mRegister;
    private SharedPreferences mSharePrefs;
    private SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    private static Animation shakeAnimation;
    private static LinearLayout loginLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginLayout = (LinearLayout) findViewById(R.id.login_layout);


        progressDialog = new ProgressDialog(this);

        email = (EditText) findViewById ( R.id.et_email );
        password = (EditText) findViewById ( R.id.et_password );
        mRegister = (TextView) findViewById ( R.id.tv_register );
        btn_login = (Button)findViewById ( R.id.btn_login );
        mLoading = (ProgressBar) findViewById(R.id.login_loading);

        mSharePrefs = getApplicationContext().getSharedPreferences(Constants.M_SHARED_PREFERENCE, MODE_PRIVATE);
        editor = mSharePrefs.edit();



        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getApplicationContext (),
                R.anim.shake);

        // Setting text selector over textviews
        @SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            mRegister.setTextColor(csl);
        } catch (Exception e) {
        }



        // Redirect home if is logged in
        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            sendToken();
            goHome();
            finish();
        }

        btn_login.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //first getting the values
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();

                // Check patter for email id
                Pattern p = Pattern.compile(Constants.regEx);

                Matcher m = p.matcher(mEmail);

                // Check for both field is empty or not
                if (mEmail.equals("") || mEmail.length() == 0
                        || mPassword.equals("") || mPassword.length() == 0) {
                    loginLayout.startAnimation(shakeAnimation);

                    {
                        Snackbar snackbar = Snackbar
                                .make(loginLayout, "Enter both credentials!", Snackbar.LENGTH_LONG)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });
                        // Changing message text color
                        snackbar.setActionTextColor(Color.GREEN);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);

                        snackbar.show();

                    }

                }
                // Check if email id is valid or not
                else if (!m.find())
                   /* new CustomToast().Show_Toast(getApplicationContext (), v,
                            "Your Email Id is Invalid.");*/
                {
                    Snackbar snackbar = Snackbar
                            .make(loginLayout, "Your Email Id is Invalid!", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });
                    // Changing message text color
                    snackbar.setActionTextColor(Color.GREEN);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);

                    snackbar.show();

                }

                    // Else do login and do your stuff
                else
                    Toast.makeText(getApplicationContext (), "Logging In....", Toast.LENGTH_SHORT)
                            .show();


                //validating inputs
                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Please enter your username");
                    email.requestFocus();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    email.setError("Enter a valid email");
                    email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(mPassword)) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                    return;
                }
                showProgressDialogWithTitle ( );



            HotelService service = RetrofitInstance.getRetrofitInstance().create(HotelService.class);
            Call<Login> call = service.login(mEmail, mPassword);

            call.enqueue(new Callback<Login> () {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if (response.isSuccessful()) {
                        String token = response.body().getToken();
                        Hotel hotel = response.body().getHotel();
                        // Persist to local storage
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(hotel, token);
                        sendToken();
                       // Start Home activity
                        goHome();
                    } else {
                        mLoading.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Error logging in...", Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialogWithTitle();
                }
                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    mLoading.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Something went wrong...Error message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
            }
        } );
        mRegister.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( LoginActivity.this, RegisterActivity.class );
                startActivity ( intent );
            }
        } );

    }

    private void sendToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("FCM_TOKEN", "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();
                Log.d("FCM_TOKEN", "Token:: "+token);
                utils.sendRegistrationToServer(LoginActivity.this, token);
            }
        });
    }


    private void goHome() {
        Intent intent = new Intent ( LoginActivity.this, MainActivity.class );
        startActivity ( intent );
    }
    // Method to show Progress bar
    private void showProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        //Setting Title
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }
    //Styling for double press back button
    @Override
    public void onBackPressed(){

            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener () {

                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface arg0, int arg1) {
                            finishAffinity();
                        }
                    }).create().show();

    }


}
