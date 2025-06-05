package domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class LectosCSV implements ILector {

    @Override
    public List<Hecho> obtencionHechos(String ruta) {
        String partes[];
        Hecho hecho = null;
        Hecho repetido = null;
        List<Hecho> hechos = new ArrayList<>();

        try (Scanner lector = new Scanner(new File(ruta))) {
            while (lector.hasNextLine()) { // Revisa si hay una línea
                partes = lector.nextLine().split(","); // Obtengo las partes del archivo

                // tener en cuenta que en el futuro podrían ser más campos
                // TODO: Adaptar si se agregan más columnas

                repetido = encontrarRepetido(partes[0], hechos); // Buscamos repetido

                if (repetido != null) {
                    reemplazarHechoRepetido(repetido, partes);
                } else {
                    hecho = crearHecho(partes);
                    hechos.add(hecho);
                }

            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: No se pudo encontrar el archivo - " + e.getMessage());
        }

        return hechos;
    }

    private Hecho encontrarRepetido(String titulo, List<Hecho> hechos) {
        if (hechos == null) {
            return null;
        }

        return hechos.stream()
                .filter(h -> h.getTitulo().equals(titulo))
                .findFirst()
                .orElse(null);
    }

    private void reemplazarHechoRepetido(Hecho hecho, String[] datos) {
        hecho.cambiarDescripcion(datos[1]);
        hecho.cambiarCategoria(datos[2]);
        hecho.cambiarUbicacion(datos[3], datos[4]);
        hecho.cambiarFecha(datos[5]);
    }

    private Hecho crearHecho(String[] datos) {
        return new Hecho(datos[0], datos[1], datos[2], datos[3], datos[4], datos[5]);
    }
}
