package com.example.lfg.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.adapters.CommentsAdapter;
import com.example.lfg.adapters.PostsAdapter;
import com.example.lfg.adapters.UserAdapter;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.interfaces.ItemLongClickListener;
import com.example.lfg.models.Comment;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


import okhttp3.Headers;

import static android.content.Context.MODE_PRIVATE;

import okhttp3.Headers;
public class PostFragment extends Fragment {
    Post post;
    User user;
    private TextView tvBody;
    private TextView tvUsername;
    private TextView tvTime;
    private ImageView ivEdit;
    private ImageView ivBack;
    private ImageView ivGameLogo;
    private EditText etComment;
    private Button btnJoinParty;
    private TextView tvMatch;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    final String DATABASEURL = "gs://lfgg-78154.appspot.com";
    public final String match_URL_base = "https://na1.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/";
    String RIOT_API_KEY = "RGAPI-b409a18d-667d-4a23-ae58-74249c736b06"; //expires after 24 hours [3/21 12:13 am]
    String matchTime;
    String leagueName;
    String leagueId;
    String userid;
    String username;
    String currUsername;


    private RecyclerView rvComments;
    private CommentsAdapter commentsAdapter;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    private RecyclerView rvParty;
    private UserAdapter userAdapter;

    List<Comment> comments;
    List<User> users;

    FirebaseDatabase database;


    public PostFragment() {
        // Required empty public constructor
    }

    public PostFragment(Post post){
        this.post = post;
    }

    public PostFragment(Post post, User user) {
        this.post = post;
        this.user = user;
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
        tvUsername = view.findViewById(R.id.tvUsername);
        tvTime = view.findViewById(R.id.tvTime);
        tvMatch = view.findViewById(R.id.league_status); // league match
        ivEdit = view.findViewById(R.id.ivEdit);
        ivBack = view.findViewById(R.id.ivBack);
        etComment  = view.findViewById(R.id.etComment);
        btnJoinParty = view.findViewById(R.id.btnJoinParty);
        ivGameLogo = view.findViewById(R.id.logo);
        getActivity().findViewById(R.id.floatingActionButton).setVisibility(View.INVISIBLE); //HIDE FAB

        rvComments = view.findViewById(R.id.rvComments);
        rvParty = view.findViewById(R.id.rvParty);
        comments  = new ArrayList<>();
        users = new ArrayList<>();
        MainActivity m = (MainActivity) getActivity();
        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();
        currUsername = prefs.getString("username", null);

        userid = post.getUser();
        getUsername();

        ivGameLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User mUser = new User();
                mUser.setId(userid);
                mUser.setUsername(username);
                Fragment fragment = new ProfileFragment(mUser);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("user").commit();
            }
        });

        ItemLongClickListener itemLongClickListener = new ItemLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                Comment comment = comments.get(position);
                if(comment.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    comments.remove(position);
                    commentsAdapter.notifyItemRemoved(position);
                    Log.i("LongClick", comment.getId());
                    String commentId = comment.getId();
                    Log.i("update", comments.toString());
                    database.getReference("posts").child(post.getId()).child("comments").child(commentId).removeValue();
                    database.getReference("comments").child(commentId).removeValue();
                }
                Log.i("LongClick", FirebaseAuth.getInstance().getCurrentUser().getUid());

            }
        };

        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Comment comment = comments.get(position);
                User mUser = new User();
                mUser.setId(comment.getUserId());
                mUser.setUsername(comment.getUsername());
                Fragment fragment = new ProfileFragment(mUser);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("user").commit();
            }
        };

        ItemClickListener partyItemClickListener = new ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                User mUser = users.get(position);
                Fragment fragment = new ProfileFragment(mUser);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("user").commit();
            }
        };


        ItemLongClickListener partyLongClickListener = new ItemLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                User user = users.get(position);
                if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    || FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.getUser())){
                    users.remove(position);
                    userAdapter.notifyItemRemoved(position);
                    database.getReference("posts").child(post.getId()).child("party").child(user.getId()).removeValue();
                }
            }
        };

        commentsAdapter = new CommentsAdapter(getContext(), comments, itemLongClickListener, itemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        rvComments.setLayoutManager(layoutManager);
        rvComments.setAdapter(commentsAdapter);

        userAdapter = new UserAdapter(getContext(), users, partyLongClickListener, partyItemClickListener);
        rvParty.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvParty.setAdapter(userAdapter);

        database = FirebaseDatabase.getInstance();
        database.goOnline();

        StorageReference storageRef = storage.getReferenceFromUrl(DATABASEURL).child(post.getLogoName());

        try {
            final File localFile = File.createTempFile("images", "png");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ivGameLogo.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}

        Log.i("userID", userid);
        long time = post.getTimeEnd()-System.currentTimeMillis();
        int minutes = (int) (time/60000);
        String timeMessage = "Party open for " + minutes/60 + " hours and " + minutes%60 + " minutes";
        if(minutes < 60){
            timeMessage = "Party open for " + minutes%60 + " minutes";
        }
        if(minutes == 0)
            timeMessage = "Party open for less than a minute";
        tvTime.setText(timeMessage);

        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userid)){
            Log.i("PostFrag", userid);
            ivEdit.setVisibility(View.GONE);
        }

        if(post.getGame().equals("League of Legends")){
            getMatch();
            tvMatch.setVisibility(View.VISIBLE);
        }else{
            tvMatch.setVisibility(View.INVISIBLE);
        }
        tvBody.setText(post.getBody());
        getComments();

        etComment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction() == keyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    String response = etComment.getText().toString();
                    if(response.length() < 1){
                        return false;
                    }
                    DatabaseReference commentRef = database.getReference("comments");
                    DatabaseReference newCommentRef = commentRef.push();
                    String commentId = newCommentRef.getKey();
                    Comment comment = new Comment(commentId, FirebaseAuth.getInstance().getCurrentUser().getUid(), currUsername, response);
                    newCommentRef.setValue(comment);
                    post.addComment(commentId);
                    post.addReply(comment);


                    String postId = post.getId();
                    DatabaseReference currPostRef = database.getReference("posts").child(postId + "/comments");
                    DatabaseReference newCommentPostRef = currPostRef.child(commentId);
                    newCommentPostRef.setValue(comment);
                    etComment.setText("");
                    return true;
                }
                return false;
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new EditFragment(post);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("post").commit();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m.fragmentManager.popBackStackImmediate();
            }
        });

        btnJoinParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(currUserId.equals(post.getUser())){
                    Toast.makeText(getContext(), "You are the party leader.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(users.size() > post.getSize()){
                    Toast.makeText(getContext(), "Party is full", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(User user: users){
                    if(user.getId().equals(currUserId)){
                        Toast.makeText(getContext(), "You are already in the party.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                getUsername(currUserId);
            }
        });

    }

    private void getComments() {
        database.goOnline();
        String postId = post.getId();
        DatabaseReference ref = database.getReference("posts").child(postId).child("comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.i("get", child.getKey());
                    String body = (String) child.child("body").getValue();
                    String id = (String) child.child("id").getValue();
                    String userId = (String) child.child("userId").getValue();
                    String username = (String) child.child("username").getValue();
                    Comment comment = new Comment(id, userId, username, body);
                    Log.i("update", comment.getId());
                    comments.add(comment);
                   /* storageRef.child("images/"+userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            comment.setPhotoUrl(uri.toString());
                            Log.i("update", "downloaded");
                            comments.add(comment);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Log.i("update", "unable to download url");
                            comments.add(comment);
                        }
                    });*/

                }

               /* for(int i = 0; i < comments.size(); i++){
                    Comment comment = comments.get(i);

                }*/
                commentsAdapter.notifyDataSetChanged();


                // Log.i("update", String.valueOf(comments.size()));
               // commentsAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                Log.e("getComments", "The read failed: " + databaseError.getCode());
            }
        });
        commentsAdapter.notifyDataSetChanged();

    }

    private void getParty(){
        database.goOnline();
        String postId = post.getId();
        DatabaseReference ref = database.getReference("posts").child(postId).child("party");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                User currUser = new User(userid, username, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                users.add(currUser);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.i("get", child.getKey());
                    String mUserId = (String) child.child("id").getValue();
                    String mEmail = (String) child.child("email").getValue();
                    String mUsername = (String) child.child("username").getValue();
                    User user = new User(mUserId, mUsername, mEmail);
                    users.add(user);

                }
                userAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                Log.e("getUser", "The read failed: " + databaseError.getCode());
            }
        });

    }

    public void getUsername(){
        FirebaseDatabase.getInstance().getReference().child("users").child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    username = (String) task.getResult().child("username").getValue();
                    tvUsername.setText(username);
                    getParty();
                }
            }
        });
    }

    public void getUsername(String userId){
        FirebaseDatabase.getInstance().getReference().child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    currUsername = (String) task.getResult().child("username").getValue();
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    User user = new User(userId, currUsername, email);
                    users.add(user);
                    Log.i("btnParty", user.toString());
                    userAdapter.notifyDataSetChanged();

                    DatabaseReference currPostRef = database.getReference("posts").child(post.getId()).child("party");
                    DatabaseReference newUserRef = currPostRef.child(userId);
                    newUserRef.setValue(user);
                }
            }
        });
    }

    public void getMatch(){
        //uses the userid from post.getUserId()
        FirebaseDatabase.getInstance().getReference().child("users").child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    //fetch summonerID of post creator
                    leagueId = (String) task.getResult().child("LeagueId").getValue();
                    Log.d("PostFragment", "summoner ID is: " + leagueId);
                    //open http client to make API request
                    String match_URL = match_URL_base+leagueId+"?api_key="+RIOT_API_KEY;
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(match_URL, new TextHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Headers headers, String response) {
                                    // called when response HTTP status is "200 OK"
                                    Log.d("PostFragment", "match onSuccess" + response);
                                    //put response into an json object
                                    JSONObject match = new JSONObject();
                                    try {
                                        match = new JSONObject(response);
                                    }catch (JSONException err){
                                        Log.d("Error", err.toString());
                                    }
                                    //parse json object into matchTime
                                    //can also be used to store other things
                                    try {
                                        matchTime = match.getString("gameStartTime");
                                        tvMatch.setText("Match time:" + (System.currentTimeMillis() - Integer.parseInt(matchTime) / 60000));
                                    } catch (JSONException e) {
                                        matchTime = "0";
                                        tvMatch.setText("Match time:" + (System.currentTimeMillis() - Integer.parseInt(matchTime) / 60000));
                                        e.printStackTrace();
                                    }
                                    Log.d("PostFragment", "Match in progress since " + matchTime);
                                    tvMatch.setBackgroundColor(Color.parseColor("#01873D"));
                                }
                                @Override
                                public void onFailure(int statusCode, Headers headers, String errorResponse, Throwable t) {
                                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                    Log.d("PostFragment", "match onFailure" + errorResponse + match_URL);
                                    tvMatch.setText("Not currently in game");
                                    tvMatch.setBackgroundColor(Color.parseColor("#D38075"));
                                }
                            }
                    );
                } //end else
            }//end on complete
        }); //end firebase get instance
    } //end getMatch

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