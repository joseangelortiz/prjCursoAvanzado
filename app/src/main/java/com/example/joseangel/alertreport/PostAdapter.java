package com.example.joseangel.alertreport;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context ctContext;
    private List<UploadClass> uplUploads;


    public PostAdapter(Context context, List<UploadClass> uploadClasses) {
        ctContext = context;
        uplUploads = uploadClasses;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctContext).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        UploadClass uploadCurrent = uplUploads.get(position);
        holder.textViewTitle.setText(uploadCurrent.getTitle());
        holder.textViewLocation.setText(uploadCurrent.getLocation());
        Picasso.with(ctContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(holder.imageViewSelected);

        holder.btnShare.setOnClickListener((v) -> {
            holder.imageViewSelected.buildDrawingCache();
            Bitmap bitmap = holder.imageViewSelected.getDrawingCache();

            try {
                File file = new File(ctContext.getCacheDir(), bitmap + ".png");
                FileOutputStream fOut = null;
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");
                ctContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        holder.textViewDescription.setText(uploadCurrent.getDescription());
    }

    @Override
    public int getItemCount() {
        return uplUploads.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewLocation;
        public TextView textViewDescription;
        public ImageView imageViewSelected;

        public Button btnShare;

        public PostViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.txt_view_title);
            textViewLocation = itemView.findViewById(R.id.txt_view_location);
            textViewDescription = itemView.findViewById(R.id.txt_view_description);
            imageViewSelected = itemView.findViewById(R.id.img_view_selected);
            btnShare = itemView.findViewById(R.id.button_share_post);


        }
    }
}
