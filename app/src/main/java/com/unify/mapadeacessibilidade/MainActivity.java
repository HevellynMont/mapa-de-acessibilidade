package com.unify.mapadeacessibilidade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context; // Importe o Context
import android.content.SharedPreferences; // Importe SharedPreferences
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import java.util.List;

import com.unify.mapadeacessibilidade.data.DatabaseHelper;
import com.unify.mapadeacessibilidade.model.Lugar;
import com.unify.mapadeacessibilidade.presenter.MapContract;
import com.unify.mapadeacessibilidade.presenter.MapPresenter;

public class MainActivity extends AppCompatActivity implements MapContract.View {

    private MapView map = null;
    private MapContract.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- LINHA ATUALIZADA ---
        // Acessa as preferências de uma forma moderna, sem o método depreciado
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences(ctx.getPackageName() + "_preferences", Context.MODE_PRIVATE));

        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // --- Conexão com o Presenter ---
        presenter = new MapPresenter(new DatabaseHelper(this));
        presenter.attachView(this);
        presenter.loadAllLocations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    // --- Métodos do Contrato ---

    @Override
    public void showLoading(boolean show) {
        if (show) {
            Log.d("MainActivity", "Mostrando loading...");
        } else {
            Log.d("MainActivity", "Escondendo loading...");
        }
    }

    @Override
    public void displayLocations(List<Lugar> lugares) {
        Log.d("MainActivity", "Locais carregados: " + (lugares != null ? lugares.size() : 0));
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}