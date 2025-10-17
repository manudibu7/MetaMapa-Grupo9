package com.metamapa.infrastructure.Outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; //para maejar los LocalDateTime
import com.metamapa.domain.MensajeOutbox;
import com.metamapa.dtos.output.HechoOutputDTO;
import com.metamapa.exceptions.DatosInvalidosException;
import com.metamapa.repository.IOutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class BandejaDeSalida {

    @Autowired
    private IOutboxRepository outboxRepository;

    @Transactional
    public void agregar(HechoOutputDTO dto){
        if (dto == null) {
            throw new DatosInvalidosException("El DTO del hecho no puede ser nulo");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(dto);
            MensajeOutbox mensaje = new MensajeOutbox(json);
            outboxRepository.save(mensaje);
        } catch (Exception e) {
            throw new DatosInvalidosException("Error al guardar en outbox: " + e.getMessage());
        }
    }

    public List<HechoOutputDTO> pendientesDeEnvio(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            List<MensajeOutbox> mensajes = outboxRepository.findAll();
            List<HechoOutputDTO> hechos = new ArrayList<>();

            for (MensajeOutbox mensaje : mensajes) {
                HechoOutputDTO hecho = mapper.readValue(mensaje.getHechoJson(), HechoOutputDTO.class);
                hechos.add(hecho);
            }

            return hechos;
        } catch (Exception e) {
            throw new DatosInvalidosException("Error al leer outbox: " + e.getMessage());
        }
    }

    @Transactional
    public void limpiar(){
        outboxRepository.deleteAll();
    }
}
