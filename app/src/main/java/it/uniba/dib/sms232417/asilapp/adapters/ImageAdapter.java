
package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.utilities.DownloadImageTask;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> thumbnailUrls;
    private List<String> videoUrls; // List of video URLs
    private Context context;

    public ImageAdapter(Context context, List<String> thumbnailUrls, List<String> videoUrls) {
        this.context = context;
        this.thumbnailUrls = thumbnailUrls;
        this.videoUrls = videoUrls; // Initialize video URLs
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        new DownloadImageTask(holder.imageView, holder.progressBar).execute(thumbnailUrls.get(position));
        Log.i("ImageAdapter", "Downloading image for position: " + position + " URL: " + thumbnailUrls.get(position));

        // Add click listener to ImageView
        holder.imageView.setOnClickListener(v -> {
            String videoUrl = videoUrls.get(position); // Get the video URL
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoUrl));

            // Check if the YouTube app is installed
            if (appIntent.resolveActivity(context.getPackageManager()) != null) {
                // If YouTube app is installed, use it to open the video
                context.startActivity(appIntent);
            } else {
                // If YouTube app is not installed, use the web browser to open the video
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                context.startActivity(webIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return thumbnailUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}