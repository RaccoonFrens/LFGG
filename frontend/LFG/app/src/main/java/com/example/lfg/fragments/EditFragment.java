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
import com.example.lfg.models.Post;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class EditFragment extends Fragment {
    public static final String TAG = "MainActivity";
    private Spinner     sPartyAmount;
    private Spinner spinnerGame;
    private Spinner spinnerTag;
    private EditText    etPostDetails;
    private EditText    etGameTags;
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
        etGameTags = view.findViewById(R.id.etGameTags);
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
                if(!validInput())
                    return;
                final String postDetail = etPostDetails.getText().toString();
                final String gameName    = spinnerGame.getSelectedItem().toString();
                final String gameTag    = spinnerTag.getSelectedItem().toString();
                final int hour          = npHour.getValue();
                final int minute        = npMinute.getValue();
                final String vacancy    = sPartyAmount.getSelectedItem().toString();
                final int partyTimer    = hour * 60 + minute;
                //TODO: implement
                Log.i(TAG, "Deets: " + postDetail + " Game tag: " + gameTag + " Timer: " + partyTimer + " minutes Vacancy: " + vacancy);
                Toast.makeText(getContext(), "save post clicked", Toast.LENGTH_SHORT).show();
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
                openHome();
            }
        });

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "other button clicked", Toast.LENGTH_SHORT).show();
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