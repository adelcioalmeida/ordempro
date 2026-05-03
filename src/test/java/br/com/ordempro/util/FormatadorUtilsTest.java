package br.com.ordempro.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatadorUtilsTest {
    @Test
    void deveFormatarCpfComPontuacao() { assertEquals("123.456.789-01", FormatadorUtils.formatarCpf("12345678901")); }
    @Test
    void deveRetornarCpfOriginalQuandoInvalido() { assertEquals("12345", FormatadorUtils.formatarCpf("12345")); }
    @Test
    void deveFormatarCepComHifen() { assertEquals("01001-000", FormatadorUtils.formatarCep("01001000")); }
    @Test
    void deveFormatarTelefoneComDezDigitos() { assertEquals("(11) 3456-7890", FormatadorUtils.formatarTelefone("1134567890")); }
    @Test
    void deveFormatarTelefoneComOnzeDigitos() { assertEquals("(11) 93456-7890", FormatadorUtils.formatarTelefone("11934567890")); }
    @Test
    void deveRetornarVazioQuandoEntradaNulaOuEmBranco() {
        assertEquals("", FormatadorUtils.formatarCpf(null));
        assertEquals("", FormatadorUtils.formatarCep("  "));
        assertEquals("", FormatadorUtils.formatarTelefone(""));
    }
}
