package domain;

import java.time.LocalDate;

/* podria variar si quiere: lo que paso antes de la fecha seleccionada, despues o el mismo dia */
public class CondicionFecha implements InterfaceCondicion {
    private LocalDate fecha;

    //Por ahora 12/06 Pensado para que sea el mismo dia

    @Override
    public boolean cumpleCondicion(Hecho hecho) {
        return this.fecha == hecho.getFecha();
    }
}
