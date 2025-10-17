package com.metamapa.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
public class PoliticaEdicion {
    private int duracionVentanaEdicion = 7; // en dias (7 dias por defecto)

    public boolean puedeEditar(LocalDate fechaDeCarga) {
        Date fechaActual = new Date();
        int diferenciaEnDias = (int) ((fechaActual.getTime() - java.sql.Date.valueOf(fechaDeCarga).getTime()) / (1000 * 60 * 60 * 24));
        return diferenciaEnDias <= duracionVentanaEdicion;
    }
}
