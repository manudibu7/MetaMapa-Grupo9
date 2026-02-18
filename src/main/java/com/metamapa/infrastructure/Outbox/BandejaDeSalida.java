package com.metamapa.infrastructure.Outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; //para maejar los LocalDateTime
import com.metamapa.domain.MensajeOutbox;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.exceptions.DatosInvalidosException;
import com.metamapa.repository.IOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class BandejaDeSalida {

    @Autowired
    private IOutboxRepository outboxRepository;

    @Transactional
    public void agregar(HechoOutputDTO dto){
        log.info("Agregando hecho a la bandeja de salida");
        if (dto == null) {
            log.warn("Intento de agregar un hecho nulo a la bandeja de salida");
            throw new DatosInvalidosException("El DTO del hecho no puede ser nulo");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(dto);
            MensajeOutbox mensaje = new MensajeOutbox(json);
            outboxRepository.save(mensaje);
            log.info("Hecho agregado a la bandeja de salida con ID: {}", mensaje.getId());
        } catch (Exception e) {
            log.warn("Error al convertir el DTO del hecho a JSON para agregar a la bandeja de salida: {}", e.getMessage());
            throw new DatosInvalidosException("Error al guardar en outbox: " + e.getMessage());
        }
    }

    public List<HechoOutputDTO> pendientesDeEnvio(){
        log.info("Obteniendo hechos pendientes de envío desde la bandeja de salida");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            List<MensajeOutbox> mensajes = outboxRepository.findAll();
            List<HechoOutputDTO> hechos = new ArrayList<>();

            for (MensajeOutbox mensaje : mensajes) {
                HechoOutputDTO hecho = mapper.readValue(mensaje.getHechoJson(), HechoOutputDTO.class);
                hechos.add(hecho);
            }

            log.debug("Leídos {} mensajes de la bandeja de salida", hechos.size());
            return hechos;
        } catch (Exception e) {
            log.warn("Error al leer los mensajes de la bandeja de salida: {}", e.getMessage());
            throw new DatosInvalidosException("Error al leer outbox: " + e.getMessage());
        }
    }

    @Transactional
    public void limpiar(){

        log.info("Limpiando la bandeja de salida después de enviar los hechos");
        outboxRepository.deleteAll();
    }
}
