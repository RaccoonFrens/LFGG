package com.example.lfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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
    private BottomNavigationView bottomNavigationView;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        fragmentManager.beginTransaction().hide(active).show(composeFragment).commit();
                        active = composeFragment;
                        break;
                    case R.id.home:
                        fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
                        active = homeFragment;
                        break;
                    case R.id.profile:
                    default:
                        fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
                        active = profileFragment;
                        break;
                }
                //fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.home);

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
                    edit.putString("username", username);
                    edit.apply();
                }
            }
        });
    }
}