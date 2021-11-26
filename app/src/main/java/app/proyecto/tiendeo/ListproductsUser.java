package app.proyecto.tiendeo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import app.proyecto.tiendeo.AdapterUser.ProductAdapterUser;
import app.proyecto.tiendeo.Entities.Product;
import app.proyecto.tiendeo.databinding.ActivityHomeUserBinding;

public class ListproductsUser extends AppCompatActivity{

    private ActivityHomeUserBinding mainBinding;
    private FirebaseFirestore db;

    ArrayList<Product> productArrayList;
    ProductAdapterUser productAdapterUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        mainBinding = ActivityHomeUserBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        db = FirebaseFirestore.getInstance();
        productArrayList = new ArrayList<>();
        productAdapterUser = new ProductAdapterUser(this,productArrayList,db);
        mainBinding.rvProducts.setHasFixedSize(true);
        mainBinding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rvProducts.setAdapter(productAdapterUser);

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        this.setTitle(email);

        getProducts();
    }

    public void getProducts() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email","");

        db.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(getApplicationContext(), "Faile to retrive data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                productArrayList.add(dc.getDocument().toObject(Product.class));
                            }
                        }
                        productAdapterUser.notifyDataSetChanged();
                    }
                });
    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name","");
        editor.putString("tipo","");
        editor.putString("tienda","");
        editor.putString("correo","");
        editor.putBoolean("session",false);
        editor.commit();

        Intent intent = new Intent(this,Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK
        |Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void listProducts(View view){
        Intent intent = new Intent(this,ListBuyUser.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onClickLogout(View v) {
        logOut();
    }

}
