package controlador;

import modelo.Cliente;

import java.util.ArrayList;
import java.util.Scanner;

public class ControladorSistema {

    private static ControladorSistema instancia;
    private static Scanner sc = new Scanner(System.in);

    // Contenedor para otras clases utilizadas
    ArrayList<Cliente> clientes = new ArrayList<>();

    private ControladorSistema() {}

    public static ControladorSistema getInstancia() {
        if (instancia == null) {
            instancia = new ControladorSistema();
        }
        return instancia;
    }

    private void crearCliente(){
        System.out.println("....:: Crear cliente ::....");

        String nombreCompleto = ingresarDatos("Nombre completo");
        String rut = ingresarDatos("RUT");
        String direccion = ingresarDatos("Direccion");

        Cliente cliente = new Cliente(nombreCompleto, rut, direccion);
        clientes.add(cliente);




    }

    // UTILIDADES

    private String ingresarDatos(String x){
        System.out.print(x + "? ");
        return sc.nextLine();
    }

}