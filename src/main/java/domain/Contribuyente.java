package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getApellido() {return apellido;}
    public void setApellido(String apellido) {this.apellido = apellido;}

    public int getEdad() {return edad;}
    public void setEdad(int edad) {this.edad = edad;}

    public Boolean esAnonimo() {return anonimo;}
    public void cambiarAnonimato(Boolean anonimo) {this.anonimo = anonimo;}

}
