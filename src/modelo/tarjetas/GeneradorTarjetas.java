package modelo.tarjetas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GeneradorTarjetas {

    public static class DatosTarjeta {
        private final String numero;
        private final String fechaExpiracion;
        private final String cvv;

        public DatosTarjeta(String numero, String fechaExpiracion, String cvv) {
            this.numero = numero;
            this.fechaExpiracion = fechaExpiracion;
            this.cvv = cvv;
        }

        public String getNumero() { return numero; }
        public String getFechaExpiracion() { return fechaExpiracion; }
        public String getCvv() { return cvv; }
    }

    /**
     * Genera un objeto con datos de tarjeta válidos (Mock).
     * @param bin Prefijo de la tarjeta (ej: "4" para Visa, "5" para Mastercard).
     * @param longitud Longitud total del número de tarjeta (ej: 16).
     * @return Objeto DatosTarjeta con numero, fecha y cvv.
     */

}