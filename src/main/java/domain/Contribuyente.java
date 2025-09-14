package domain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contribuyente {
    private Long id;
    private String nombre;
    private String apellido;
    private int edad;
    private Boolean anonimo;

    public Contribuyente(String nombre, String apellido, int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa.");
        }
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.anonimo = false;
    }
}
