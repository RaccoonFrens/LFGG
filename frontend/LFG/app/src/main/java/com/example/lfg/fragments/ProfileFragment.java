package com.example.lfg.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.lfg.LoginActivity;
import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.adapters.PostsAdapter;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.models.Comment;
import com.example.lfg.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private TextView tvUsername;
    private TextView tvUserDetails;
    private EditText etBio;
    private RecyclerView rvUserPosts;
    private PostsAdapter postsAdapter;
    private FirebaseDatabase database;
    private TextInputLayout etLayout;
    private ImageView ivSettings;
    private EditText etLeagueName;
    private Button btnAdd;

    private String RIOT_API_KEY = "RGAPI-84ed6122-226c-4ea9-a72d-9dfa2d17d8ab"; //expires after 24 hours [3/17 6:43 pm]
    private static String summoner_URL = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
    //https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/lolo1163?api_key=RGAPI-7dd4cdc6-34b8-4e35-9117-5c5548e0a13d
    private static String match_URL = "https://na1.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/";
    //https://na1.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/ZXjtyVKWGR6x3xVYEo9wDqxYBJhlm50WNGYDBsBq4yWE-1o
    public static String summonerId;

    List<Post> posts;
    private FirebaseAuth mAuth;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    String userId;

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
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserDetails = view.findViewById(R.id.tvUserDetails);
        etBio = view.findViewById(R.id.etBio);
        etLayout = view.findViewById(R.id.etLayout);
        ivSettings = view.findViewById(R.id.ivSettings);
        btnAdd = view.findViewById(R.id.btn_LName);
        etLeagueName = view.findViewById(R.id.etLeagueName);


        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(username.isEmpty())
            username = prefs.getString("username", "username");
        tvUsername.setText(username);
        Date createdAt = new Date(FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp());
        tvUserDetails.setText("Member since: " + createdAt.toLocaleString());
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

        etBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etBio.setFocusableInTouchMode(true);
                if(etBio.getText().toString().equals("Tap to set bio"))
                    etBio.setText("");
            }
        });
        
        etBio.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction() == keyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String bio = etBio.getText().toString();
                    if(bio.length() < 1){
                        return false;
                    }
                    DatabaseReference userRef = database.getReference("users").child(userId).child("bio");
                    userRef.setValue(bio);
                    View v = getActivity().getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    etBio.setFocusable(false);
                    return true;
                }
                return false;
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sumName = etLeagueName.getText().toString();
                summoner_URL = summoner_URL+sumName+"?api_key="+RIOT_API_KEY;

                AsyncHttpClient client = new AsyncHttpClient();
                client.get(summoner_URL, new TextHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, String response) {
                                // called when response HTTP status is "200 OK"
                                Log.d("ProfileFragment", "onSuccess" + response);
                                JSONObject summoner = new JSONObject();
                                try {
                                    summoner = new JSONObject(response);
                                }catch (JSONException err){
                                    Log.d("Error", err.toString());
                                }

                                try {
                                    summonerId = summoner.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("ProfileFragment", "unique summoner id = " + summonerId);
                                DatabaseReference userRef = database.getReference("users").child(userId).child("LeagueId");
                                userRef.setValue(summonerId);
                                Toast.makeText(getContext(), "Sum Id is: " + summonerId, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String errorResponse, Throwable t) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                Log.d("ProfileFragment", "onFailure" + errorResponse);
                            }
                        }
                );
            }
        });


        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity m = (MainActivity) getActivity();
                Fragment fragment = new SettingsFragment();
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("profile").commit();
            }
        });

        postsAdapter = new PostsAdapter(getContext(), posts, itemClickListener);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvUserPosts.setLayoutManager(gridLayoutManager);
        rvUserPosts.setAdapter(postsAdapter);
        database = FirebaseDatabase.getInstance();
        loadData();
        setBio();

    }

    private void setBio() {
        database.goOnline();
        DatabaseReference userRef = database.getReference("users").child(userId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String bio = (String) task.getResult().child("bio").getValue();
                    if(bio != null) {
                        Log.i("Bio", bio);
                        etBio.setText(bio);
                    }
                    etBio.setFocusable(false);
                }
            }
        });
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
                    Log.i("get", child.getKey());
                    String game = (String) child.child("game").getValue();
                    int size = (int) ((long) child.child("size").getValue());
                    long time = (long) child.child("timestamp").getValue();
                    long timer = (long) child.child("timer").getValue();
                    String logo = (String) child.child("logoName").getValue();
                    String userId = (String) child.child("user").getValue();
                    String id = (String) child.child("id").getValue();
                    Post currPost = new Post(game, size, logo, time + timer);
                    String body = (String) child.child("body").getValue();
                    String tag = (String) child.child("tag").getValue();
                    currPost.setBody(body);
                    currPost.setTag(tag);
                    currPost.setId(id);
                    currPost.setTime(time);
                    currPost.setUser(userId);
                    currPost.setTimer(timer);
                    DataSnapshot commentChild = child.child("comments");
                    if(commentChild != null) {
                        Log.i("comments", "success");
                        List<String> commentIds = new ArrayList<>();
                        for (DataSnapshot currComment : commentChild.getChildren()) {
                            String commentId = (String) currComment.child("id").getValue();
                            String commentUserId = (String) currComment.child("userId").getValue();
                            String commentUsername = (String) currComment.child("username").getValue() ;
                            String commentBody = (String) currComment.child("body").getValue();
                            Comment c = new Comment(commentId, commentUserId, commentUsername, commentBody);
                            Log.i("comments", commentId);
                            commentIds.add(commentId);
                            currPost.addReply(c);
                        }
                        currPost.setComments(commentIds);
                    }
                    Date postTimestamp = new Date(time + timer);
                    Log.i("TIME", String.valueOf(time));
                    long currentTimestamp = System.currentTimeMillis();
                    Date currentTime = new Date(currentTimestamp);
                    if (postTimestamp.before(currentTime)) {
                        String postId = child.getKey();
                        ref.child(postId).removeValue();
                        database.getReference("users").child(userId).child("posts").child(postId).removeValue();
                        if(currPost.getComments() != null) {
                            for (String commentId : currPost.getComments()) {
                                database.getReference("comments").child(commentId).removeValue();
                            }
                        }
                        Log.i("expired", "timestamp: " + postTimestamp.toString());
                        Log.i("expired", "current time: " + currentTime.toString());
                    } else {
                        Log.i("active", game + " timestamp: " + postTimestamp.toString());
                        Log.i("active", game + " current time: " + currentTime.toString());
                        posts.add(currPost);

                    }

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