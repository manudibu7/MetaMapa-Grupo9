package domain;

import java.time.LocalDate;

public class SolicitudEliminacion {

    private Contribuyente solicitante;
    private Hecho hecho;
    private LocalDate fecha;
    private String motivo;
    private EnumEstadoSol estado;

    public SolicitudEliminacion(Contribuyente solicitante, Hecho hecho, LocalDate fecha, String motivo) {
        /**
        if (motivo == null || motivo.length() < 500) {
            throw new IllegalArgumentException("El motivo debe tener al menos 500 caracteres.");
        } COMENTARIO: Se delego al gestor de spam
        */ 
        this.solicitante = solicitante;
        this.hecho = hecho;
        this.fecha = fecha;
        this.motivo = motivo;
        this.estado = EnumEstadoSol.PENDIENTE;
    }

    public void cambiarEstado(EnumEstadoSol nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public Contribuyente getSolicitante(){
        return solicitante;
    }

    public Hecho getHecho(){
        return hecho;
    }

    public String getMotivo(){
        return motivo;
    }

    public EnumEstadoSol getEstado(){
        return estado;
    }
}

/**
 * nota: al crearse una solicitud de eliminacion sobre un hecho, tendra x default el estado pendiente
 * hasta q se acepte o rechace (cambiarEstado(...))
 */
