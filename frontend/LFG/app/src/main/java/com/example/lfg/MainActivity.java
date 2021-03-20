package com.example.lfg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lfg.fragments.ComposeFragment;
import com.example.lfg.fragments.FriendRequestFragment;
import com.example.lfg.fragments.HomeFragment;
import com.example.lfg.fragments.ProfileFragment;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    public final FragmentManager fragmentManager = getSupportFragmentManager();
    public final Fragment homeFragment = new HomeFragment();
    public final Fragment composeFragment = new ComposeFragment();
    public final Fragment profileFragment  = new ProfileFragment();
    public Fragment active = homeFragment;
    public BottomNavigationView bottomNavigationView;
    public FloatingActionButton FAB;
    private int friendReqNum = 0;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FAB = findViewById(R.id.floatingActionButton);
        fragmentManager.beginTransaction().add(R.id.flContainer, profileFragment, "profileFragment").hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flContainer, composeFragment, "composeFragment").hide(composeFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flContainer,homeFragment, "homeFragment").commit();
        prefs = getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();
        getUser();

        FAB.setOnClickListener(new View.OnClickListener(){
            final FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment composeFragment  = new ComposeFragment();
            public void onClick(View v){
                //open post fragment
                //setActive(composeFragment);
                fragmentManager.beginTransaction().replace(R.id.flContainer, composeFragment).addToBackStack("home").commit();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch(item.getItemId()){
                    case R.id.post:
                        setActive(composeFragment);
                        break;
                    case R.id.home:
                        setActive(homeFragment);
                       // HomeFragment t = (HomeFragment) active;
                       // t.loadData();
                        break;
                    case R.id.profile:
                    default:
                        setActive(profileFragment);
                        break;
                }
                //fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String theFrag = intent.getStringExtra("active");
        if(theFrag != null) {
            if (theFrag.equals("home") && active != homeFragment) {
                Log.i("MainActivity", "Fragment switched to home");
                setActive(homeFragment);
            }
            else if(theFrag.equals("friends")){
                Log.i("MainActivity", "Fragment switched to friend requests");
                Fragment fragment = new FriendRequestFragment();
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("profile").commit();
            }
            else{
                Log.i("MainActivity", "Fragment already home");
            }
        }
    }

    private void setActive(Fragment theActive){
        fragmentManager.beginTransaction().hide(active).show(theActive).commit();
        fragmentManager.popBackStack();
        fragmentManager.popBackStack();
        active = theActive;
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void getUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.goOnline();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = database.getReference("users").child(userId);
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    DataSnapshot data = task.getResult();
                    String username = (String) data.child("username").getValue();
                    Log.i("Username", data.toString());
                    Log.i("Username", username);
                    //Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();
                    edit.putString("username", username);
                    edit.commit();
                }
            }
        });
        ref.child("requests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int theNewNum = (int) snapshot.getChildrenCount();
                if(theNewNum != 0){
                    showNotification(0);
                }
                //if(theNewNum > friendReqNum){
                //    showNotification(theNewNum - friendReqNum);
                //    friendReqNum+=theNewNum;
                //}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //friendReqNum--;
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "User err: " + error.toString());
            }
        });
    }

    private void showNotification(int friendReqs){
        String NEW_FRIEND_CHANNEL_ID = "new_friend_request_channel";

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("active", "friends");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NEW_FRIEND_CHANNEL_ID)
                .setSmallIcon(R.drawable.other)
                .setContentTitle("New Friend Request")
                .setContentText("You have friend request(s)!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel new_friend_req = new NotificationChannel(NEW_FRIEND_CHANNEL_ID, "New Friend Request", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(new_friend_req);
        }

        notificationManager.notify(1, builder.build());
    }

}