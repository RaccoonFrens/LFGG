package com.example.lfg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lfg.R;
import com.example.lfg.interfaces.ItemLongClickListener;
import com.example.lfg.models.User;

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
        public ViewHolder(@NonNull View itemView, ItemLongClickListener itemLongClickListener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
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
        }
    }
}
