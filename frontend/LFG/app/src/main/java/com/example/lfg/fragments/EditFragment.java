package com.example.lfg.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.lfg.models.Comment;
import com.example.lfg.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditFragment extends Fragment {
    public static final String TAG = "MainActivity";
    private Spinner     sPartyAmount;
    private Spinner spinnerGame;
    private Spinner spinnerTag;
    private EditText    etPostDetails;
    private NumberPicker npHour;
    private NumberPicker npMinute;
    private Button      btnPost;
    private Button      btnDeletePost;
    private Button      btnOther;
    Post post;
    FirebaseDatabase database;
    MainActivity m;

    public EditFragment() {
        // Required empty public constructor
    }
    public EditFragment(Post post){
        this.post = post;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sPartyAmount = view.findViewById(R.id.sPartyAmount);
        spinnerGame = view.findViewById(R.id.spinnerGame);
        spinnerTag = view.findViewById(R.id.spinnerTag);
        etPostDetails = view.findViewById(R.id.etPostDetails);
        npHour = view.findViewById(R.id.npHour);
        npMinute = view.findViewById(R.id.npMinute);
        btnPost = view.findViewById(R.id.btnPost);
        btnDeletePost = view.findViewById(R.id.btnDeletePost);
        btnOther = view.findViewById(R.id.btnCancel);

        m = (MainActivity) getActivity();

        database = FirebaseDatabase.getInstance();

        npHour.setMinValue(0);
        npHour.setMaxValue(23);
        npMinute.setMinValue(0);
        npMinute.setMaxValue(59);

        npHour.setValue((int)((post.getTimeEnd() - System.currentTimeMillis()) / 60000 / 60));
        npMinute.setValue((int)((post.getTimeEnd() - System.currentTimeMillis()) / 60000 % 60));

        etPostDetails.setText(post.getBody());

        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.size_array, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPartyAmount.setAdapter(sizeAdapter);
        sPartyAmount.setSelection(post.getSize());

        ArrayAdapter<CharSequence> gameAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.game_array, android.R.layout.simple_spinner_item);
        gameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGame.setAdapter(gameAdapter);
        spinnerGame.setSelection(Arrays.asList(getResources().getStringArray(R.array.game_array)).indexOf(post.getGame()));

        ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tag_array, android.R.layout.simple_spinner_item);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTag.setAdapter(tagAdapter);
        spinnerTag.setSelection(Arrays.asList(getResources().getStringArray(R.array.tag_array)).indexOf(post.getTag()));

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String postDetail = etPostDetails.getText().toString();
                final String gameName   = spinnerGame.getSelectedItem().toString();
                final String gameTag    = spinnerTag.getSelectedItem().toString();
                final int hour          = npHour.getValue();
                final int minute        = npMinute.getValue();
                final String vacancy    = sPartyAmount.getSelectedItem().toString();
                final long partyTimer    = (hour * 3600 + minute * 60) * 1000;

                Log.i(TAG, "Deets: " + postDetail + " Game tag: " + gameTag + " Timer: " + partyTimer + " minutes Vacancy: " + vacancy);
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                //DatabaseReference postRef = database.getReference("posts").child(post.getId());

                HashMap<String, Object> updates = new HashMap<>();
                if(!postDetail.isEmpty()){
                    updates.put("body", postDetail);
                }
                else
                    updates.put("body", post.getBody());
                if(!gameName.isEmpty()){
                    updates.put("game", gameName);
                    updates.put("logoName", gameName+".png");
                }
                else{
                    updates.put("game", post.getGame());
                    updates.put("logoName", post.getLogoName());
                }
                if(!gameTag.isEmpty()){
                    updates.put("tag", gameTag);
                }
                else
                    updates.put("tag", post.getTag());
                if(partyTimer != 0){
                    updates.put("timer", partyTimer);
                    Map<String, String> timestamp = ServerValue.TIMESTAMP;
                    updates.put("timestamp", timestamp);
                }
                else{
                    updates.put("timer", post.getTimer());
                    updates.put("timestamp", post.getTime());
                }
                if(!vacancy.isEmpty()){
                    updates.put("size", Integer.parseInt(vacancy));
                }
                else
                    updates.put("size", post.getSize());
                updates.put("id", post.getId());
                updates.put("user", post.getUser());
                Map<String, Object> updateComments = new HashMap<>();
                List<Comment> comments = post.getReplies();
                for(Comment c: comments){
                    updateComments.put(c.getId(), c);
                }
                updates.put("comments", updateComments);
                Map<String, Object> childUpdates = new HashMap<>();
                String postID = post.getId();
                childUpdates.put("/posts/"+postID, updates);
                childUpdates.put("/users/" + post.getUser()+ "/posts/"+postID, updates);
                database.updateChildren(childUpdates);


                openHomeActivity();
            }
        });

        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "delete post clicked", Toast.LENGTH_SHORT).show();
                String postId = post.getId();
                String userId = post.getUser();
                database.getReference("posts").child(postId).removeValue();
                database.getReference("users").child(userId).child("posts").child(postId).removeValue();
                if(post.getComments() != null) {
                    for (String commentId : post.getComments()) {
                        Log.i("deletePost", commentId);
                        database.getReference("comments").child(commentId).removeValue();
                    }
                }
                HomeFragment.decreasePostCount();
                openHome();
            }
        });

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "edit cancelled", Toast.LENGTH_SHORT).show();
                m.fragmentManager.popBackStackImmediate();
            }
        });

        setSpinnerListeners();
    }

    private void openHome() {
        m.fragmentManager.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void setSpinnerListeners() {
        spinnerGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    view.setBackgroundColor(Color.GRAY);
                    return;
                }
                String game = spinnerGame.getSelectedItem().toString();
                //Toast.makeText(getContext(), "Selected: " + game, Toast.LENGTH_SHORT).show();
                //TODO: change background color according to game selected?
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    view.setBackgroundColor(Color.GRAY);
                    return;
                }
                String tag = spinnerTag.getSelectedItem().toString();
                //Toast.makeText(getContext(), "Selected: " + tag, Toast.LENGTH_SHORT).show();
                //TODO: change background color according to game selected?
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        sPartyAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    view.setBackgroundColor(Color.GRAY);
                    return;
                }
                String thePartyAmount = sPartyAmount.getSelectedItem().toString();
                //Toast.makeText(getContext(), "Selected: " + thePartyAmount, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void openHomeActivity() {
        //final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        // fragmentManager.beginTransaction().replace(R.id.flContainer, new HomeFragment()).commit();

        MainActivity m = (MainActivity) getActivity();
        m.fragmentManager.beginTransaction().hide(m.active).show(m.homeFragment).commit();
        m.active = m.homeFragment;
        m.bottomNavigationView.setSelectedItemId(R.id.home);
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
            Log.i(TAG, "No game tag");
            Toast.makeText(getContext(), "Select a game", Toast.LENGTH_SHORT).show();
        }

        if(spinnerTag.getSelectedItemPosition() == 0){
            valid = false;
            Log.i(TAG, "No game tag");
            Toast.makeText(getContext(), "Choose competitive or casual", Toast.LENGTH_SHORT).show();		            Toast.makeText(getContext(), "Select a game", Toast.LENGTH_SHORT).show();
        }

        if(npHour.getValue() *60 + npMinute.getValue() == 0){
            valid = false;
            Log.i(TAG, "Timer is empty");
            Toast.makeText(getContext(), "Please set a timer", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

}