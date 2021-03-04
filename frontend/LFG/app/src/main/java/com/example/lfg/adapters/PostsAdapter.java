package com.example.lfg.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    String DATABASEURL = "gs://lfgg-78154.appspot.com";
    Context context;
    List<Post> posts;
    ItemClickListener itemClickListener;

    public PostsAdapter(Context context, List<Post> posts, ItemClickListener itemClickListener){
        this.context = context;
        this.posts = posts;
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
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
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
            // timeLeft = itemView.findViewById(R.id.timeLeft);
            tag1 = itemView.findViewById(R.id.tag1);
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

            playerCount.setText(""+post.getSize());
            tag1.setText(post.getTag());
        }
    }
}