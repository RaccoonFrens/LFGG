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

import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText etEmail;
    private EditText etPassword;
    private EditText etUsername;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private Button btnLogin;

    private FirebaseAuth mAuth;

    String userToken;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

        prefs = getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccount(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });


    }



    private void registerAccount(String email, String password) {
        if(!validInput()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser mUser = mAuth.getCurrentUser();
                            mUser.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                String username = etUsername.getText().toString();
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(username).build();
                                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "username set");
                                                        }
                                                    }
                                                });


                                               FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                String uid = mAuth.getCurrentUser().getUid();
                                                DatabaseReference currUserRef = database.getReference("users").child(uid);
                                                User mUser = new User(uid, username, email);
                                                currUserRef.setValue(mUser);

                                                openLoginActivity();
                                            } else {
                                                Log.w(TAG, "Failed to retrieve token");
                                            }
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private boolean validInput() {
        boolean valid = true;

        if(etEmail.getText().toString().isEmpty()){
            valid = false;
            etEmail.setError("Field cannot be left blank");
        }

        else if(!(etEmail.getText().toString().contains("@"))){
            valid = false;
            etEmail.setError("Please enter a valid email");
        }

        if(etPassword.getText().toString().isEmpty()){
            valid = false;
            etPassword.setError("Field cannot be left blank");
        }

        if(etPassword.getText().toString().length() < 6){
            valid = false;
            etPassword.setError("Password must be longer than 6 characters");
        }

        if(etUsername.getText().toString().isEmpty()){
            valid = false;
            etUsername.setError("Field cannot be left blank");
        }

        if(etConfirmPassword.getText().toString().isEmpty()){
            valid = false;
            etConfirmPassword.setError("Field cannot be left empty");
        }

        if(!etConfirmPassword.getText().toString().equals(etPassword.getText().toString()) &&
            !etPassword.getText().toString().isEmpty() && !etConfirmPassword.getText().toString().isEmpty()){
            etConfirmPassword.setError("Passwords do not match!");
            etPassword.setError("Passwords do not match!");
            valid = false;
        }

        return valid;
    }
}