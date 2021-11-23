package app.proyecto.tiendeo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {
    Button jbtnPasar;
    EditText jetcorreo, jetcontra;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        jetcontra=findViewById(R.id.etcontrasena);
        jetcorreo=findViewById(R.id.etcorreoelectro);
        jbtnPasar=findViewById(R.id.btnPasar);
        firebaseAuth = FirebaseAuth.getInstance();


    }

    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(Login.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(Login.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(Login.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(Login.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                jetcorreo.setError("La dirección de correo electrónico está mal formateada.");
                jetcorreo.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(Login.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                jetcontra.setError("la contraseña es incorrecta ");
                jetcontra.requestFocus();
                jetcontra.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(Login.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(Login.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(Login.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(Login.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                jetcorreo.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                jetcorreo.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(Login.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(Login.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(Login.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(Login.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(Login.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(Login.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(Login.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                jetcontra.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                jetcontra.requestFocus();
                break;

        }
    }




    public void onCLickLogin(View view) {
        String email = jetcorreo.getText().toString();
        String pass = jetcontra.getText().toString();

        db.collection("usuarios").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre = (String) document.get("nombre");
                                String rol = (String) document.get("rol");
                                String nombre_tienda = (String) document.get("nombre_tienda");

                                Context context = getApplicationContext();
                                SharedPreferences sharedPref = context.getSharedPreferences(
                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("nombre", nombre);
                                editor.putString("rol", rol);
                                editor.putString("nombre_tienda", nombre_tienda);
                                editor.putString("email", email);
                                editor.putBoolean("session", true);
                                editor.commit();

                            }
                        } else {
                            String errorcode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            dameToastdeerror(errorcode);
                        }
                    }

                });

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(Login.this, "Los Campos son requeridos", Toast.LENGTH_LONG).show();
        } else {

            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //irahome();
                        Context context = getApplicationContext();
                        SharedPreferences sharedPref = context.getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        String tipousuario = sharedPref.getString("rol","");

                        String t = "Vendedor";
                        if(tipousuario.equals("Usuario")){
                            listUser();
                        }
                        else if(tipousuario.equals("Vendedor")){
                            list();
                        }

                    }
                    else {
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        dameToastdeerror(errorCode);
                    }
                }

            });



        }
    }

    public void Registrarme (View view){
        Intent nombre = new Intent(this, MainActivity.class);
        startActivity(nombre);

    }

    private void listUser(){
        Intent intent = new Intent(this,ListproductsUser.class);
        startActivity(intent);
    }

    private void list(){
        Intent intent1 = new Intent(this,Home.class);
        startActivity(intent1);
    }


}