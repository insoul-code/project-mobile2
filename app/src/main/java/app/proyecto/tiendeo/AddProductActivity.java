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
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.HashMap;
import java.util.Map;

import app.proyecto.tiendeo.databinding.ActivityAddProductBinding;
import app.proyecto.tiendeo.databinding.ActivityEditProductBinding;


public class AddProductActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityAddProductBinding addProductBinding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public Product product;
    private FirebaseFirestore db;
    EditText jprice, jstock, jproduct, jdescription, jcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        jproduct=findViewById(R.id.edtProduct);
        jcategory=findViewById(R.id.edtCategory);
        jdescription=findViewById(R.id.edtDescription);
        jstock=findViewById(R.id.edtStockAvailable);
        jprice=findViewById(R.id.edtPrice);
        db = FirebaseFirestore.getInstance();
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
                            addProductBinding.ivProduct.setImageURI(uri);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "canceled",Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    public boolean validar(){
        boolean retorno = true;
        String product = jproduct.getText().toString().trim();
        //String price = jprice.getText().toString().trim();
        String description = jdescription.getText().toString().trim();
        //String stock = jstock.getText().toString().trim();
        String category = jcategory.getText().toString().trim();

        if (product.isEmpty()||description.isEmpty()||category.isEmpty()){
            jcategory.setError("el campo debe estar lleno");
            jprice.setError("el campo debe estar lleno");
            jstock.setError("el campo debe estar lleno");
            jproduct.setError("el campo debe estar lleno");
            jdescription.setError("el campo debe estar lleno");
        }
        else {
            crearproducto();
            Toast.makeText(this, "Productos ingresados correctamente", Toast.LENGTH_SHORT).show();
        }
        return retorno;
    }

public void crearproducto(){
    String product = jproduct.getText().toString().trim();
    Double price = Double.valueOf(jprice.getText().toString().trim());
    String description = jdescription.getText().toString().trim();
    Integer stock = Integer.valueOf(jstock.getText().toString().trim());
    String category = jcategory.getText().toString().trim();
    Map<String, Object> dataProduct = new HashMap<>();
    dataProduct.put("name",product);
    dataProduct.put("description",description);
    dataProduct.put("price", price);
    dataProduct.put("stock", stock);
    dataProduct.put("category",category);
    db.collection("products")
            .add(dataProduct)
            .addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(), "El producto se actualizo correctamente", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
            });

}

    @Override
    public void onClick(View v) {
        String product = jproduct.getText().toString().trim();
        //String price = jprice.getText().toString().trim();
        String description = jdescription.getText().toString().trim();
       // String stock = jstock.getText().toString().trim();
        String category = jcategory.getText().toString().trim();
        if (product.isEmpty()||description.isEmpty()||category.isEmpty()){
            validar();
        }else {
            crearproducto();
        }
    }
}

