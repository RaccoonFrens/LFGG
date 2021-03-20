package com.example.lfg.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lfg.R;
import com.example.lfg.adapters.CommentsAdapter;
import com.example.lfg.adapters.FriendRequestAdapter;
import com.example.lfg.adapters.UserAdapter;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.interfaces.ItemLongClickListener;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FriendRequestFragment extends Fragment {
    private RecyclerView rvRequests;
    private RecyclerView rvFriends;

    FriendRequestAdapter friendRequestAdapter;
    UserAdapter friendAdapter;

    List<User> requests;
    List<User> friends;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    String userId;
    String mUsername;

    public FriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRequests = view.findViewById(R.id.rvRequests);
        rvFriends = view.findViewById(R.id.rvFriends);

        prefs = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (mUsername == null)
            mUsername = prefs.getString("username", "username");
        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClicked(int position) {

            }
        };

        ItemClickListener acceptClickListener = new ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                User user = requests.get(position);
                User mUser = new User();
                mUser.setUsername(mUsername);
                mUser.setId(userId);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference newFriendRef = database.getReference("users").child(uId + "/friends").child(user.getId());
                newFriendRef.setValue(user);

                DatabaseReference sendFriendRef = database.getReference("users").child(user.getId() + "/friends").child(uId);
                sendFriendRef.setValue(mUser);

                database.getReference("users").child(uId).child("requests").child(user.getId()).removeValue();

                Toast.makeText(getActivity(), "accept", Toast.LENGTH_SHORT).show();

               // friends.add(user);
                requests.remove(position);
                friendRequestAdapter.notifyDataSetChanged();
                //TODO: notify friendAdapter
            }
        };

        ItemClickListener declineClickListener = new ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                User user = requests.get(position);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                database.getReference("users").child(uId + "/friends").child(user.getId()).removeValue();
                requests.remove(position);
                friendRequestAdapter.notifyDataSetChanged();
            }
        };

        requests = new ArrayList<>();
        friendRequestAdapter = new FriendRequestAdapter(getContext(), requests, itemClickListener, acceptClickListener, declineClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        rvRequests.setLayoutManager(layoutManager);
        rvRequests.setAdapter(friendRequestAdapter);
        ItemLongClickListener itemLongClickListener = new ItemLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {

            }
        };
        friends = new ArrayList<>();
        friendAdapter = new UserAdapter(getContext(), friends, itemLongClickListener);
        rvFriends.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvFriends.setAdapter(friendAdapter);

        getRequests();
        getFriends();
    }

    private void getFriends() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.goOnline();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.getReference("users").child(userId).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    DataSnapshot data = task.getResult();
                    for(DataSnapshot child: data.getChildren()) {
                        String username = (String) child.child("username").getValue();
                        String userId = (String) child.child("id").getValue();
                        User user = new User();
                        user.setUsername(username);
                        user.setId(userId);
                        friends.add(user);
                        Log.i("friend", data.toString());
                        friendAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void getRequests() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.goOnline();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.getReference("users").child(userId).child("requests").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    DataSnapshot data = task.getResult();
                    for(DataSnapshot child: data.getChildren()) {
                        String username = (String) child.child("username").getValue();
                        String userId = (String) child.child("id").getValue();
                        User user = new User();
                        user.setUsername(username);
                        user.setId(userId);
                        requests.add(user);
                        Log.i("request", data.toString());
                        friendRequestAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}