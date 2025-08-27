package domain;

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
}
