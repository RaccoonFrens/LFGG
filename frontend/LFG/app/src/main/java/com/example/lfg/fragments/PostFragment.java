package com.example.lfg.fragments;

import android.content.SharedPreferences;
import android.graphics.Movie;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.adapters.CommentsAdapter;
import com.example.lfg.adapters.PostsAdapter;
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
    private ImageView ivEdit;
    private ImageView ivBack;
    private EditText etComment;
    private Button btnSubmit;
    String userid;
    String username;


    private RecyclerView rvComments;
    private CommentsAdapter commentsAdapter;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    List<Comment> comments;

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
        ivEdit = view.findViewById(R.id.ivEdit);
        ivBack = view.findViewById(R.id.ivBack);
        etComment  = view.findViewById(R.id.etComment);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        rvComments = view.findViewById(R.id.rvComments);
        comments  = new ArrayList<>();
        MainActivity m = (MainActivity) getActivity();
        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();


        ItemLongClickListener itemLongClickListener = new ItemLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {

            }
        };

        commentsAdapter = new CommentsAdapter(getContext(), comments, itemLongClickListener);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(commentsAdapter);
        database = FirebaseDatabase.getInstance();
        database.goOnline();

        userid = post.getUser();
        Log.i("userID", userid);
        //fragmentManager = getActivity().getSupportFragmentManager();
       /* database.getReference("users").child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String username = (String) task.getResult().child("username").getValue();
                    tvUsername.setText(username);
                }
            }
        });*/
        username = prefs.getString("username", null);
        tvUsername.setText(username);




        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userid)){
            Log.i("PostFrag", userid);
            ivEdit.setVisibility(View.GONE);
        }

        tvBody.setText(post.getBody());
        getComments();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = etComment.getText().toString();
                if(response.length() < 1){
                    return;
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference commentRef = database.getReference("comments");
                DatabaseReference newCommentRef = commentRef.push();
                String commentId = newCommentRef.getKey();

                Comment comment = new Comment(commentId, userid, username, response);
                newCommentRef.setValue(comment);
                post.addComment(commentId);

                comments.add(comment);
                commentsAdapter.notifyDataSetChanged();

                String postId = post.getId();
                DatabaseReference currPostRef = database.getReference("posts").child(postId + "/comments");
                DatabaseReference newCommentPostRef = currPostRef.child(commentId);
                newCommentPostRef.setValue(comment);
               // DatabaseReference currPostRef = database.getReference("posts").child()
               /* FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference postsRef = database.getReference("posts");
                DatabaseReference newPostRef = postsRef.push();
                Map<String, String> timestamp = ServerValue.TIMESTAMP;
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Post post = new Post(gameTag, Integer.parseInt(vacancy), timestamp, user, partyTimer);
                post.setBody(postDetail);
                newPostRef.setValue(post);

                //update '/users' database
                String postId = newPostRef.getKey();
                DatabaseReference currUserRef = database.getReference("users").child(user + "/posts");
                DatabaseReference newUserPostRef = currUserRef.child(postId);
                newUserPostRef.setValue(post);*/

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


}