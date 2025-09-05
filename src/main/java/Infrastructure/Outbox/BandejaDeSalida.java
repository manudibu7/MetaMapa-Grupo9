package Infrastructure.Outbox;

import dtos.output.HechoOutputDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BandejaDeSalida {
    private List<HechoOutputDTO> hechosAExportar = new ArrayList<>();

    public void agregar(HechoOutputDTO dto){
        hechosAExportar.add(dto);
    }

    public List<HechoOutputDTO> pendientesDeEnvio(){
        return new ArrayList<>(hechosAExportar);
    }

    public void limpiar(){
        hechosAExportar.clear();
    }
}
