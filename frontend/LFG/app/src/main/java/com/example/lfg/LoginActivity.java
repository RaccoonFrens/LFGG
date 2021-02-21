package com.example.lfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LogInActivity";
    private static final String EMAIL_SUCCESS = "signInWithEmail:success";
    private static final String TOKEN_FAILURE = "failed to retrieve token";
    private static final String EMAIL_FAILURE = "signInWithEmail:failure";
    private static final String AUTHENTICATION_FAILURE = "Authentication failed.";
    public static final String BLANK_FIELD = "Field cannot be left blank";
    public static final String INVALID_EMAIL = "Please enter a valid email";

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;

    private FirebaseAuth mAuth;

    String userToken;

    //used to save user's data within the app
    SharedPreferences prefs;
    SharedPreferences.Editor edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        mAuth = FirebaseAuth.getInstance();

        prefs = getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userSignIn(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterActivity();
            }
        });

    }


    @Override
    public void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            FirebaseUser mUser = mAuth.getCurrentUser();

            mUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                userToken = task.getResult().getToken();
                                edit.putString("userToken", userToken);
                                edit.apply();
                                openMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Internal Token Error", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }

    }


    private void userSignIn(String email, String password) {
        if(!validInput()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, EMAIL_SUCCESS);
                            FirebaseUser user = task.getResult().getUser();

                            FirebaseUser mUser = mAuth.getCurrentUser();
                            mUser.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                userToken = task.getResult().getToken();
                                                 edit.putString("userToken", userToken);
                                                edit.apply();
                                                openMainActivity();
                                            } else {
                                                Log.w(TAG, TOKEN_FAILURE);
                                            }
                                        }
                                    });


                        } else {
                            Log.w(TAG, EMAIL_FAILURE, task.getException());
                            Toast.makeText(LoginActivity.this, AUTHENTICATION_FAILURE,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private boolean validInput() {
        boolean valid = true;

        if(etEmail.getText().toString().isEmpty()){
            valid = false;
            etEmail.setError(BLANK_FIELD);
        }

        else if(!(etEmail.getText().toString().contains("@"))){
            valid = false;
            etEmail.setError(INVALID_EMAIL);
        }

        if(etPassword.getText().toString().isEmpty()){
            valid = false;
            etPassword.setError(BLANK_FIELD);
        }

        return valid;
    }


}