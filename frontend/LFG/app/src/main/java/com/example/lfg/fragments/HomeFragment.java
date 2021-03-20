package com.example.lfg.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.adapters.PostsAdapter;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.models.Comment;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";
    private RecyclerView rvPosts;
    private ProgressBar progressBar;
    private PostsAdapter postsAdapter;
    private FirebaseDatabase database;
    private SwipeRefreshLayout swipeContainer;
    ArrayAdapter<CharSequence> filterAdapter;
    private Button btnFilter;
    private Spinner spinnerFilter;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    String scrollID;
    int index;
    static int numPosts = 0;
    public List<Post> posts;

    public HomeFragment() {
        // Required empty public constructor
        scrollID = null;
        index = 0;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        posts.clear();
        postsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        if(hidden){
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }else{
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        }
        loadData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Show the FAB
        getActivity().findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE); //SHOW FAB

        rvPosts = view.findViewById(R.id.rvPosts);
        progressBar = view.findViewById(R.id.progressBar);
        posts = new ArrayList<>();
        swipeContainer = view.findViewById(R.id.swipeContainer);
        filterAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.game_array, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();

        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Post post = posts.get(position);
                User postUser = post.getAuthor();
                //Fragment currFrag = fragmentManager.findFragmentByTag("homeFragment");
                Fragment fragment = new PostFragment(post, postUser);
                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    // Inflate transitions to apply
                    Transition changeTransform = TransitionInflater.from(getContext()).
                            inflateTransition(R.transition.change_image_transition);
                    Transition explodeTransform = TransitionInflater.from(getContext()).
                            inflateTransition(android.R.transition.explode);
                    Transition bottomTransform = TransitionInflater.from(getContext()).
                            inflateTransition((android.R.transition.slide_bottom));
                    Transition topTransformation = TransitionInflater.from(getContext()).
                            inflateTransition((android.R.transition.slide_bottom));
                    Transition fadeTransform = TransitionInflater.from(getContext()).
                            inflateTransition((android.R.transition.fade));
                    //currFrag.setReenterTransition(bottomTransform);
                    //TransitionSet transitionSet = new TransitionSet();
                    //transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
                    //transitionSet.addTransition(new ChangeBounds()).addTransition(new ChangeTransform()).addTransition(new ChangeImageTransform());
                    fragment.setEnterTransition(bottomTransform);
                    fragment.postponeEnterTransition(100, TimeUnit.MILLISECONDS);
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("home").commit();
                    //fragmentManager.beginTransaction().addSharedElement(view.findViewById(R.id.logo), "logo").replace(R.id.flContainer, fragment).addToBackStack("home").commit();

                }*/
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("home").commit();
            }
        };

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                swipeContainer.setRefreshing(false);
            }
        });

        postsAdapter = new PostsAdapter(getContext(), posts, itemClickListener);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvPosts.setLayoutManager(gridLayoutManager);
        rvPosts.setAdapter(postsAdapter);
        database = FirebaseDatabase.getInstance();
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment profileFragment  = new ProfileFragment();

        if(item.getItemId() == R.id.filter){
            displayPopupWindow();
            return true;
        }
        else if(item.getItemId() == R.id.profile){
            //open profile fragment
            fragmentManager.beginTransaction().replace(R.id.flContainer, profileFragment).addToBackStack("home").commit();
            return true;
        }else{
            return false;
        }
    }

    private void displayPopupWindow() {
        PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.pop_up_filter, null);
        popup.setContentView(layout);
        popup.setOutsideTouchable(true);
        popup.setTouchable(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.BLACK);
        colorDrawable.setAlpha(210);
        popup.setBackgroundDrawable(colorDrawable);
        popup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popup.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        btnFilter = layout.findViewById(R.id.btnFilter);
        spinnerFilter = layout.findViewById(R.id.spinnerFilter);
        spinnerFilter.setAdapter(filterAdapter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = spinnerFilter.getSelectedItemPosition();
                if(i == 0){
                    postsAdapter.filterPosts("all");
                }
                else{
                    String game = spinnerFilter.getItemAtPosition(i).toString();
                    postsAdapter.filterPosts(game);
                }
                popup.dismiss();
            }
        });
        popup.update(0, 300, LinearLayout.LayoutParams.MATCH_PARENT, -1);
    }

    public void loadData(){
        database.goOnline();

        DatabaseReference ref = database.getReference("posts");
        posts.clear();
        postsAdapter.notifyDataSetChanged();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(numPosts < dataSnapshot.getChildrenCount()){
                    showNotification((int) dataSnapshot.getChildrenCount() - numPosts);
                }
                numPosts = (int) dataSnapshot.getChildrenCount();
                posts.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User tempUser = new User();
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
                    int players = 0;
                    if(child.hasChild("party")) {
                        players = (int) child.child("party").getChildrenCount();
                        currPost.setPlayers(players);
                    }
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

                Collections.sort(posts, new Sortbytime());
                postsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }

    public void showNotification(int numNewPosts){
        String NEW_POST_CHANNEL_ID = "new_post_channel";
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("active", "home");
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Objects.requireNonNull(getContext()), NEW_POST_CHANNEL_ID)
                .setSmallIcon(R.drawable.other)
                .setContentTitle("New Post")
                .setContentText("There are " + numNewPosts + " new groups waiting for you to join!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true);
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel new_post = new NotificationChannel(NEW_POST_CHANNEL_ID, "new post", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(new_post);
        }

        notificationManager.notify(0, builder.build());
    }

    public static void addPostCount(){
        numPosts++;
    }

    public static void decreasePostCount(){
        numPosts--;
    }

    class Sortbytime implements Comparator<Post>{

        @Override
        public int compare(Post a, Post b) {
            int aFill = a.getSize() - a.getPlayers();
            int bFill = b.getSize() - b.getPlayers();
            if(aFill == 0 && bFill == 0)
                return (int) (a.getTimeEnd() - b.getTimeEnd());
            if(aFill == 0)
                return 1;
            if(bFill == 0)
                return -1;
            return (int) (a.getTimeEnd() - b.getTimeEnd());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();


    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
}