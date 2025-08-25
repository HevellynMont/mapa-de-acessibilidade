package com.unify.mapadeacessibilidade.presenter;

import com.unify.mapadeacessibilidade.model.Lugar;
import java.util.List;

public interface MapContract {
    interface View {
        void showLoading(boolean show);
        void displayLocations(List<Lugar> lugares);
        void showErrorMessage(String message);
    }

    interface Presenter {
        void loadAllLocations();
        void attachView(View view);
        void detachView();
    }
}