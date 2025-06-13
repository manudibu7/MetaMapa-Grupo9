package domain;

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
}
