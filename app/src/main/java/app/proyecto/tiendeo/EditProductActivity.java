package app.proyecto.tiendeo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import app.proyecto.tiendeo.Entities.Product;
import app.proyecto.tiendeo.databinding.ActivityAddProductBinding;
import app.proyecto.tiendeo.databinding.ActivityEditProductBinding;
import app.proyecto.tiendeo.databinding.ActivityHomeBinding;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityEditProductBinding editProductBinding;
    private Product product;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editProductBinding = ActivityEditProductBinding.inflate(getLayoutInflater());
        View view = editProductBinding.getRoot();
        setContentView(view);
        editProductBinding.btnProduct.setOnClickListener(this);
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        db = FirebaseFirestore.getInstance();
        editProductBinding.editProduct.setText(product.getName());
        editProductBinding.editDescription.setText(product.getDescription());
        editProductBinding.editPrice.setText(String.valueOf(product.getPrice()));
        editProductBinding.editStockAvailable.setText(String.valueOf(product.getStock()));
        editProductBinding.editCategory.setText(product.getCategory());
    }

    public void selectImageFromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Obtenemos el resultado de seleccionar la imagen
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        if(uri != null){
                            editProductBinding.ivProduct.setImageURI(uri);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "canceled",Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    @Override
    public void onClick(View v) {
        Map<String, Object> dataProduct = new HashMap<>();
        dataProduct.put("name",editProductBinding.editProduct.getText().toString());
        dataProduct.put("description",editProductBinding.editDescription.getText().toString());
        dataProduct.put("price", Double.valueOf(editProductBinding.editPrice.getText().toString()));
        dataProduct.put("stock", Integer.valueOf(editProductBinding.editStockAvailable.getText().toString()));
        dataProduct.put("category",editProductBinding.editCategory.getText().toString());
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