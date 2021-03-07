package com.example.lfg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lfg.R;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.interfaces.ItemLongClickListener;
import com.example.lfg.models.Comment;
import com.example.lfg.models.Post;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>  {

    Context context;
    List<Comment> comments;
    ItemLongClickListener itemLongClickListener;

    public CommentsAdapter(Context context, List<Comment>  comments, ItemLongClickListener itemLongClickListener){
        this.context = context;
        this.comments = comments;
        this.itemLongClickListener = itemLongClickListener;
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

        public ViewHolder(@NonNull View itemView, ItemLongClickListener itemLongClickListener) {
            super(itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemLongClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
            etComment = itemView.findViewById(R.id.tvComment);
            etUsername = itemView.findViewById(R.id.tvUsername);
        }

        public void bind(Comment comment) {
            etComment.setText(comment.getBody());
            etUsername.setText(comment.getUsername());
        }
    }

}
