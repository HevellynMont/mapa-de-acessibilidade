package com.unify.mapadeacessibilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TelaInicial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        Button btnRegistrar = findViewById(R.id.registrar);
        Button btnEntrar = findViewById(R.id.entrar);
        Button btnVisitante = findViewById(R.id.visitante);

        // Como não há tela de registro, todos os botões levarão para a tela do mapa (MainActivity)
        View.OnClickListener irParaMapaListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaInicial.this, MainActivity.class);
                startActivity(intent);
            }
        };

        // Aplica a mesma ação para todos os botões
        btnRegistrar.setOnClickListener(irParaMapaListener);
        btnEntrar.setOnClickListener(irParaMapaListener);
        btnVisitante.setOnClickListener(irParaMapaListener);
    }
}