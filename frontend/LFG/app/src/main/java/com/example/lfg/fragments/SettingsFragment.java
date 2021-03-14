package com.example.lfg.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    private TextView tvUsernameArrow;
    private TextView tvEmailArrow;

    private LinearLayout layoutUsername;
    private LinearLayout layoutEmail;
    private LinearLayout layoutPassword;

    String userId;
    String username;
    String email;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    public SettingsFragment(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsernameArrow = view.findViewById(R.id.tvUsernameArrow);
        tvEmailArrow = view.findViewById(R.id.tvEmailArrow);
        layoutEmail = view.findViewById(R.id.layoutEmail);
        layoutUsername = view.findViewById(R.id.layoutUsername);
        layoutPassword = view.findViewById(R.id.layoutPassword);

        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        username = firebaseUser.getDisplayName();
        if(username.isEmpty())
            username = prefs.getString("username", "username");
        email = firebaseUser.getEmail();
        tvUsernameArrow.setText(username);
        tvEmailArrow.setText(email);
        MainActivity m = (MainActivity) getActivity();
        User user = new User(userId, username, email);

        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new EmailFragment(user);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("settings").commit();
            }
        });

        layoutUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UsernameFragment(user);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("settings").commit();
            }
        });

        layoutPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PasswordFragment(user);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("settings").commit();
            }
        });

    }



}