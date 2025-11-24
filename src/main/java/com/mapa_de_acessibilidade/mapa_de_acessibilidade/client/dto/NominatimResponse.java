package com.mapa_de_acessibilidade.mapa_de_acessibilidade.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NominatimResponse {

    @JsonProperty("lat")
    private Double latitude;

    @JsonProperty("lon")
    private Double longitude;

    public NominatimResponse() {}
    
   

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}