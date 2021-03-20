package com.example.lfg.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.lfg.MainActivity;
import com.example.lfg.R;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class LeagueUserFragment extends Fragment{

        private TextView tvCancel;
        private TextView tvSave;
        private EditText etLOLUser;
        FirebaseDatabase database;
        User user;

        String leagueId;
        String leagueSumName;


        private String RIOT_API_KEY = "RGAPI-b409a18d-667d-4a23-ae58-74249c736b06"; //expires after 24 hours [3/18 4:53 pm]
        private static final String summoner_URL_base = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
        //https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/lolo1163?api_key=RGAPI-7dd4cdc6-34b8-4e35-9117-5c5548e0a13d
        private static String match_URL = "https://na1.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/";
        //https://na1.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/ZXjtyVKWGR6x3xVYEo9wDqxYBJhlm50WNGYDBsBq4yWE-1o
        public static String summonerId;

        MainActivity m;

        public LeagueUserFragment() {
            // Required empty public constructor
        }

        public LeagueUserFragment(User user, String userId) {
            this.user = user;
            this.leagueId = userId;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_league_user, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            tvCancel = view.findViewById(R.id.tvCancel);
            tvSave = view.findViewById(R.id.tvSave);
            etLOLUser = view.findViewById(R.id.etLeagueUser);
            etLOLUser.setText(user.getSum_name());
            getActivity().findViewById(R.id.floatingActionButton).setVisibility(View.INVISIBLE); //HIDE FAB
            m = (MainActivity) getActivity();

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m.fragmentManager.popBackStackImmediate();
                }
            });

            tvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String LOLUser = etLOLUser.getText().toString();
                    Log.d("LeagueUserFragment", "LOLUser: " + LOLUser);
                    if(LOLUser.isEmpty()){
                        etLOLUser.setError("Invalid input");
                        return;
                    }

                    authenticate(LOLUser);

                }
            });
        }

        private void authenticate(String LOLUser) {
            String summoner_URL = summoner_URL_base+LOLUser+"?api_key="+RIOT_API_KEY;

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(summoner_URL, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, String response) {
                        // called when response HTTP status is "200 OK"
                        Log.d("ProfileFragment", "onSuccess" + response);
                        JSONObject summoner;

                        try {
                            summoner = new JSONObject(response);
                            summonerId = summoner.getString("id");
                            leagueSumName = summoner.getString("name");
                        }catch (JSONException err){
                            Log.d("Error", err.toString());
                        }

                        database = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = database.getReference("users").child(user.getId());
                        userRef.child("LeagueId").setValue(summonerId);
                        userRef.child("LeagueName").setValue(leagueSumName);
                        Log.d("LeagueUserFragment", "unique summoner id = " + summonerId);
                        Log.d("LeagueUserFragment", "sum name = " + leagueSumName);
                        Toast.makeText(getContext(), "Sum Name is: " + leagueSumName, Toast.LENGTH_SHORT).show();
                        m.fragmentManager.popBackStackImmediate();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String errorResponse, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.d("LeagueUserFragment", "onFailure" + errorResponse);
                        Toast.makeText(getContext(), "not a valid League user", Toast.LENGTH_SHORT).show();
                    }
                }
            );
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
