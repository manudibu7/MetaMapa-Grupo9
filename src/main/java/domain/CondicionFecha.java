package domain;

import java.time.LocalDate;

/* podria variar si quiere: lo que paso antes de la fecha seleccionada, despues o el mismo dia */
public class CondicionFecha implements InterfaceCondicion {
    private LocalDate fecha;

    @Override
    public boolean cumpleCondicion(Hecho hecho) {
        return this.fecha == hecho.fecha;
    }
}
