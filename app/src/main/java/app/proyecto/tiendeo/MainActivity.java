package app.proyecto.tiendeo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import app.proyecto.tiendeo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*])(?=\\S+$).{8,}$");


    EditText jetnombre, jetcorreo, jetpais, jetciudad, jetcontra;
    Button jbtregistro;
    Spinner spinner;
    RadioButton jradioVendedor, jradioUsuario;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jetnombre=findViewById(R.id.etnombre);
        jetcorreo=findViewById(R.id.etcorreo);
        jetpais=findViewById(R.id.etpais);
        jetciudad=findViewById(R.id.etciudad);
        jetcontra=findViewById(R.id.etcontra);
        jbtregistro=findViewById(R.id.btregistro);
//        jbtregistro.setOnClickListener(this);

        spinner=(Spinner)findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
//
    public boolean validar(){
        boolean retorno = true;
        String nombre = jetnombre.getText().toString().trim();
        String ciudad = jetciudad.getText().toString().trim();
        String pais = jetpais.getText().toString().trim();
        String contraInput = jetcontra.getText().toString().trim();
        String emailInput = jetcorreo.getText().toString().trim();

         if (nombre.isEmpty()){
            jetnombre.setError("El campo se debe estar lleno");
            retorno=false;
        }
        if (emailInput.isEmpty()){
            jetcorreo.setError("El campo se debe estar lleno");
             retorno=false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            jetcorreo.setError("Por favor ingrese un correo valido ");
             retorno=false;
        }
         if (pais.isEmpty()){
            jetpais.setError("El campo se debe estar lleno");
             retorno=false;
        }
         if (ciudad.isEmpty()){
            jetciudad.setError("El campo se debe estar lleno");
             retorno=false;
        }
          if (contraInput.isEmpty()){
             jetcontra.setError("El campo se debe estar lleno");
             retorno=false;
         }
         else if (!PASSWORD_PATTERN.matcher(contraInput).matches()){
             jetcontra.setError("La contraseña debe contener una mayuscula, un caracter especial y debe ser de mas de 8 digitos");
             retorno=false;
         }
        else {
            jetcontra.setError(null);
            jetcorreo.setError(null);
            Toast.makeText(this, "Ingreso correctamente", Toast.LENGTH_SHORT).show();;
        }
        createUser();
        return retorno;
    }

    public void Pasar(View view) {
        Intent pasar = new Intent(MainActivity.this, Login.class);
        startActivity(pasar);
    }

    public void createUser(){
        Map<String, Object> register = new HashMap<>();
        register.put("nombre","Jose");
        register.put("email","jose@gmail.com");
        register.put("pais","Polonia");
        register.put("ciudad","Gdansk");
        register.put("pass","12345@");
        register.put("rol","admin");
        register.put("nombre_tienda","Tres Trigos");
        db.collection("usuarios")
                .add(register)
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

    @Override
    public void onClick(View v) {
        validar();
    }

    public void Login (View view){
        Intent login = new Intent(this, Login.class);
        startActivity(login);

    }
}


