package ar.utn.ba.ddsi.fuenteproxy.models.entities;

import java.time.LocalDateTime;
import java.util.List;
//Tengo dudas  sobre como está creada esta clase
public abstract class FuenteProxy {
    public LocalDateTime ultimaConsulta = LocalDateTime.now(); //Está bien setear de una este horario? está bein public?

    //Metodos especiales :D
    public List<Hecho> obtenerHechos(){
        return null;
    }
    //Getters y Setters
    public LocalDateTime getUltimaConsulta() {
        return ultimaConsulta;
    }

    public void setUltimaConsulta(LocalDateTime ultimaConsulta) {
        this.ultimaConsulta = ultimaConsulta;
    }
}
