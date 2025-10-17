package com.metamapa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "archivo_id")
    private Archivo adjunto;
}
