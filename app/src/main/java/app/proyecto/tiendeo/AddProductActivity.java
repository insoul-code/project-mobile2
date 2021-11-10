package app.proyecto.tiendeo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import app.proyecto.tiendeo.databinding.ActivityAddProductBinding;
import app.proyecto.tiendeo.databinding.ActivityEditProductBinding;


public class AddProductActivity extends AppCompatActivity{
    private ActivityAddProductBinding addProductBinding;
//    private FirebaseStorage storage;
//    private StorageReference storageReference;
//    public Product product;
//    private FirebaseFirestore db;
//    private Uri postImageUri;
//    private static final int GALLERY_INTENT = 1;
    Button btnImage, btnAddProduct;
    EditText jprice, jstock, jproduct, jdescription, jcategory;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

//    private ActivityAddProductBinding addProductBinding;
    Uri imageUri, downloadUrl;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProductBinding = ActivityAddProductBinding.inflate(getLayoutInflater());
        View v = addProductBinding.getRoot();
        setContentView(v);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnAddProduct = findViewById(R.id.btnProduct);
        btnImage = findViewById(R.id.btn_image);
        jproduct=findViewById(R.id.edtProduct);
        jcategory=findViewById(R.id.edtCategory);
        jdescription=findViewById(R.id.edtDescription);
        jstock=findViewById(R.id.edtStockAvailable);
        jprice=findViewById(R.id.edtPrice);

        Context context = getApplicationContext();
    }

    public void SeletImageFromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        if (uri != null){
                            addProductBinding.ivProduct.setImageURI(uri);
                            imageUri = uri;
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Canceled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public void createProgress(View view){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creando producto");
        progressDialog.show();
    }

    public boolean addProduct(View view){
        String product = jproduct.getText().toString().trim();
        String price = jprice.getText().toString().trim();
        String description = jdescription.getText().toString().trim();
        String stock = jstock.getText().toString().trim();
        String category = jcategory.getText().toString().trim();

        if (imageUri == null){
            Toast.makeText(getApplicationContext(),"Ingrese la imagen de su producto", Toast.LENGTH_SHORT).show();
        }if (product.isEmpty()){
            jproduct.setError("Ingrese el nombre de su producto");
            jproduct.requestFocus();
            return false;
        }if (price.isEmpty()){
            jprice.setError("Ingrese el precio de su producto");
            jprice.requestFocus();
            return false;
        }if (stock.isEmpty()){
            jstock.setError("Ingrese el stock de su producto");
            jstock.requestFocus();
            return false;
        }if (category.isEmpty()){
            jcategory.setError("Ingrese la categoria de su producto");
            jcategory.requestFocus();
            return false;
        }if (description.isEmpty()){
            jdescription.setError("Ingrese la descripcion de su producto");
            jdescription.requestFocus();
            return false;
        }
        else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo imagen");
            progressDialog.show();

            SimpleDateFormat format = new SimpleDateFormat(
                    "YYYY_MM_dd_HH_mm_ss.SSS", Locale.US);
            Date date = new Date();
            String filename = format.format(date);
            //asignacion del nombre de la carpeta y nombre del archivo
            storageReference = FirebaseStorage.getInstance()
                    .getReference("products/" + filename);
            //imagen a subir al storage
            UploadTask uploadTask = storageReference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()){
                        return storageReference.getDownloadUrl();
                    }else{
                        Toast.makeText(getApplicationContext(),"Error al subir imagen", Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUrl = task.getResult();
                    String downloadURL = downloadUrl.toString();

                    String product = jproduct.getText().toString().trim();
                    Double price = Double.valueOf(jprice.getText().toString().trim());
                    String description = jdescription.getText().toString().trim();
                    Integer stock = Integer.valueOf(jstock.getText().toString().trim());
                    String category = jcategory.getText().toString().trim();
                    String uri = downloadURL;

                    Map<String, Object> dataProduct = new HashMap<>();
                    dataProduct.put("name",product);
                    dataProduct.put("description",description);
                    dataProduct.put("price", price);
                    dataProduct.put("stock", stock);
                    dataProduct.put("category",category);
                    dataProduct.put("image", uri);

                    db.collection("products")
                            .add(dataProduct)
                            .addOnSuccessListener(unused -> {
//                    progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "El producto se actualizo correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
                            });
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(),Home.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
        return true;
    }
}

