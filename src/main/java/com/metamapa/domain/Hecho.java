package com.metamapa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "hecho")
public class Hecho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String Titulo;

    @Column(length=1000)
    private String descripcion;

    @Embedded
    private Categoria categoria;

    @Column(nullable = false)
    private LocalDate fecha;

    @Embedded
    private Ubicacion lugarDeOcurrencia;
    private String origen;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "hecho")
    private List<Archivo> adjuntos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_hecho", nullable = false)
    private TipoDeHecho tipoDeHecho = TipoDeHecho.TEXTO;

    // Método para agregar un adjunto y cambiar el tipo a MULTIMEDIA
    public void agregarAdjunto(Archivo archivo) {
        if (archivo != null) {
            adjuntos.add(archivo);
            archivo.setHecho(this);
            this.tipoDeHecho = TipoDeHecho.MULTIMEDIA;
        }
    }

    // Método para establecer los adjuntos y actualizar el tipo
    public void setAdjuntos(List<Archivo> adjuntos) {
        this.adjuntos = adjuntos != null ? adjuntos : new ArrayList<>();
        if (this.adjuntos.isEmpty()) {
            this.tipoDeHecho = TipoDeHecho.TEXTO;
        } else {
            this.tipoDeHecho = TipoDeHecho.MULTIMEDIA;
            // Asegurar la relación bidireccional
            for (Archivo archivo : this.adjuntos) {
                archivo.setHecho(this);
            }
        }
    }
}
