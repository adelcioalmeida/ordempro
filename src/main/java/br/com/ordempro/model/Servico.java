package br.com.ordempro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "servicos")
@Getter
@Setter
public class Servico {

    public static final String STATUS_ATIVO = "ATIVO";
    public static final String STATUS_INATIVO = "INATIVO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServico;

    private String nome;

    private String status = STATUS_ATIVO;

    @PrePersist
    public void prePersist() {
        if (status == null || status.isBlank()) {
            status = STATUS_ATIVO;
        }
    }

    public boolean estaAtivo() {
        return STATUS_ATIVO.equalsIgnoreCase(status);
    }
}