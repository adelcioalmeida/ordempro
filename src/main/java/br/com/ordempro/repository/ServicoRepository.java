package br.com.ordempro.repository;

import br.com.ordempro.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findTop5ByStatusOrderByIdServicoDesc(String status);

    List<Servico> findByStatusOrderByNomeAsc(String status);

    List<Servico> findByStatusAndNomeContainingIgnoreCaseOrderByNomeAsc(
            String status,
            String nome
    );

    List<Servico> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Servico> findAllByOrderByIdServicoDesc();
}