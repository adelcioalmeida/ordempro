package br.com.ordempro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordens_servico")
@Getter
@Setter
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "data_abertura")
    private LocalDateTime dataAbertura;

    @Column(name = "data_prevista_conclusao")
    private LocalDateTime dataPrevistaConclusao;

    private String status;
    private String observacao;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "email_enviado")
    private Boolean emailEnviado;

    @Column(name = "data_envio_email")
    private LocalDateTime dataEnvioEmail;
}