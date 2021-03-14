package com.example.lfg.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailFragment extends Fragment {
    private TextView tvCancel;
    private TextView tvSave;
    private EditText etEmail;

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

        MainActivity m = (MainActivity) getActivity();

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
                if(email.isEmpty() || email.length() > 4 || !email.contains("@")){
                    etEmail.setError("Invalid input");
                    return;
                }
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                mUser.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("username", "User email address updated.");
                                }
                            }
                        });
            }
        });
    }
}