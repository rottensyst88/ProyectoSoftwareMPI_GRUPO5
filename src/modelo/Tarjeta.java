package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Tarjeta implements Serializable {
    private String numeroTarjeta;
    private String titular;
    private String fechaExpiracion;
    private String cvv;

    public Tarjeta(String numeroTarjeta, String fechaExpiracion, String cvv) {
        this.numeroTarjeta = numeroTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.cvv = cvv;
        this.titular = "";
    }

    public static Tarjeta generar() {
        String numero = generarNumeroLuhn("4", 16);
        String fecha = generarFechaExpiracion();
        String cvv = generarCVV();

        return new Tarjeta(numero, fecha, cvv);
    }

    public String[] obtenerDatos() {
        return new String[] { numeroTarjeta, fechaExpiracion, cvv };
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public String getTitular() {
        return titular;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public String getCvv() {
        return cvv;
    }

    // --- Lógica Privada ---

    private static String generarNumeroLuhn(String bin, int length) {
        int randomLength = length - (bin.length() + 1);
        StringBuilder builder = new StringBuilder(bin);
        Random random = new Random();

        for (int i = 0; i < randomLength; i++) {
            builder.append(random.nextInt(10));
        }

        int checkDigit = calcularDigitoControl(builder.toString());
        builder.append(checkDigit);

        return builder.toString();
    }

    private static int calcularDigitoControl(String number) {
        int sum = 0;
        int length = number.length();
        boolean alternate = true;

        for (int i = length - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0) ? 0 : 10 - (sum % 10);
    }

    private static String generarFechaExpiracion() {
        Random random = new Random();
        LocalDate fechaFutura = LocalDate.now()
                .plusYears(random.nextInt(5) + 1)
                .plusMonths(random.nextInt(12));
        return fechaFutura.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private static String generarCVV() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900) + 100);
    }

}
