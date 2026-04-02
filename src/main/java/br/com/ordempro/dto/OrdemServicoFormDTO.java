package br.com.ordempro.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrdemServicoFormDTO {

    private Long idCliente;
    private Long idServico;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataAbertura;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataPrevistaConclusao;

    private String status;
    private String observacao;
    private String descricao;
    private BigDecimal valor;
}