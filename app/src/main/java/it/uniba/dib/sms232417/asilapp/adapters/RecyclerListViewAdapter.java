package it.uniba.dib.sms232417.asilapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.utilities.listItem;

public class RecyclerListViewAdapter extends RecyclerView.Adapter<RecyclerListViewAdapter.ViewHolder> {

    private List<listItem> data;

    public RecyclerListViewAdapter(List<listItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        listItem item = data.get(position);
        holder.titolo.setText(item.getTitle());
        holder.descrizione.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titolo;
        public TextView descrizione;

        public ViewHolder(View itemView) {
            super(itemView);
            titolo = itemView.findViewById(R.id.title);
            descrizione = itemView.findViewById(R.id.description);
        }
    }
}