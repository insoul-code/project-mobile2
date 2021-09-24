package app.proyecto.tiendeo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import app.proyecto.tiendeo.R;

public class Login extends AppCompatActivity {
    Button jbtnPasar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        jbtnPasar=findViewById(R.id.btnPasar);
<<<<<<< HEAD

    }
    public void Pasar(View view){
        Intent pasar = new Intent(Login.this, MainActivity.class);
        startActivity(pasar);
=======
    }

    public void createUser(View view){
        Map<String, Object> userData = new HashMap<>();
        userData.put("email","santiago@gmail.com");
        db.collection("users")
                .add(userData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(),"Hola",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void Siguiente (View view){
        Intent nombre = new Intent(this, MainActivity.class);
        startActivity(nombre);
>>>>>>> develop
    }






}