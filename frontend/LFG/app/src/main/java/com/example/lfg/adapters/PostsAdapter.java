package com.example.lfg.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lfg.R;
import com.example.lfg.interfaces.ItemClickListener;
import com.example.lfg.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    String DATABASEURL = "gs://lfgg-78154.appspot.com";
    Context context;
    List<Post> posts;
    List<Post> filteredPosts;
    ItemClickListener itemClickListener;

    public PostsAdapter(Context context, List<Post> posts, ItemClickListener itemClickListener){
        this.context = context;
        this.posts = posts;
        this.filteredPosts = posts;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_view, parent, false);
        return new ViewHolder(view, itemClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = filteredPosts.get(position);
        holder.bind(post);
    }

    public void filterPosts(String filterTag){
        Log.i("PostsAdapter tag: ", filterTag);
        if(!filterTag.equals("all")) {
            List<Post> filteredList = new ArrayList<>();
            for (Post post : posts) {
                if (post.getGame().equals(filterTag)) {
                    filteredList.add(post);
                }
            }
            filteredPosts = filteredList;
        }
        else{
            filteredPosts = posts;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return filteredPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       //TODO: finish implementing
        private TextView playerCount;
        private TextView timeLeft;
        private TextView tag1;
        private ImageView logo;

        public ViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClicked(getAdapterPosition());
                }
            });
            playerCount = itemView.findViewById(R.id.playerCount);
            timeLeft = itemView.findViewById(R.id.timeLeft);
            tag1 = itemView.findViewById(R.id.tag);
            logo = itemView.findViewById(R.id.logo);
        }

        public void bind(Post post) {
            //TODO: logo fetch from server
            //TODO: duration and manipulating DATE

            StorageReference storageRef = storage.getReferenceFromUrl(DATABASEURL).child(post.getLogoName());

            try {
                final File localFile = File.createTempFile("images", "png");
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        logo.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            } catch (IOException e ) {}

            playerCount.setText(post.getPlayers()+1 + "/" + (post.getSize()+1)); //removed + " players"
            tag1.setText(post.getTag());
            if(tag1.getText().toString().equals("Casual")){
                tag1.setBackgroundColor(Color.parseColor("#4E88E6"));
            }else{
                tag1.setBackgroundColor(Color.parseColor("#E6AC4E"));
            }
            long time = post.getTimeEnd()-System.currentTimeMillis();
            int minutes = (int) (time/60000);
            String format = "%d:%02d";
            String timeMessage = String.format(format, minutes/60, minutes%60);
            if(minutes < 60){
                timeMessage = minutes%60 + " mins";
            }
            if(minutes == 0)
                timeMessage = "< 1 min";
            timeLeft.setText(timeMessage);
        }
    }
}
