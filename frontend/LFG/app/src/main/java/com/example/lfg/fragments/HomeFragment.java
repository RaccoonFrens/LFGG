package com.example.lfg.fragments;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lfg.R;
import com.example.lfg.adapters.PostsAdapter;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment {
    public static final String TAG = "PostFragment";
    private RecyclerView rvPosts;
    private PostsAdapter postsAdapter;
    private FirebaseDatabase database;

    List<Post> posts;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        posts = new ArrayList<>();

        //add new post then append to post array list
        //set body
        //set tag
        //set size
        /*
        Post post = new Post();
        post.setBody("RacconFrens");
        post.setTag("test1");
        post.setSize(3);
        post.setLogoName("amongUs_logo.png");
        posts.add(post);
        posts.add(post);
        posts.add(post);

        Post post2 = new Post();
        post2.setBody("test2");
        post2.setTag("test2");
        post2.setSize(12);
        post2.setLogoName("pubg_logo.png");
        posts.add(post2);

        Post post3 = new Post();
        post3.setBody("test3");
        post3.setTag("test3");
        post3.setSize(4);
        post3.setLogoName("fortnite_logo.png");
        posts.add(post3);

        Post post4 = new Post();
        post4.setBody("RacconFrens");
        post4.setTag("test4");
        post4.setSize(7);
        post4.setLogoName("minecraft_logo.png");
        posts.add(post4);
        */

        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Post post = posts.get(position);
                Fragment fragment = new PostFragment(post);
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("home").commit();

                //TODO: show post details
            }
        };
        postsAdapter = new PostsAdapter(getContext(), posts, itemClickListener);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPosts.setAdapter(postsAdapter);
        database = FirebaseDatabase.getInstance();
        loadData();
    }



    private void loadData(){
        database.goOnline();
        DatabaseReference ref = database.getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> thePosts = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Post tempPost = new Post();
                    User tempUser = new User();
                    tempUser.setId((String) child.child("author").child("id").getValue());
                    tempUser.setUsername((String) child.child("author").child("username").getValue());
                    tempUser.setEmail((String) child.child("author").child("email").getValue());
                    //tempUser.setEmail(child.child("author").child("posts").getValue());
                    tempPost.setId((String) child.child("id").getValue());
                    tempPost.setAuthor(tempUser);
                    tempPost.setBody((String) child.child("body").getValue());
                    tempPost.setTag((String) child.child("tag").getValue());
                    tempPost.setDuration((Date) child.child("duration").getValue());
                    tempPost.setCreatedAt((Date) child.child("createdAt").getValue());
                    tempPost.setSize((int) ((long) child.child("size").getValue()));
                    tempPost.setLogoName("minecraft_logo.png");
                    //tempPost.setReplies(child.child("replies").getValue());
                    //thePosts.add(tempPost);
                    posts.add(tempPost);
                }
                postsAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }

}