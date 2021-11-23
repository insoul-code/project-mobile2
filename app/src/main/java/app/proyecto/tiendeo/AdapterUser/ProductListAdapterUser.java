package app.proyecto.tiendeo.AdapterUser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import app.proyecto.tiendeo.Entities.Shop;
import app.proyecto.tiendeo.databinding.ProductItemUserPurchasedBinding;

public class ProductListAdapterUser extends RecyclerView.Adapter<ProductListAdapterUser.ProductViewHolder> {

    private Context context;
    private ProductItemUserPurchasedBinding productItemUserPurchasedBinding;
    private ArrayList<Shop> productListArrayList;
    private FirebaseFirestore db;
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        productItemUserPurchasedBinding = productItemUserPurchasedBinding.inflate(LayoutInflater.from(context));
        return new ProductViewHolder(productItemUserPurchasedBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Shop shopping = productListArrayList.get(position);
        holder.itemBinding.tvName.setText(shopping.getName());
        holder.itemBinding.tvCantidad.setText(String.valueOf(shopping.getUnits()));
        DecimalFormat formato = new DecimalFormat("$#,###.###");
        String valorFormateado = formato.format((shopping.getPrice()*shopping.getUnits()));
        holder.itemBinding.tvPrice.setText(valorFormateado);
        holder.itemBinding.tvDireccionCompra.setText(shopping.getDescription());
        holder.itemBinding.tvTienda.setText(shopping.getNombre_tienda());
        Glide.with(context)
                .load(shopping.getImage())
                .into(productItemUserPurchasedBinding.imageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("products").document(shopping.getId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Data delete", Toast.LENGTH_SHORT).show();
                                productListArrayList.remove(holder.getAdapterPosition());
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
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productListArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ProductItemUserPurchasedBinding itemBinding;
        public ProductViewHolder(@NonNull ProductItemUserPurchasedBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }
}
