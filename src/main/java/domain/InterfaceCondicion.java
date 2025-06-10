package domain;

import java.time.LocalDate;

public interface InterfaceCondicion {

    public boolean cumpleCondicion(Hecho hecho);

}

public class CondicionCategoria implements InterfaceCondicion {
    private Categoria categoria;
    @Override
    public boolean cumpleCondicion(Hecho hecho) {
        return hecho.getCategoria().getNombre().equals(this.categoria.getNombre());
    }
}

public class CondicionOrigen implements InterfaceCondicion{
    private String origen;
    @Override
    public boolean cumpleCondicion(Hecho hecho) {
        return hecho.getOrigen().equals(this.origen);
    }
}

public class CondicionTitulo implements InterfaceCondicion{
    private String titulo;
    @Override
    public boolean cumpleCondicion(Hecho hecho) {
        return hecho.getTitulo().equals(this.titulo);
    }
}

public class CondicionEtiqueta implements InterfaceCondicion {
    private Etiqueta etiqueta;
    @Override
    public boolean cumpleCondicion(Hecho hecho) {
        return hecho.getEtiqueta().getNombre().equals(this.etiqueta.getNombre());
    }
}

/* podria variar si quiere: lo que paso antes de la fecha seleccionada, despues o el mismo dia */
public class CondicionFecha implements InterfaceCondicion {
    private LocalDate fecha;
    @Override
    public boolean cumpleCondicion(Hecho hecho) {
        return this.fecha == hecho.fecha;
    }
}