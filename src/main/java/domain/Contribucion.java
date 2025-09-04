package domain;
import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;
import java.util.List;

@Getter
@Setter
public class Contribucion {
    private Contribuyente contribuyente;
    private Hecho hecho;
    private List<Revision> listaRevision;
    private Long id;
    private Boolean exportada;



    public Contribucion(Contribuyente contribuyente, Hecho hecho) {
        this.contribuyente = contribuyente.esAnonimo() ? null : contribuyente  ; //Esto hay que catchearlo siempre preguntar si es null o no D:
        this.hecho = hecho;
        this.listaRevision.add(new Revision(contribuyente));
        this.exportada=false;
    }
    //Faltarían Get y Setters?
    //Modificacion de Revision no va en el dominio de Contribucion :D

    //metodos que figuran en el diagrama
    public void editarHecho(){
        //para que ingrese un dato del hecho
        Scanner obj = new Scanner(System.in);
        hecho.cambiarTitulo(obj.nextLine());
        hecho.cambiarDescripcion(obj.nextLine());
        hecho.cambiarCategoria(obj.nextLine());
        hecho.cambiarEtiqueta(obj.nextLine());
        hecho.cambiarFecha(obj.nextLine());
        hecho.cambiarUbicacion(obj.nextLine(), obj.nextLine());
    }
    public void agregarRevision(Revision nuevaRevision){
        listaRevision.add(nuevaRevision);
    }
    public void cambiarEstadoHecho(){
        this.hecho.cambiarEstado(EnumEstadoHecho.DADO_DE_BAJA);
    }
}
