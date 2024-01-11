package it.uniba.dib.sms232417.asilapp.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder.OperationItem;
import it.uniba.dib.sms232417.asilapp.databinding.FragmentLastOperationItemBinding;
import it.uniba.dib.sms232417.asilapp.utilities.listItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OperationItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class OperationItemRecyclerViewAdapter extends RecyclerView.Adapter<OperationItemRecyclerViewAdapter.ViewHolder> {

    private final List<OperationItem>  data;
  // private RecyclerListViewAdapter.OnItemClickListener listener;

    public OperationItemRecyclerViewAdapter(List<OperationItem> items) {
        data = items;
    }

    @Override
    public OperationItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_last_operation_item, parent, false);
      //  return new OperationItemRecyclerViewAdapter.ViewHolder(view, listener);
          return new OperationItemRecyclerViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OperationItemRecyclerViewAdapter.ViewHolder holder, int position) {
        OperationItem item = data.get(position);
        //convert to string money
        String moneyString = String.valueOf(item.getMoney());

        holder.description.setText(item.getDescription());
        holder.money.setText("-"+moneyString+"€");
        holder.operationDate.setText(item.getOperationDate()); // No need to convert to String
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView money;
        public TextView operationDate;

       // public ViewHolder(View itemView, RecyclerListViewAdapter.OnItemClickListener listener) {
         public ViewHolder(View itemView){
            super(itemView);
            description = itemView.findViewById(R.id.description);
            money = itemView.findViewById(R.id.money);
            operationDate = itemView.findViewById(R.id.operationDate);

         /*   itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }

            });

          */
        }

    }
}
