package com.unify.mapadeacessibilidade.presenter;

import com.unify.mapadeacessibilidade.data.DatabaseHelper;
import com.unify.mapadeacessibilidade.model.Lugar;
import java.util.List;

public class MapPresenter implements MapContract.Presenter {

    private MapContract.View view;
    private DatabaseHelper databaseHelper;

    public MapPresenter(DatabaseHelper dbHelper) {
        this.databaseHelper = dbHelper;
    }

    @Override
    public void attachView(MapContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadAllLocations() {
        if (view != null) {
            view.showLoading(true);
            List<Lugar> lugares = databaseHelper.getAllLugares();
            view.displayLocations(lugares);
            view.showLoading(false);
        }
    }
}