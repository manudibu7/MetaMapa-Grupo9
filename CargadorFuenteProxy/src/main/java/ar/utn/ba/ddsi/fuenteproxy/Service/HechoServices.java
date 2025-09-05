package ar.utn.ba.ddsi.fuenteproxy.Service;

import ar.utn.ba.ddsi.fuenteproxy.models.Repository.IHechoRepository;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.Fuente;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.FuenteMetamapa;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.FuenteProxy;
import ar.utn.ba.ddsi.fuenteproxy.models.entities.Hecho;
import ar.utn.ba.ddsi.fuenteproxy.models.factory.FactoryFuenteProxy;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class HechoServices implements IHechoServices {
    private List<FuenteProxy> fuenteProxis;
    private Long ultimoId;
    private FactoryFuenteProxy factoryFuenteProxy;
    private IHechoRepository hechoRepository;

    @Override
    public List<Hecho> BuscarHechos() {
        List<Hecho> hechosObtenidos = new ArrayList<>();
        fuenteProxis.forEach(fuenteProxy -> hechosObtenidos.addAll(fuenteProxy.obtenerHechos()));
        return hechosObtenidos;
    }

    @Override
    public void BuscarNuevasFuentes() {
        List<Fuente> fuentes =this.hechoRepository.buscarNuevasRutas(this.ultimoId);
        String ruta = "dsdas";
        URL url = null;
        try {
            url = new URL(ruta);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        fuenteProxis.add(this.factoryFuenteProxy.createFuenteMetamapa(url));
    }
}
