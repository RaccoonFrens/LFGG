package com.example.lfg.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class UsernameFragment extends Fragment {

    private TextView tvCancel;
    private TextView tvSave;
    private EditText etUsername;
    User user;


    public UsernameFragment() {
        // Required empty public constructor
    }

    public UsernameFragment(User user){
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_username, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvSave = view.findViewById(R.id.tvSave);
        etUsername = view.findViewById(R.id.etUsername);


        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(username != null)
            username = user.getUsername();

        etUsername.setText(username);

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
                String username = etUsername.getText().toString();
                if(username.isEmpty()){
                    etUsername.setError("Enter a username");
                }

                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName("Jane Q. User")
                        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                        .build();

                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("UsernameChange", "User profile updated.");
                                }
                            }
                        });
            }
        });

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