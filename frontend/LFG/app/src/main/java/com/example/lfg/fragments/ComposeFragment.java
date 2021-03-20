package com.example.lfg.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Map;


public class ComposeFragment extends Fragment {
    public static final String BLANK_FIELD = "Field cannot be left blank";
    public static final String TAG = "ComposeFragment";
    private Spinner spinnerGame;
    private Spinner     sPartyAmount;
    private Spinner spinnerTag;
    private EditText    etPostDetails;
    private NumberPicker npHour;
    private NumberPicker npMinute;
    private Button      btnPost;
    private FloatingActionButton FAB;
    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.floatingActionButton).setVisibility(View.INVISIBLE); //HIDE FAB

        spinnerGame = view.findViewById(R.id.spinnerGame);
        spinnerTag = view.findViewById(R.id.spinnerTag);
        sPartyAmount = view.findViewById(R.id.sPartyAmount);
        etPostDetails = view.findViewById(R.id.etPostDetails);
        npHour = view.findViewById(R.id.npHour);
        npMinute = view.findViewById(R.id.npMinute);
        btnPost = view.findViewById(R.id.btnPost);

        npHour.setMinValue(0);
        npHour.setMaxValue(23);
        npMinute.setMinValue(0);
        npMinute.setMaxValue(59);

        ArrayAdapter<CharSequence> gameAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.game_array, android.R.layout.simple_spinner_item);
        gameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGame.setAdapter(gameAdapter);

        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.size_array, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPartyAmount.setAdapter(sizeAdapter);

        ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tag_array, android.R.layout.simple_spinner_item);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTag.setAdapter(tagAdapter);

        setSpinnerListeners();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), spinnerGame.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                createPost();
            }
        });
    }

    private void createPost() {
        if(!validInput())
            return;
        final String postDetail = etPostDetails.getText().toString();
        final String gameName   = spinnerGame.getSelectedItem().toString();
        final String gameTag    = spinnerTag.getSelectedItem().toString();
        final int hour          = npHour.getValue();
        final int minute        = npMinute.getValue();
        final String vacancy    = sPartyAmount.getSelectedItem().toString();
        final long partyTimer    = (hour * 3600 + minute * 60) * 1000;

        Log.i(TAG, "Deets: " + postDetail + " Game tag: " + gameTag + " Timer: " + partyTimer + " minutes Vacancy: " + vacancy);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("posts");
        DatabaseReference newPostRef = postsRef.push();
        String postId = newPostRef.getKey();
        Map<String, String> timestamp = ServerValue.TIMESTAMP;
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Post post = new Post(gameName, Integer.parseInt(vacancy), timestamp, user, partyTimer);
        post.setTag(gameTag);
        post.setBody(postDetail);
        post.setId(postId);
        newPostRef.setValue(post);

        //update '/users' database
        DatabaseReference currUserRef = database.getReference("users").child(user + "/posts");
        DatabaseReference newUserPostRef = currUserRef.child(postId);
        newUserPostRef.setValue(post);
        HomeFragment.addPostCount();
        openHomeActivity();
    }

    private void openHomeActivity() {
        //final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
       // fragmentManager.beginTransaction().replace(R.id.flContainer, new HomeFragment()).commit();

        MainActivity m = (MainActivity) getActivity();
        m.fragmentManager.beginTransaction().hide(m.active).show(m.homeFragment).commit();
        m.active = m.homeFragment;
        m.bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void setSpinnerListeners() {
        spinnerGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                   //view.setBackgroundColor(Color.GRAY);
                    return;
                }
                //TODO: change background color according to game selected?
                String game = spinnerGame.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sPartyAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    //view.setBackgroundColor(Color.GRAY);
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean validInput() {
        boolean valid = true;

        if(sPartyAmount.getSelectedItemPosition()== 0){
            valid = false;
            Log.i(TAG, "Party vacancy not selected");
            Toast.makeText(getContext(), "Choose a party vacancy", Toast.LENGTH_SHORT).show();
        }

        if(spinnerGame.getSelectedItemPosition() == 0){
            valid = false;
            Log.i(TAG, "No game");
            Toast.makeText(getContext(), "Select a game", Toast.LENGTH_SHORT).show();
        }

        if(npHour.getValue() *60 + npMinute.getValue() == 0){
            valid = false;
            Log.i(TAG, "Timer is empty");
            Toast.makeText(getContext(), "Please set a timer", Toast.LENGTH_SHORT).show();
        }

        if(spinnerTag.getSelectedItemPosition() == 0){
            valid = false;
            Log.i(TAG, "No game tag");
            Toast.makeText(getContext(), "Choose competitive or casual", Toast.LENGTH_SHORT).show();
        }

        return valid;
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