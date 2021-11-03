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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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
    FirebaseAuth firebaseAuth;

    EditText jetnombre, jetcorreo, jetpais, jetciudad, jetcontra, jetSelectRol;
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
        firebaseAuth = FirebaseAuth.getInstance();

        String[] ListItems = getResources().getStringArray(R.array.options_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, ListItems);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.selectRol);
        textView.setAdapter(adapter);
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
        createUserAuthen();
        return retorno;
    }

    public void Pasar(View view) {
        Intent pasar = new Intent(MainActivity.this, Login.class);
        startActivity(pasar);
    }

    public void createUser(){
        Map<String, Object> register = new HashMap<>();
        register.put("nombre",jetnombre.getText().toString());
        register.put("email",jetcorreo.getText().toString());
        register.put("pais",jetpais.getText().toString());
        register.put("ciudad",jetciudad.getText().toString());
        register.put("pass",jetcontra.getText().toString());
        register.put("rol","");
        register.put("nombre_tienda","");
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
    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(MainActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(MainActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(MainActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(MainActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                jetcorreo.setError("La dirección de correo electrónico está mal formateada.");
                jetcorreo.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(MainActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                jetcontra.setError("la contraseña es incorrecta ");
                jetcontra.requestFocus();
                jetcontra.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(MainActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(MainActivity.this,"Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(MainActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(MainActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                jetcorreo.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                jetcorreo.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(MainActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(MainActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(MainActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(MainActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(MainActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(MainActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(MainActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                jetcontra.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                jetcontra.requestFocus();
                break;

        }

    }

    public void createUserAuthen(){
        String email = jetcorreo.getText().toString();
        String pass = jetcontra.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Usuario Creado con Exito",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    String errorcode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    dameToastdeerror(errorcode);


                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        String nombre = jetnombre.getText().toString().trim();
        String ciudad = jetciudad.getText().toString().trim();
        String pais = jetpais.getText().toString().trim();
        String contraInput = jetcontra.getText().toString().trim();
        String emailInput = jetcorreo.getText().toString().trim();
        if(nombre.isEmpty()||ciudad.isEmpty()||pais.isEmpty()||contraInput.isEmpty()||emailInput.isEmpty()){
            Toast.makeText(MainActivity.this,"Todos los campos deben estar diligenciados",Toast.LENGTH_SHORT).show();
        } else {
            validar();
        }

    }

    public void Login (View view){
        Intent login = new Intent(this, Login.class);
        startActivity(login);

    }


}


