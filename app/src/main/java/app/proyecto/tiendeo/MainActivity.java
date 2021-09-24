package app.proyecto.tiendeo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import app.proyecto.tiendeo.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*])(?=\\S+$).{8,}$");


    EditText jetnombre, jetcorreo, jetpais, jetciudad, jetcontra;
    Button jbtregistro;
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
    }
    private boolean validateCorreo(){
        String emailInput = jetcorreo.getText().toString().trim();

        if (emailInput.isEmpty()){
            jetcorreo.setError("El campo se debe estar lleno");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            jetcorreo.setError("Por favor ingrese un correo valido ");
            return false;
        }
        else {
            jetcorreo.setError(null);
            return true;
        }
    }
    private boolean validateContra(){
        String contraInput = jetcontra.getText().toString().trim();
        if (contraInput.isEmpty()){
            jetcontra.setError("El campo se debe estar lleno");
            return false;
        }else if (!PASSWORD_PATTERN.matcher(contraInput).matches()){
            jetcontra.setError("La contraseña debe contener una mayuscula, un caracter especial y debe ser de mas de 8 digitos");
            return false;
        }
        else {
            jetcontra.setError(null);
            return true;
        }
    }
    private boolean validateCampos(){
        String nombre = jetnombre.getText().toString().trim();
        String ciudad = jetciudad.getText().toString().trim();
        String pais = jetpais.getText().toString().trim();
        if (nombre.isEmpty()){
            jetnombre.setError("El campo debe estar completo");
            return false;
        }
        else if (ciudad.isEmpty()){
            jetciudad.setError("El campo debe estar completo");
            return false;
        }
        else if (pais.isEmpty()){
            jetpais.setError("El campo debe estar completo");
            return false;
        }
        else {
            Toast.makeText(this, "Ingreso correctamente", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void Pasar(View view) {
        Intent pasar = new Intent(MainActivity.this, Login.class);
        startActivity(pasar);
        validateContra();
        validateCorreo();
        validateCampos();

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
        validateContra();
        validateCorreo();
        validateCampos();
        /*createUser();*/
        jbtregistro=findViewById(R.id.btregistro);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void Login (View view){
        Intent login = new Intent(this, Login.class);
        startActivity(login);

    }
}


