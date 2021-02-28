package com.example.lfg.fragments;

import android.graphics.Movie;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfg.R;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PostFragment extends Fragment {
    Post post;
    private TextView tvBody;
    private ImageView ivEdit;
    private ImageView ivBack;
    FragmentManager fragmentManager;


    public PostFragment() {
        // Required empty public constructor
    }

    public PostFragment(Post post){
        this.post = post;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvBody = view.findViewById(R.id.tvBody);
        ivEdit = view.findViewById(R.id.ivEdit);
        ivBack = view.findViewById(R.id.ivBack);
        tvBody.setText(post.getBody());
        fragmentManager = getActivity().getSupportFragmentManager();
        FirebaseUser author = null;
        /**
         * TODO: finish implementation
         * check if current user is post author
         */
        if(FirebaseAuth.getInstance().getCurrentUser() !=  author){
            ivEdit.setVisibility(View.GONE);
        }

        ivEdit.setVisibility(View.VISIBLE);  //TODO: remove when finished implementing

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new EditFragment(post);
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("post").commit();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.popBackStackImmediate();
            }
        });
    }

}