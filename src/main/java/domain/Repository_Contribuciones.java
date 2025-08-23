package domain;
import java.util.List;

public class Repository_Contribuciones {
    private List<Contribucion> contribuciones;

    public void agregarContribucion(Contribucion contribucion){
        contribuciones.add(contribucion);
    }
    //public Contribucion buscarContribuciones() {}
}
