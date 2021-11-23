package app.proyecto.tiendeo.AdapterUser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import app.proyecto.tiendeo.Entities.Product;
import app.proyecto.tiendeo.ListBuyUser;
import app.proyecto.tiendeo.databinding.ProductItemUserBinding;

public class ProductAdapterUser extends RecyclerView.Adapter<ProductAdapterUser.ProductViewHolder> {

    private Context context;
    private ProductItemUserBinding productItemUserBinding;
    private ArrayList<Product> productArrayList;
    private FirebaseFirestore db;
    public ProductAdapterUser(Context context, ArrayList<Product> productArrayList, FirebaseFirestore db){
        this.context = context;
        this.productArrayList = productArrayList;
        this.db = db;
    }

    @NonNull
    @Override
    public ProductAdapterUser.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        productItemUserBinding = productItemUserBinding.inflate(LayoutInflater.from(context));
        return new ProductViewHolder(productItemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.itemUserBinding.tvName.setText(product.getName());
        holder.itemUserBinding.tvDescription.setText(product.getDescription());
        holder.itemUserBinding.tvStock.setText(String.valueOf(product.getStock()));
        holder.itemUserBinding.tvPrice.setText(String.valueOf(product.getPrice()));
        holder.itemUserBinding.tvCategory.setText(product.getCategory());
        holder.itemUserBinding.tvTienda.setText((product.getNombre_tienda()));
        Glide.with(context)
                .load(product.getImage())
                .into(productItemUserBinding.imageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("products").document(product.getId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Data delete", Toast.LENGTH_SHORT).show();
                                productArrayList.remove(holder.getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        holder.itemUserBinding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListBuyUser.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ProductItemUserBinding itemUserBinding;

        public ProductViewHolder(ProductItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            this.itemUserBinding = itemUserBinding;
        }
    }
}
