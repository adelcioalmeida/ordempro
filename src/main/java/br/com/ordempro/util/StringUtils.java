package br.com.ordempro.util;

import java.text.Normalizer;

public class StringUtils {

    private StringUtils() {
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return null;
        }

        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);

        return textoNormalizado
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .trim()
                .toUpperCase();
    }
}