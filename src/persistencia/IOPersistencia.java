package persistencia;

import controlador.ControladorSistema;
import excepcion.BancoException;

import java.io.*;
import java.util.ArrayList;

public class IOPersistencia implements Serializable {

    private static IOPersistencia instance = new IOPersistencia();
    public static IOPersistencia getInstance() {
        return instance;
    }

    public void saveControladores(Object controladores) throws BancoException {
        ObjectOutputStream objetoArch = null;
        try {
            objetoArch = new ObjectOutputStream(new FileOutputStream("SVPObjetos.obj"));
            objetoArch.writeObject(controladores);
        } catch (IOException e) {
            throw new BancoException("No se puede abrir o crear el archivo SVPObjetos.obj");
        } finally {
            if (objetoArch != null) {
                try {
                    objetoArch.close();
                } catch (IOException e) {
                    throw new BancoException("No se puede grabar en el archivo SVPObjetos.obj");
                }
            }
        }
    }


    public ControladorSistema readControladores() throws BancoException {
        ObjectInputStream objetoArch = null;
        ArrayList<ControladorSistema> objetosLeidos = new ArrayList<ControladorSistema>();
        try {
            objetoArch = new ObjectInputStream(new FileInputStream("SVPObjetos.obj"));
            ControladorSistema objeto;
            while (true) {
                try {
                    objeto = (ControladorSistema) objetoArch.readObject();
                    objetosLeidos.add(objeto);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new BancoException("No existe o no se puede abrir el archivo SVPObjetos.obj");
        } catch (ClassNotFoundException e) {
            throw new BancoException("No se puede leer el archivo SVPObjetos.obj");
        } finally {
            if (objetoArch != null) {
                try {
                    objetoArch.close();
                } catch (IOException e) {
                    throw new BancoException("No se puede leer el archivo SVPObjetos.obj");
                }
            }
        }
        System.out.println("Cantidad de objetos leidos: " + objetosLeidos.size());
        return objetosLeidos.getFirst();
    }
}
