package domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class PoliticaEdicion {
    private int duracionVentanaEdicion = 7; // en dias (7 dias por defecto)

    public boolean puedeEditar(Date fechaDeCarga) {
        Date fechaActual = new Date();
        long diferenciaEnMilisegundos = fechaActual.getTime() - fechaDeCarga.getTime();
        long diferenciaEnDias = diferenciaEnMilisegundos / (1000 * 60 * 60 * 24);
        return diferenciaEnDias <= duracionVentanaEdicion;
    }
}
