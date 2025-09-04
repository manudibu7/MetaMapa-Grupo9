package domain;

import java.util.Date;

public class PoliticaEdicion {
    private int duracionVentanaEdicion; // en dias (7 dias por defecto)

    public PoliticaEdicion(int duracionVentanaEdicion) {
        this.duracionVentanaEdicion = duracionVentanaEdicion;
    }

    public boolean puedeEditar(Date fechaDeCarga) {
        Date fechaActual = new Date();
        long diferenciaEnMilisegundos = fechaActual.getTime() - fechaDeCarga.getTime();
        long diferenciaEnDias = diferenciaEnMilisegundos / (1000 * 60 * 60 * 24);
        return diferenciaEnDias <= duracionVentanaEdicion;
    }
}
