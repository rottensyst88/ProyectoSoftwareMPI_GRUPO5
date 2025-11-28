package controlador;

import modelo.Cliente;
import excepcion.BancoException;

import java.util.ArrayList;
import java.util.Scanner;

public class ControladorSistema {

    private static ControladorSistema instancia;

    private ControladorSistema() {}

    public static ControladorSistema getInstancia() {
        if (instancia == null) {
            instancia = new ControladorSistema();
        }
        return instancia;
    }

    // Se inician metodos del sistema!
    ArrayList<Cliente> clientes = new ArrayList<>();

    public void crearCliente(String nombre, String rut, String domicilio) throws BancoException {

        if(buscarCliente(rut)){
            throw new BancoException("Cliente ya existe");
        }

        Cliente cliente = new Cliente(nombre, rut, domicilio);
        clientes.add(cliente);

    }

    public void firmarContratoCliente(String rutCliente){
        return;
    }

    public String[][] listarClientes(){
        String[][] datosClientes = new String[clientes.size()][3];

        for(int i = 0; i < clientes.size(); i++){
            datosClientes[i][0] = clientes.get(i).getNombreCompleto();
            datosClientes[i][1] = clientes.get(i).getRut();
            datosClientes[i][2] = clientes.get(i).getDomicilio();
        }

        return datosClientes;
    }

    // UTILIDADES

    private boolean buscarCliente(String rut){
        for(Cliente c : clientes){
            if(c.getRut().equals(rut)){
                return true;
            }
        }
        return false;
    }

}