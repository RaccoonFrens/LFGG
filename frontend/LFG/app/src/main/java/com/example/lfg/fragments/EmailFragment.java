package com.example.lfg.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailFragment extends Fragment {
    private TextView tvCancel;
    private TextView tvSave;
    private EditText etEmail;

    MainActivity m;

    public EmailFragment() {
        // Required empty public constructor
    }

    public EmailFragment(User user) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvSave = view.findViewById(R.id.tvSave);
        etEmail = view.findViewById(R.id.etEmail);
        etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        m = (MainActivity) getActivity();

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m.fragmentManager.popBackStackImmediate();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                if(email.isEmpty() || email.length() < 4 || !email.contains("@")){
                    etEmail.setError("Invalid input");
                    return;
                }

                authenticate(email);

            }
        });
    }

    private void authenticate(String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder enterCredentials = new AlertDialog.Builder(getContext());
        enterCredentials.setTitle("Enter Password");

        final EditText aPassword = new EditText(getContext());

        aPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        enterCredentials.setView(aPassword);


        enterCredentials.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mPassword = aPassword.getText().toString();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), mPassword);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("authenticate", "User re-authenticated.");
                                if(!task.isSuccessful()){
                                    Toast.makeText(getContext(), "incorrect password", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else{
                                    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                    mUser.updateEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.i("username", "User email address updated.");
                                                        m.fragmentManager.popBackStackImmediate();
                                                    } else{
                                                        Log.i("email", email);
                                                        Toast.makeText(getContext(), "Failed to update email", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });

            }
        });
        enterCredentials.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        enterCredentials.show();


    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();


    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}