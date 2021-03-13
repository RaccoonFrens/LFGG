package com.example.lfg.fragments;

import android.content.SharedPreferences;
import android.graphics.Movie;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.adapters.CommentsAdapter;
import com.example.lfg.adapters.PostsAdapter;
import com.example.lfg.adapters.UserAdapter;
import com.example.lfg.interfaces.ItemLongClickListener;
import com.example.lfg.models.Comment;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class PostFragment extends Fragment {
    Post post;
    User user;
    private TextView tvBody;
    private TextView tvUsername;
    private TextView tvTime;
    private ImageView ivEdit;
    private ImageView ivBack;
    private EditText etComment;
    private Button btnJoinParty;
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
        ivEdit = view.findViewById(R.id.ivEdit);
        ivBack = view.findViewById(R.id.ivBack);
        etComment  = view.findViewById(R.id.etComment);
        btnJoinParty = view.findViewById(R.id.btnJoinParty);

        rvComments = view.findViewById(R.id.rvComments);
        rvParty = view.findViewById(R.id.rvParty);
        comments  = new ArrayList<>();
        users = new ArrayList<>();
        MainActivity m = (MainActivity) getActivity();
        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();
        currUsername = prefs.getString("username", null);

        ItemLongClickListener itemLongClickListener = new ItemLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                Comment comment = comments.get(position);
                if(comment.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    comments.remove(position);
                    commentsAdapter.notifyItemRemoved(position);
                    Log.i("LongClick", comment.getId());
                    String commentId = comment.getId();
                    database.getReference("posts").child(post.getId()).child("comments").child(commentId).removeValue();
                    database.getReference("comments").child(commentId).removeValue();

                }
                Log.i("LongClick", FirebaseAuth.getInstance().getCurrentUser().getUid());

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

        commentsAdapter = new CommentsAdapter(getContext(), comments, itemLongClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        rvComments.setLayoutManager(layoutManager);
        rvComments.setAdapter(commentsAdapter);

        userAdapter = new UserAdapter(getContext(), users, partyLongClickListener);
        rvParty.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvParty.setAdapter(userAdapter);

        database = FirebaseDatabase.getInstance();
        database.goOnline();

        userid = post.getUser();
        getUsername();
        getParty();
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

                    comments.add(0, comment);
                    commentsAdapter.notifyDataSetChanged();

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
                if(users.size() >= post.getSize()){
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
                    comments.add(comment);

                }
                commentsAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                Log.e("getComments", "The read failed: " + databaseError.getCode());
            }
        });
    }

    private void getParty(){
        database.goOnline();
        String postId = post.getId();
        DatabaseReference ref = database.getReference("posts").child(postId).child("party");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
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


}