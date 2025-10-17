package com.metamapa.domain;


import java.time.LocalDate;

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
@Table(name="revision")
public class Revision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=500)
    private String mensaje = "El hecho está pendiente de revisión";

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private EstadoRevision estado = EstadoRevision.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY) //solo me cargas al padre cuando lo necesito
    @JoinColumn(name="responsable_id")
    private Contribuyente responsable;

    @Column(name="fecha")
    private LocalDate fecha;

    @OneToOne(mappedBy = "revision")
    private Contribucion contribucion;
}
