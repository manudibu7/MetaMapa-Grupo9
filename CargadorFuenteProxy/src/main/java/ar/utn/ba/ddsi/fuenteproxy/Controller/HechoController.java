package ar.utn.ba.ddsi.fuenteproxy.Controller;


import ar.utn.ba.ddsi.fuenteproxy.Service.IHechoServices;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.Hecho;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hechos")
public class HechoController { //Ver si esta bien el nombre

    @Autowired
    private IHechoServices hechoServices;

    @GetMapping
    public List<Hecho> BuscarHechoes() {
        return this.hechoServices.BuscarHechos();
    }
}
