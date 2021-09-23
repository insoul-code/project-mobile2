package app.proyecto.tiendeo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.projecto_final_mv2.R;

public class Login extends AppCompatActivity {

    Button jbtnPasar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        jbtnPasar=findViewById(R.id.btnPasar);
    }


    public void Siguiente (View view){
        Intent nombre = new Intent(this, MainActivity.class);
        startActivity(nombre);
    }
}