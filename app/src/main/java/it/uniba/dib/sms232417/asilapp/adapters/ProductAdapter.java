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
import it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder.ProductItem;
/**
 * {@link RecyclerView.Adapter} that can display a {@link ProductItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductItem> products;

    public ProductAdapter(List<ProductItem> products) {
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
        ProductItem product = products.get(position);
        holder.productName.setText(product.getNameProduct());
        holder.quantityProduct.setText(holder.itemView.getContext().getResources().getString(R.string.quantity) + ": "+product.getQuantityProduct());
        holder.itemView.setOnClickListener(v -> Toast.makeText(v.getContext(), "Hai cliccato sul CardView " + product.getNameProduct(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        TextView quantityProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            quantityProduct = itemView.findViewById(R.id.quantity);
            productName = itemView.findViewById(R.id.productName);
        }
    }
}