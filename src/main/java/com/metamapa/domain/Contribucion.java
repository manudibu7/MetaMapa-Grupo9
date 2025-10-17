package com.metamapa.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="contribucion")
public class Contribucion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //solo me cargas al padre cuando lo necesito
    @JoinColumn(name="contribuyente_id", nullable=false)
    private Contribuyente contribuyente;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)//si elimino la contribucion, elimino el hecho
    @JoinColumn(name="hecho_id", nullable=false)
    private Hecho hecho;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) //si elimino la contribucion, elimino la revision
    @JoinColumn(name = "revision_id")
    private Revision revision = new Revision();  // Inicializar automáticamente

    @Column(name="exportada", nullable=false)
    private Boolean exportada = false;  // Valor por defecto

    @Column(name="fecha_de_carga", nullable=false)
    private LocalDate fechaDeCarga;



    //metodos que figuran en el diagrama
    public void editarHecho(){
        //para que ingrese un dato del hecho
        Scanner obj = new Scanner(System.in);
        hecho.setTitulo(obj.nextLine());
        hecho.setDescripcion(obj.nextLine());
        hecho.setCategoria(new Categoria(obj.nextLine()));
        hecho.setFecha(LocalDate.parse(obj.nextLine()));
        hecho.setLugarDeOcurrencia(new Ubicacion(Float.parseFloat(obj.nextLine()), Float.parseFloat(obj.nextLine())));
    }
}
