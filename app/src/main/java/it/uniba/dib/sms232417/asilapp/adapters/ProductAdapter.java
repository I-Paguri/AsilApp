package it.uniba.dib.sms232417.asilapp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder.OperationItem;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<OperationItem> products;

    public ProductAdapter(List<OperationItem> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        OperationItem product = products.get(position);
        holder.description.setText(product.getDescription());
        holder.productCheckbox.setOnCheckedChangeListener(null); // Imposta il listener a null per evitare comportamenti inaspettati
        holder.productCheckbox.setChecked(product.isChecked()); // Imposta lo stato della checkbox in base al prodotto

        // Aggiungi un listener alla checkbox
        holder.productCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Crea un AlertDialog con un AutoCompleteTextView
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.productCheckbox.getContext());
                builder.setTitle("Inserisci un valore");

                final AutoCompleteTextView input = new AutoCompleteTextView(holder.productCheckbox.getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Aggiungi un bottone per confermare
                builder.setPositiveButton("Conferma", (dialog, which) -> {
                    String value = input.getText().toString();
                    // Fai qualcosa con il valore inserito dall'utente

                    // Crea un secondo AlertDialog per chiedere la categoria
                    AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(holder.productCheckbox.getContext());
                    categoryBuilder.setTitle("Inserisci una categoria");

                    // Inflate the dialog_for_product.xml layout and get the reference to the AutoCompleteTextView
                    LayoutInflater inflater = (LayoutInflater) holder.productCheckbox.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);                    View dialogView = inflater.inflate(R.layout.dialog_for_product, null);
                    final AutoCompleteTextView categoryInput = dialogView.findViewById(R.id.Categories_list);

                    String[] categories = holder.productCheckbox.getContext().getResources().getStringArray(R.array.categories);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.productCheckbox.getContext(), android.R.layout.simple_dropdown_item_1line, categories);
                    categoryInput.setAdapter(adapter);
                    categoryBuilder.setView(dialogView);

                    // Aggiungi un bottone per confermare la categoria
                    categoryBuilder.setPositiveButton("Conferma", (categoryDialog, categoryWhich) -> {
                        String category = categoryInput.getText().toString();
                        // Fai qualcosa con la categoria inserita dall'utente

                        // Rimuovi l'elemento dalla lista di prodotti
                        products.remove(position);
                        // Notifica all'adapter che i dati sono cambiati
                        notifyDataSetChanged();
                    });

                    categoryBuilder.setNegativeButton("Annulla", (categoryDialog, categoryWhich) -> {
                        categoryDialog.cancel();
                        holder.productCheckbox.setChecked(false);
                    });

                    categoryBuilder.show();
                });

                builder.setNegativeButton("Annulla", (dialog, which) -> {
                    dialog.cancel();
                    holder.productCheckbox.setChecked(false);
                });


                builder.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView description;
        CheckBox productCheckbox;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.description);
            productCheckbox = itemView.findViewById(R.id.product_checkbox);
        }
    }
}