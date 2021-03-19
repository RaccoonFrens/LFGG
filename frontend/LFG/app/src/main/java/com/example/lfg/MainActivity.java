package com.example.lfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lfg.fragments.ComposeFragment;
import com.example.lfg.fragments.HomeFragment;
import com.example.lfg.fragments.ProfileFragment;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public final FragmentManager fragmentManager = getSupportFragmentManager();
    public final Fragment homeFragment = new HomeFragment();
    public final Fragment composeFragment = new ComposeFragment();
    public final Fragment profileFragment  = new ProfileFragment();
    public Fragment active = homeFragment;
    public BottomNavigationView bottomNavigationView;
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

        fragmentManager.beginTransaction().add(R.id.flContainer, profileFragment, "profileFragment").hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flContainer, composeFragment, "composeFragment").hide(composeFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flContainer,homeFragment, "homeFragment").commit();
        prefs = getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();
        getUser();

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
                Log.i("MainActivity", "Activity switched to home");
                setActive(homeFragment);
            }
            else{
                Log.i("MainActivity", "Activity already home");
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

        database.getReference("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
    }

}