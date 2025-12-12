package com.metamapa.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="contribuyente")
public class Contribuyente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID externo proveniente de Keycloak.
     * Puede ser null inicialmente, pero una vez asignado identifica unívocamente al contribuyente.
     */
    @Column(unique = true)
    private String keycloakId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private Integer edad;  // Nullable para contribuyentes anónimos

    public Contribuyente(String nombre, String apellido, Integer edad) {  // Cambio de int a Integer
        if (edad != null && edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa.");
        }
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
    }
}
