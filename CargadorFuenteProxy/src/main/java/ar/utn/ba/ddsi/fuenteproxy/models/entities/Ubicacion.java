package ar.utn.ba.ddsi.fuenteproxy.models.entities;

public class Ubicacion {
    private float latitud;
    private float longitud;

    public Ubicacion(float latitud, float longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public void setUbicacion(float newLatitud, float newLongitud) {
        this.latitud = newLatitud;
        this.longitud = newLongitud;
    }

    public float getLatitud() {return this.latitud;}

    public float getLongitud() {
        return longitud;
    }
}
