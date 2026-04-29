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

@Entity
@Table(name = "itens_ordem_servico")
@Getter
@Setter
public class ItemOrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_os", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servico", nullable = true)
    private Servico servico;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
}