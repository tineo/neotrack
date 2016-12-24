package com.makinap.tineo.neotrack;

import com.makinap.tineo.neotrack.data.Tienda;

import java.util.List;

/**
 * Created by tineo on 25/09/16.
 */
public class SearchTiendaModel {
    private String response;
    private String message;
    private List<Tienda> tiendas;


    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public List<Tienda> getFriends() {
        return tiendas;
    }
    public void setFriends(List<Tienda> tiendas) {
        this.tiendas = tiendas;
    }
}
