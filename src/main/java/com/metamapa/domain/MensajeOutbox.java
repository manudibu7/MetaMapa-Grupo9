package com.metamapa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@NoArgsConstructor
public class MensajeOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String hechoJson;

    // Constructor personalizado
    public MensajeOutbox(String hechoJson) {
        this.hechoJson = hechoJson;
    }
}
