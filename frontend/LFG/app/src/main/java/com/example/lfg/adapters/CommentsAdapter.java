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
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.interfaces.ItemLongClickListener;
import com.example.lfg.models.Comment;
import com.example.lfg.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>  {

    Context context;
    List<Comment> comments;
    ItemLongClickListener itemLongClickListener;
    ItemClickListener itemClickListener;

    public CommentsAdapter(Context context, List<Comment>  comments, ItemLongClickListener itemLongClickListener, ItemClickListener itemClickListener){
        this.context = context;
        this.comments = comments;
        this.itemLongClickListener = itemLongClickListener;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view, itemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView etComment;
        private TextView etUsername;
        private ImageView ivIcon;

        public ViewHolder(@NonNull View itemView, ItemLongClickListener itemLongClickListener) {
            super(itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemLongClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClicked(getAdapterPosition());
                }
            });
            etComment = itemView.findViewById(R.id.tvComment);
            etUsername = itemView.findViewById(R.id.tvUsername);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }

        public void bind(Comment comment) {
            etComment.setText(comment.getBody());
            etUsername.setText(comment.getUsername());
            ivIcon.setVisibility(View.INVISIBLE);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            storageRef.child("images/"+comment.getUserId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
