package com.example.projecto_final_mv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {
    EditText jetnombre, jetcorreo, jetpais, jetciudad, jetcontra;
    Button jbtregistro;
    RadioButton jradioVendedor, jradioUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jetnombre=findViewById(R.id.etnombre);
        jetcorreo=findViewById(R.id.etcorreo);
        jetpais=findViewById(R.id.etcorreo);
        jetciudad=findViewById(R.id.etciudad);
        jetcontra=findViewById(R.id.etcontra);
        jbtregistro=findViewById(R.id.btregistro);
        jradioVendedor=findViewById(R.id.radioVendedor);
        jradioUsuario=findViewById(R.id.radioUsuario);
    }
}