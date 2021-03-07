package com.example.lfg.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfg.LoginActivity;
import com.example.lfg.R;
import com.example.lfg.adapters.PostsAdapter;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private Button btnLogout;
    private TextView tvUsername;
    private TextView tvUserDetails;
    private RecyclerView rvUserPosts;
    private PostsAdapter postsAdapter;
    private FirebaseDatabase database;
    List<Post> posts;
    private FirebaseAuth mAuth;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvUserPosts = view.findViewById(R.id.rvUserPosts);
        posts = new ArrayList<>();
        btnLogout = view.findViewById(R.id.btnLogout);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserDetails = view.findViewById(R.id.tvUserDetails);
        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();
        String username = prefs.getString("username", null);
        if(username == null)
            Toast.makeText(getContext(), "error loading username", Toast.LENGTH_SHORT).show();
        tvUsername.setText(username);

        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Post post = posts.get(position);
                Fragment fragment = new PostFragment(post);
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("profile").commit();
                //fragmentManager.beginTransaction().replace(R.id.,fragment).addToBackStack("profile").commit();

                //TODO: show post details
            }
        };

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        postsAdapter = new PostsAdapter(getContext(), posts, itemClickListener);
        rvUserPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserPosts.setAdapter(postsAdapter);
        database = FirebaseDatabase.getInstance();
        loadData();
    }
    private void loadData(){
        database.goOnline();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        Log.i(TAG, mUser.getUid());

        DatabaseReference ref = database.getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(!child.child("user").getValue().toString().equals(mUser.getUid())){
                        continue;
                    }
                    //User tempUser = new User();
                    Log.i("get", child.getKey());
               /*     tempUser.setId((String) child.child("author").child("id").getValue());
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
                    //thePosts.add(tempPost);*/
                    String game = (String) child.child("game").getValue();
                    int size = (int) ((long) child.child("size").getValue());
                    long time = (long) child.child("timestamp").getValue();
                    long timer = (long) child.child("timer").getValue();
                    String logo = (String) child.child("logoName").getValue();
                    String userId = (String) child.child("user").getValue();
                    Post currPost = new Post(game, size, logo, time+timer);
                    currPost.setUser(userId);
                    String body = (String) child.child("body").getValue();
                    currPost.setBody(body);
                    Date postTimestamp = new Date(time+timer);
                    Log.i("TIME", String.valueOf(time));
                    long currentTimestamp = System.currentTimeMillis();
                    Date currentTime = new Date(currentTimestamp);
                    if(postTimestamp.before(currentTime)){
                        String postId = child.getKey();
                        ref.child(postId).removeValue();
                        database.getReference("users").child(userId).child("posts").child(postId).removeValue();
                        Log.i("expired", "timestamp: " + postTimestamp.toString());
                        Log.i("expired", "current time: " + currentTime.toString());
                        //posts.add(currPost);
                    }
                    else{
                        Log.i("active", game + " timestamp: " + postTimestamp.toString());
                        Log.i("active", game + " current time: " + currentTime.toString());
                        posts.add(currPost);
                    }
                    //currPost.setLogoName("fortnite_logo.png");

                }

                Collections.sort(posts, new ProfileFragment.Sortbytime());
                postsAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }

    class Sortbytime implements Comparator<Post> {

        @Override
        public int compare(Post a, Post b) {
            return (int) (a.getTimeEnd() - b.getTimeEnd());
        }
    }
}