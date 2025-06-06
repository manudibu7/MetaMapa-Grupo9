package domain;

//revisar y agregar en diagrama los atributos de las clases

public interface Condicion {

    public boolean cumpleCondicion(Hecho hecho);

}

public class CondicionCategoria implements Condicion {
    private Categoria categoria;
    public boolean cumpleCondicion(Hecho hecho) {
        return hecho.categoria.nombre == categoria.nombre;
    }
}

public class CondicionOrigen implements Condicion{
    private String origen;
    public boolean cumpleCondicion(Hecho hecho) {

        return hecho.origen == origen;

    }

}

public class CondicionTitulo implements Condicion{
    private String titulo;
    public boolean cumpleCondicion(Hecho hecho) {

        return hecho.titulo == titulo;

    }
}

public class CondicionEtiqueta implements Condicion {
    private Etiqueta etiqueta;
    public boolean cumpleCondicion(Hecho hecho) {
        return etiqueta.nombre == hecho.etiqueta.nombre;
    }
}

public class CondicionFecha implements Condicion {
    private Fecha fecha;
    public boolean cumpleCondicion(Hecho hecho) {
        return fecha == hecho.fecha;
    }
}