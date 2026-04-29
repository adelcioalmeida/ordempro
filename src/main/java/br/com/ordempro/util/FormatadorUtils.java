package br.com.ordempro.util;

public class FormatadorUtils {

    private FormatadorUtils() {
    }

    public static String formatarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return "";
        }

        String numeros = cpf.replaceAll("\\D", "");

        if (numeros.length() != 11) {
            return cpf;
        }

        return numeros.substring(0, 3) + "." +
                numeros.substring(3, 6) + "." +
                numeros.substring(6, 9) + "-" +
                numeros.substring(9, 11);
    }

    public static String formatarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return "";
        }

        String numeros = cep.replaceAll("\\D", "");

        if (numeros.length() != 8) {
            return cep;
        }

        return numeros.substring(0, 5) + "-" +
                numeros.substring(5, 8);
    }

    public static String formatarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return "";
        }

        String numeros = telefone.replaceAll("\\D", "");

        if (numeros.length() == 10) {
            return "(" + numeros.substring(0, 2) + ") " +
                    numeros.substring(2, 6) + "-" +
                    numeros.substring(6, 10);
        }

        if (numeros.length() == 11) {
            return "(" + numeros.substring(0, 2) + ") " +
                    numeros.substring(2, 7) + "-" +
                    numeros.substring(7, 11);
        }

        return telefone;
    }
}