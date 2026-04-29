package br.com.ordempro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IbgeCidadeResponse {

    private String nome;
    private Microrregiao microrregiao;

    @Getter
    @Setter
    public static class Microrregiao {
        private Mesorregiao mesorregiao;
    }

    @Getter
    @Setter
    public static class Mesorregiao {

        @JsonProperty("UF")
        private UF uf;
    }

    @Getter
    @Setter
    public static class UF {
        private String sigla;
    }
}