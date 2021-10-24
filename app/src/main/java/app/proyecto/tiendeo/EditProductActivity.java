package app.proyecto.tiendeo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import app.proyecto.tiendeo.Entities.Product;
import app.proyecto.tiendeo.databinding.ActivityAddProductBinding;
import app.proyecto.tiendeo.databinding.ActivityEditProductBinding;
import app.proyecto.tiendeo.databinding.ActivityHomeBinding;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityEditProductBinding editProductBinding;
    private Product product;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        editProductBinding = ActivityEditProductBinding.inflate(getLayoutInflater());
        View view = editProductBinding.getRoot();
        setContentView(view);
        editProductBinding.btnProduct.setOnClickListener(this);
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        db = FirebaseFirestore.getInstance();
        editProductBinding.edtProduct.setText(product.getName());
        editProductBinding.edtDescription.setText(product.getDescription());
        editProductBinding.edtPrice.setText(String.valueOf(product.getPrice()));
        editProductBinding.edtStockAvailable.setText(String.valueOf(product.getStock()));
        editProductBinding.edtCategory.setText(product.getCategory());
    }

    @Override
    public void onClick(View v) {
        Map<String, Object> dataProduct = new HashMap<>();
        dataProduct.put("name",editProductBinding.edtProduct.getText().toString());
        dataProduct.put("description",editProductBinding.edtDescription.getText().toString());
        dataProduct.put("price", Double.parseDouble(editProductBinding.edtPrice.getText().toString()));
        dataProduct.put("stock", Integer.parseInt(editProductBinding.edtStockAvailable.getText().toString()));
        dataProduct.put("category",editProductBinding.edtCategory.getText().toString());
        if (v.getId() == editProductBinding.btnProduct.getId()){
            db.collection("products")
                    .document(product.getId())
                    .update(dataProduct)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getApplicationContext(), "El producto se actualizo correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}