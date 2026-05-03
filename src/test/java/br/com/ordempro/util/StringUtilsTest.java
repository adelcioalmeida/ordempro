package br.com.ordempro.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringUtilsTest {
    @Test
    void deveNormalizarRemovendoAcentosEspacosELetrasMinusculas() { assertEquals("JOAO DA SILVA", StringUtils.normalizar("  João da Silva  ")); }
    @Test
    void deveRetornarNuloQuandoTextoForNulo() { assertNull(StringUtils.normalizar(null)); }
}
