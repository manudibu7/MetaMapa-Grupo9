package services;

import domain.Contribuyente;
import dtos.input.ContribuyenteInputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.IContribuyentesRepository;

@Service
public class ServicioContribuyente {
    @Autowired
    private IContribuyentesRepository repositorio;

    public long registrarContribuyente(ContribuyenteInputDTO contribuyenteInputDTO){
        Contribuyente nuevo = new Contribuyente(contribuyenteInputDTO.getNombre(),
                                                contribuyenteInputDTO.getApellido(),
                                                contribuyenteInputDTO.getEdad());
        if(contribuyenteInputDTO.getNombre() == null && contribuyenteInputDTO.getApellido() == null){
            nuevo.setAnonimo(true);
        }
        repositorio.guardar(nuevo);
        return nuevo.getId();
    }

    public Contribuyente buscarContribuyente(Long id){
        return repositorio.buscarPorId(id);
    }
}
