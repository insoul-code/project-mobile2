package app.proyecto.tiendeo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.HashMap;
import java.util.Map;

import app.proyecto.tiendeo.Entities.Product;
import app.proyecto.tiendeo.databinding.ActivityAddProductBinding;
import app.proyecto.tiendeo.databinding.ActivityEditProductBinding;


public class AddProductActivity extends AppCompatActivity {
    private ActivityAddProductBinding addProductBinding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Product product;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        addProductBinding = ActivityAddProductBinding.inflate(getLayoutInflater());
        View v = addProductBinding.getRoot();
        setContentView(v);
        addProductBinding.btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        db = FirebaseFirestore.getInstance();
        addProductBinding.edtProduct.setText(product.getName());
        addProductBinding.edtDescription.setText(product.getDescription());
        addProductBinding.edtPrice.setText(String.valueOf(product.getPrice()));
        addProductBinding.edtStockAvailable.setText(String.valueOf(product.getStock()));
        addProductBinding.edtCategory.setText(product.getCategory());

    }
    public void selectImageFromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("ïmge/*");
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
                            addProductBinding.ivProduct.setImageURI(uri);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "canceled",Toast.LENGTH_LONG).show();
                    }
                }
            }
    );


    public void onClick(View v) {
        Map<String, Object> dataProduct = new HashMap<>();
        dataProduct.put("name",addProductBinding.edtProduct.getText().toString());
        dataProduct.put("description",addProductBinding.edtDescription.getText().toString());
        dataProduct.put("price", Double.parseDouble(addProductBinding.edtPrice.getText().toString()));
        dataProduct.put("stock", Integer.parseInt(addProductBinding.edtStockAvailable.getText().toString()));
        dataProduct.put("category",addProductBinding.edtCategory.getText().toString());
        if (v.getId() == addProductBinding.btnProduct.getId()){
            db.collection("products")
                    .add(dataProduct)
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