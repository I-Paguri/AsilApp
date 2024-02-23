package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.utilities.listItem;

public class RecyclerListViewAdapter extends RecyclerView.Adapter<RecyclerListViewAdapter.ViewHolder> {

    private List<listItem> data;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public RecyclerListViewAdapter(List<listItem> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        listItem item = data.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());


        if (item.getImageUrl() != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImageUrl());
            // Call the getDownloadUrl() method on the StorageReference to get a Task<Uri>
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Get the download URL when the task completes
                    String downloadUrl = uri.toString();

                    // Use Glide to load the image from the download URL into the ImageView
                    Glide.with(holder.icon.getContext())
                            .load(downloadUrl)
                            .circleCrop()
                            .into(holder.icon);
                }
            });
        } else {
            holder.icon.setImageResource(R.drawable.my_account);
        }



    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView icon;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            icon = itemView.findViewById(R.id.list_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}