package app.proyecto.tiendeo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import app.proyecto.tiendeo.Entities.Product;
import app.proyecto.tiendeo.databinding.ActivityAddProductBinding;
import app.proyecto.tiendeo.databinding.ActivityEditProductBinding;
import app.proyecto.tiendeo.databinding.ActivityHomeBinding;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityEditProductBinding editProductBinding;
    private Product product;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    Uri imageUri, downloadUri;
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
        Glide.with(getApplicationContext())
                .load(product.getImage())
                .centerCrop()
                .fitCenter()
                .into(editProductBinding.ivProduct);
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
                            imageUri = uri;
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "canceled",Toast.LENGTH_LONG).show();
                        imageUri = null;
                    }
                }
            }
    );

    @Override
    public void onClick(View v) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
        if (imageUri!=null) {
            UploadTask uploadTask = storageReference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();

                        Map<String, Object> dataProduct = new HashMap<>();
                        dataProduct.put("name", editProductBinding.editProduct.getText().toString());
                        dataProduct.put("description", editProductBinding.editDescription.getText().toString());
                        dataProduct.put("price", Double.parseDouble(editProductBinding.editPrice.getText().toString()));
                        dataProduct.put("stock", Integer.parseInt(editProductBinding.editStockAvailable.getText().toString()));
                        dataProduct.put("category", editProductBinding.editCategory.getText().toString());
                        dataProduct.put("image", downloadURL);

                        if (v.getId() == editProductBinding.btnProduct.getId()) {
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
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        }
        else{
            Map<String, Object> dataProduct = new HashMap<>();
            dataProduct.put("name",editProductBinding.editProduct.getText().toString());
            dataProduct.put("description",editProductBinding.editDescription.getText().toString());
            dataProduct.put("price", Double.parseDouble(editProductBinding.editPrice.getText().toString()));
            dataProduct.put("stock", Integer.parseInt(editProductBinding.editStockAvailable.getText().toString()));
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
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(),Home.class);
                startActivity(intent);
                finish();
            }
    }
}
}