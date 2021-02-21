package com.example.lfg.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.example.lfg.R;


public class ComposeFragment extends Fragment {
    public static final String BLANK_FIELD = "Field cannot be left blank";
    public static final String TAG = "ComposeFragment";
    private Spinner spinnerGame;
    private Spinner     sPartyAmount;
    private EditText    etPostDetails;
    private NumberPicker npHour;
    private NumberPicker npMinute;
    private Button      btnPost;

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
        spinnerGame = view.findViewById(R.id.spinnerGame);
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
        final String gameTag    = spinnerGame.getSelectedItem().toString();
        final int hour          = npHour.getValue();
        final int minute        = npMinute.getValue();
        final String vacancy    = sPartyAmount.getSelectedItem().toString();
        final int partyTimer    = hour * 60 + minute;

        Log.i(TAG, "Deets: " + postDetail + " Game tag: " + gameTag + " Timer: " + partyTimer + " minutes Vacancy: " + vacancy);
        Toast.makeText(getContext(), "save post clicked", Toast.LENGTH_SHORT).show();

    }

    private void setSpinnerListeners() {
        spinnerGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    view.setBackgroundColor(Color.GRAY);
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
                    view.setBackgroundColor(Color.GRAY);
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
            Log.i(TAG, "No game tag");
            Toast.makeText(getContext(), "Select a game", Toast.LENGTH_SHORT).show();
        }

        if(npHour.getValue() *60 + npMinute.getValue() == 0){
            valid = false;
            Log.i(TAG, "Timer is empty");
            Toast.makeText(getContext(), "Please set a timer", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }
}