package app.proyecto.tiendeo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import app.proyecto.tiendeo.Adapters.ProductAdapter;
import app.proyecto.tiendeo.Entities.Product;
import app.proyecto.tiendeo.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    private ActivityHomeBinding mainBinding;
    private FirebaseFirestore db;
    ArrayList<Product> productArrayList;
    ProductAdapter productAdapter;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        db = FirebaseFirestore.getInstance();
        productArrayList = new ArrayList<>();
        productAdapter = new ProductAdapter(this,productArrayList, db);
        mainBinding.rvProducts.setHasFixedSize(true);
        mainBinding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rvProducts.setAdapter(productAdapter);
        getProducts();
    }

    public void getProducts(){

        db.collection("products")
//                .orderBy("price", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(getApplicationContext(), "Failed to retrive that", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                productArrayList.add(dc.getDocument().toObject(Product.class));
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void logout(View view){
        Intent logout = new Intent(this, Login.class);
        startActivity(logout);
    }
    public void createproduct(View v) {
        Intent CreateProduct = new Intent(this, AddProductActivity.class);
        startActivity(CreateProduct);
    }
    }