package br.com.ordempro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CepResponse {

    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
}