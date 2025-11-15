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
@Table
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_media", nullable=false)
    TipoMedia tipo;

    @Column(nullable = false)
    String url;

    @Column
    String tamanio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hecho_id")
    private Hecho hecho;

    public void setTipoFromString(String tipo) {
        this.tipo = TipoMedia.valueOf(tipo.toUpperCase());
    }
}
