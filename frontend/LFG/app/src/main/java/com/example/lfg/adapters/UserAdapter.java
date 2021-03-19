package com.example.lfg.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lfg.R;
import com.example.lfg.interfaces.ItemLongClickListener;
import com.example.lfg.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    List<User> users;
    ItemLongClickListener itemLongClickListener;

    public UserAdapter(Context context, List<User> users, ItemLongClickListener itemLongClickListener ){
        this.context = context;
        this.users = users;
        this.itemLongClickListener = itemLongClickListener;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.party_user, parent, false);
        return new UserAdapter.ViewHolder(view, itemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private ImageView ivIcon;
        public ViewHolder(@NonNull View itemView, ItemLongClickListener itemLongClickListener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemLongClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }

        public void bind(User user) {
            tvUsername.setText(user.getUsername());
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
