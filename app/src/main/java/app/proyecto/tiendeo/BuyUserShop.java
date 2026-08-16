package app.proyecto.tiendeo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import app.proyecto.tiendeo.Entities.Product;
import app.proyecto.tiendeo.databinding.ActivityBuyProductUserBinding;

public class BuyUserShop extends AppCompatActivity{

    private ActivityBuyProductUserBinding buyProductUserBinding;
    private Product product;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    EditText jetDireccion, jetUnidades;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buyProductUserBinding = ActivityBuyProductUserBinding.inflate(getLayoutInflater());
        View view = buyProductUserBinding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        db = FirebaseFirestore.getInstance();
        jetDireccion=findViewById(R.id.edtDireccionEnvio);
        jetUnidades=findViewById(R.id.edtStockAvailable);
        buyProductUserBinding.tvNameProduct.setText(product.getName());
        buyProductUserBinding.tvCateogoriaProductBuy.setText(product.getCategory());
        buyProductUserBinding.tvPriceProduct.setText(String.valueOf(product.getPrice()));
        buyProductUserBinding.tvDescriptionBuy.setText(product.getDescription());
        Glide.with(getApplicationContext())
                .load(product.getImage())
                .into(buyProductUserBinding.ivProduct);
    }

    public void updateShop(View v) {
        Context context = getApplicationContext();
        SharedPreferences sharedPref2 = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPref2.getString("email","");


        String unidades = jetUnidades.getText().toString();
        String direccion = jetDireccion.getText().toString();

        String id = product.getId();

        db.collection("products")
                .document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        Long dataStock = documentSnapshot.getLong("stock");

                        int units = Integer.parseInt(unidades);

                        if (dataStock >= units && units != 0 && direccion != ""){
                            int dato = (int) (dataStock - units);
                            Map<String, Object> dataProduct = new HashMap<>();
                            dataProduct.put("stock",dato);

                            db.collection("products")
                                    .document(product.getId())
                                    .update(dataProduct)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error stock", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            Map<String, Object> userShop = new HashMap<>();

                            String name = product.getName();
                            String description = product.getDescription();
                            double price = product.getPrice();
                            String category = product.getCategory();
                            String tienda = product.getNombre_tienda();
                            String image = product.getImage();
                            String user = email;

                            userShop.put("name", name);
                            userShop.put("description", description);
                            userShop.put("units", units);
                            userShop.put("price", price);
                            userShop.put("category", category);
                            userShop.put("nombre_tienda", tienda);
                            userShop.put("user", user);
                            userShop.put("image", image);
                            userShop.put("diretion", direccion);

                            db.collection("shop")
                                    .add(userShop)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            int total = (int) (units*price);
                                            DecimalFormat format = new DecimalFormat("$#,###.###");
                                            String valFormat = format.format(total);

                                            TimerTask Star = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(getApplicationContext(),ListBuyUser.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            };
                                            Timer time = new Timer();
                                            time.schedule(Star,3500);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error al procesar la compra", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else if (units == 0 || direccion == ""){
                            Toast.makeText(getApplicationContext(), "Por favor verifique su dirreccion de entrega y cantidad.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Lo sentimos el articulo no tiene existencias " + dataStock + " unidades", Toast.LENGTH_SHORT).show();
                        }
//                        if (progressDialog.isShowing()){
//                            progressDialog.dismiss();
//                            Intent intent = new Intent(getApplicationContext(),BuyUserShop.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                        else {
//
//                        }
                    }
                }
            }
        });
    }
}
