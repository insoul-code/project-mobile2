package app.proyecto.tiendeo.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

import app.proyecto.tiendeo.Entities.Shop;
import app.proyecto.tiendeo.databinding.ProductItemSellerSoldBinding;

public class ListProductBuyAdapter extends RecyclerView.Adapter<ListProductBuyAdapter.ProductViewHolder> {
    private Context context;
    private ProductItemSellerSoldBinding productItemSellerSoldBinding;
    private ArrayList<Shop> shopArrayList;
    private FirebaseFirestore db;

    public ListProductBuyAdapter(Context context, ArrayList<Shop> shopArrayList, FirebaseFirestore db) {
        this.context = context;
        this.shopArrayList = shopArrayList;
        this.db = db;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        productItemSellerSoldBinding = productItemSellerSoldBinding.inflate(LayoutInflater.from(context));
        return new ProductViewHolder(productItemSellerSoldBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Shop shop = shopArrayList.get(position);
        holder.itemSellerSoldBinding.tvName.setText(shop.getName());
        holder.itemSellerSoldBinding.tvStock.setText(String.valueOf(shop.getUnits()));
        DecimalFormat formato = new DecimalFormat("$#,###.###");
        String valorFormateado = formato.format((shop.getPrice()*shop.getUnits()));
        holder.itemSellerSoldBinding.tvPrice.setText(valorFormateado);
        holder.itemSellerSoldBinding.tvDireccionEnvioProduct.setText(shop.getDirection());
        holder.itemSellerSoldBinding.tvClient.setText(shop.getUser());
        holder.itemSellerSoldBinding.tvDatePurchased.setText(shop.getFecha());
        Glide.with(context)
                .load(shop.getImage())
                .into(productItemSellerSoldBinding.imageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("shop").document(shop.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Data delete", Toast.LENGTH_SHORT).show();
                        shopArrayList.remove(holder.getAdapterPosition());
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
        return shopArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ProductItemSellerSoldBinding itemSellerSoldBinding;

        public ProductViewHolder(ProductItemSellerSoldBinding itemSellerSoldBinding) {
            super(itemSellerSoldBinding.getRoot());
            this.itemSellerSoldBinding = itemSellerSoldBinding;
        }
    }
}
