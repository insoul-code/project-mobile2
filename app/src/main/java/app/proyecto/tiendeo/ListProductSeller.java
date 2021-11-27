package app.proyecto.tiendeo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.NoCopySpan;
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

import app.proyecto.tiendeo.Adapters.ListProductBuyAdapter;
import app.proyecto.tiendeo.Entities.Shop;
import app.proyecto.tiendeo.databinding.ActivityListSellerSolditemsBinding;

public class ListProductSeller extends AppCompatActivity {
    private FirebaseFirestore db;
    private ActivityListSellerSolditemsBinding mainBinding;

    ArrayList<Shop> shopArrayList;
    ListProductBuyAdapter listProductBuyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_seller_solditems);

        mainBinding =ActivityListSellerSolditemsBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        db = FirebaseFirestore.getInstance();
        shopArrayList = new ArrayList<>();
        listProductBuyAdapter = new ListProductBuyAdapter(this,shopArrayList,db);
        mainBinding.rvListofVentas.setHasFixedSize(true);
        mainBinding.rvListofVentas.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rvListofVentas.setAdapter(listProductBuyAdapter);

        getSellersSold();
    }

    public void getSellersSold(){

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String tienda = sharedPreferences.getString("nombre_tienda", "");

        this.setTitle(tienda);

        db.collection("shop").whereEqualTo("nombre_tienda", tienda)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Faile to retrive data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()){
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                shopArrayList.add(dc.getDocument().toObject(Shop.class));
                            }
                    }
                        listProductBuyAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void irHome(View view){
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
    }
}
