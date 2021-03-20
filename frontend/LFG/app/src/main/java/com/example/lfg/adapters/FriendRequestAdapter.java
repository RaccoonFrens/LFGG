package com.example.lfg.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lfg.R;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.models.Post;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    Context context;
    List<User> friends;
    ItemClickListener itemClickListener;
    ItemClickListener acceptClickListener;
    ItemClickListener declineClickListener;

    public FriendRequestAdapter(Context context, List<User> friends, ItemClickListener itemClickListener,
                                ItemClickListener acceptClickListener, ItemClickListener declineClickListener){
        this.context = context;
        this.friends = friends;
        this.itemClickListener = itemClickListener;
        this.acceptClickListener = acceptClickListener;
        this.declineClickListener = declineClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_request, parent, false);
        return new FriendRequestAdapter.ViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = friends.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvUsername;
        ImageView ivAccept;
        ImageView ivDecline;

        public ViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivAccept = itemView.findViewById(R.id.ivAccept);
            ivDecline = itemView.findViewById(R.id.ivDecline);
        }

        public void bind(User user) {

            tvUsername.setText(user.getUsername());

            ivAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptClickListener.onItemClicked(getAdapterPosition());
                }
            });

            ivDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClicked(getAdapterPosition());
                }
            });

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            storageRef.child("images/"+user.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    //comment.setPhotoUrl(uri.toString());
                    //comments.set(i, comment);
                    Log.i("update", uri.toString());
                    Glide.with(context).load(uri.toString()).circleCrop().into(ivIcon);
                    ivIcon.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    //comments.add(comment);
                    Log.i("update", "failed");
                }
            });


        }
    }
}
