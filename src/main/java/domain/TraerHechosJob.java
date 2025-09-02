package domain;

/* ---- CON QUARTZ
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.quartz.*;

@Component
public class TraerHechosJob implements  Job{

    private CargadorHechos cargador;

    public TraerHechosJob(CargadorHechos cargador) {
        this.cargador = cargador;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        cargador.cargarHecho();
    }
}*/
 // --- Con ScheduledExecutorService
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit.*;

public class TraerHechosJob {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    //mas que nada para detener una tarea especifica
    private Map<FuenteDemo, ScheduledFuture<?>> fuentes = new HashMap<>();

    //manda la orden para que fuente demo ejecute su metodo, debe tener un handle fuenteDemo
    public void iniciarTarea(FuenteDemo fuente) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                fuente::agregarHecho,
                0,
                60,
                TimeUnit.MINUTES
        );
        fuentes.put(fuente, future);
    }

    public void detenerTarea(FuenteDemo fuente) {
        ScheduledFuture<?> future = fuentes.remove(fuente);
        if(future != null) {
            future.cancel(true);
        }
    }

    public void detenerTodas() {
        scheduler.shutdownNow();
    }

}