package vista;

import java.util.Scanner;

public class Vista {

    private static Vista vista;
    private Scanner sc = new Scanner(System.in);

    private Vista(){}

    public static Vista getInstancia(){
        if(vista == null){
            vista = new Vista();
        }
        return vista;
    }

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
                    System.out.println("Listar datos");
                    break;
                case 99:
                    System.exit(0);
                default:
                    System.out.println("Opcion no valida");
            }
        }while(true);
    }
}
