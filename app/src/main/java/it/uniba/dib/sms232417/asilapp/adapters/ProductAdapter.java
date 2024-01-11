package it.uniba.dib.sms232417.asilapp.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder.OperationItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OperationItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<OperationItem> products;

    public ProductAdapter(List<OperationItem> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        OperationItem product = products.get(position);
        holder.description.setText(product.getDescription());
        holder.quantityProduct.setText(holder.itemView.getContext().getResources().getString(R.string.quantity) + ": "+product.getQuantity());
        holder.itemView.setOnClickListener(v -> Toast.makeText(v.getContext(), "Hai cliccato sul CardView " + product.getDescription(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView description;
        TextView quantityProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            quantityProduct = itemView.findViewById(R.id.quantity);
            description = itemView.findViewById(R.id.description);
        }
    }
}