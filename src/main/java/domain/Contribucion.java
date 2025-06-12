package domain;

public class Contribucion {
    private Contribuyente contribuyente;
    private Hecho hecho;
    private Revision revision;


    public Contribucion(Contribuyente contribuyente, Hecho hecho) {
        this.contribuyente = contribuyente.esAnonimo() ? null : contribuyente  ; //Esto hay que catchearlo siempre preguntar si es null o no D:
        this.hecho = hecho;
        this.revision = new Revision(contribuyente);
    }
    //Faltarían Get y Setters?
    //Modificacion de Revision no va en el dominio de Contribucion :D
}
