package domain;

public class GestorDeSolicitudes {
    private DetectorDeSpam detector;

    public GestorDeSolicitudes() {
        this.detector = new DetectorBasicoDeSpam();
    }

    public void procesarSolicitud(String textoSolicitud) {
        if (detector.esSpam(textoSolicitud)) {
            System.out.println("❌ Solicitud rechazada automáticamente por ser SPAM.");
        } else {
            System.out.println("✅ Solicitud válida. Pasará a revisión.");
        }
    }
}