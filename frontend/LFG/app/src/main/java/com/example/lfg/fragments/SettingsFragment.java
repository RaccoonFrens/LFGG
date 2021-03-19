package com.example.lfg.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.lfg.LoginActivity;
import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Headers;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    private TextView tvUsernameArrow;
    private TextView tvEmailArrow;
    private TextView tvLeagueUserArrow;

    private LinearLayout layoutUsername;
    private LinearLayout layoutEmail;
    private LinearLayout layoutPassword;
    private LinearLayout layoutLeagueUser;

    private Button btnLogout;

    private ImageView ivProfile;

    private String RIOT_API_KEY = "RGAPI-20190ff7-230a-4ddd-afdc-fed4bfba5d26"; //expires after 24 hours [3/18 4:53 pm]
    private static String summoner_URL = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
    //https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/lolo1163?api_key=RGAPI-7dd4cdc6-34b8-4e35-9117-5c5548e0a13d
    private static String match_URL = "https://na1.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/";
    //https://na1.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/ZXjtyVKWGR6x3xVYEo9wDqxYBJhlm50WNGYDBsBq4yWE-1o
    public static String summonerId;

    String userId;
    String username;
    String email;
    String leagueId;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    public SettingsFragment(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsernameArrow = view.findViewById(R.id.tvUsernameArrow);
        tvEmailArrow = view.findViewById(R.id.tvEmailArrow);
        tvLeagueUserArrow = view.findViewById(R.id.tvLeagueUserArrow);
        layoutEmail = view.findViewById(R.id.layoutEmail);
        layoutUsername = view.findViewById(R.id.layoutUsername);
        layoutPassword = view.findViewById(R.id.layoutPassword);
        layoutLeagueUser = view.findViewById(R.id.layoutLeagueUser);
        ivProfile = view.findViewById(R.id.ivProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        username = firebaseUser.getDisplayName();
        Log.i("SettingsFragment", "Username: " + username);
        if(username == null)
            username = prefs.getString("username", "username");
        email = firebaseUser.getEmail();
        tvUsernameArrow.setText(username);
        tvEmailArrow.setText(email);
        MainActivity m = (MainActivity) getActivity();
        User user = new User(userId, username, email);

        database = FirebaseDatabase.getInstance();

        database.getReference("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("SettingsFragment", "Error getting data", task.getException());
                }
                else {
                    if(task != null){
                        leagueId = String.valueOf(task.getResult().child("LeagueId").getValue());
                        if(leagueId != null) {
                            tvLeagueUserArrow.setText(String.valueOf(task.getResult().child("LeagueName").getValue()));
                        }
                        else{
                            tvLeagueUserArrow.setText("no LOL username");
                        }
                    }
                }
            }
        });

        Uri profileUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        if(profileUri!= null){
            Log.i("Uri", profileUri.toString());
            Glide.with(getContext())
                    .load(profileUri.toString())
                    .circleCrop()
                    .into(ivProfile);
        }

        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new EmailFragment(user);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("settings").commit();
            }
        });

        layoutUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UsernameFragment(user);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("settings").commit();
            }
        });

        layoutPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PasswordFragment(user);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("settings").commit();
            }
        });

        layoutLeagueUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LeagueUserFragment(user, leagueId);
                m.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("settings").commit();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                ivProfile.setImageBitmap(bitmap);
                uploadImage(filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri filePath) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            StorageReference ref = storageReference.child("images/"+ userId);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            setProfile(filePath);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void setProfile(Uri filePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child("images");
        UploadTask uploadTask = ref.putFile(filePath);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri).build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Profile", "username set");
                            }
                        }
                    });
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

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