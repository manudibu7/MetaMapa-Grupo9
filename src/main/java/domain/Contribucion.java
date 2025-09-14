package domain;
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
public class Contribucion {
    private Contribuyente contribuyente;
    private Hecho hecho;
    private Revision revision= new Revision();
    private Long id;
    private Boolean exportada;



    //metodos que figuran en el diagrama
    public void editarHecho(){
        //para que ingrese un dato del hecho
        Scanner obj = new Scanner(System.in);
        hecho.setTitulo(obj.nextLine());
        hecho.setDescripcion(obj.nextLine());
        hecho.setCategoria(new Categoria(obj.nextLine()));
        hecho.setEtiqueta(new Etiqueta(obj.nextLine()));
        hecho.setFecha(LocalDate.parse(obj.nextLine()));
        hecho.setLugarDeOcurrencia(new Ubicacion(Float.parseFloat(obj.nextLine()), Float.parseFloat(obj.nextLine())));
    }
}
