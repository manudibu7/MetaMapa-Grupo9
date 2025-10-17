package com.metamapa.domain;

public class CondicionOrigen implements InterfaceCondicion {
    private String origen;

    @Override
    public boolean cumpleCondicion(Hecho hecho) {
        return hecho.getOrigen().equals(this.origen);
    }
}
