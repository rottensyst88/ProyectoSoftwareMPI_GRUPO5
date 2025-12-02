package vista;

import controlador.ControladorSistema;
import excepcion.BancoException;

import java.util.Scanner;

public class Vista {

    // Patron Singleton
    private static Vista vista;

    private Vista(){}

    public static Vista getInstancia(){
        if(vista == null){
            vista = new Vista();
        }
        return vista;
    }

    // Variables
    private Scanner sc = new Scanner(System.in);

    public void menu(){
        do{
            System.out.println("""
                ....:: MENU ::....
                1. Crear cliente
                2. Generar contrato para cliente
                3. Crear cuenta de cliente
                4. Listar datos
                99. Salir
                """);

            System.out.print("OPCION? ");

            int num = sc.nextInt();
            sc.nextLine();

            switch(num){
                case 1:
                    crearCliente();
                    break;
                case 2:
                    System.out.println("Generar contrato para cliente");
                    break;
                case 3:
                    System.out.println("Crear cuenta de cliente");
                    break;
                case 4:
                    listarClientes();
                    break;
                case 99:
                    System.exit(0);
                default:
                    System.out.println("Opcion no valida");
            }
        }while(true);
    }

    private void crearCliente(){
        System.out.println("....:: Crear cliente ::....");

        try{
            String nombreCompleto = ingresarDatos("Nombre completo");
            String rut = ingresarDatos("RUT");
            String direccion = ingresarDatos("Direccion");

            ControladorSistema.getInstancia().crearCliente(nombreCompleto, rut, direccion);
        } catch (BancoException e) {
            System.out.println(e.getMessage());
        }

    }

    private void listarClientes(){
        String[][] datosClientes = ControladorSistema.getInstancia().listarClientes();

        System.out.println("....:: LISTA DE CLIENTES ::....");
        for(int i = 0; i < datosClientes.length; i++){
            System.out.println("Nombre: " + datosClientes[i][0]);
            System.out.println("RUT: " + datosClientes[i][1]);
            System.out.println("Domicilio: " + datosClientes[i][2]);
            System.out.println("-------------------------");
        }
    }

    private String ingresarDatos(String x){
        System.out.print(x + "? ");
        return sc.nextLine();
    }
}
