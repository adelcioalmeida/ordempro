package br.com.ordempro.repository;

import br.com.ordempro.model.OrdemServico;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    @EntityGraph(attributePaths = {"cliente", "cliente.cidade"})
    List<OrdemServico> findAllByOrderByIdOsDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "cliente.cidade"})
    @Query("""
            SELECT DISTINCT os
            FROM OrdemServico os
            LEFT JOIN os.cliente c
            LEFT JOIN ItemOrdemServico ios ON ios.ordemServico = os
            LEFT JOIN ios.servico s
            WHERE (:status IS NULL OR LOWER(os.status) LIKE LOWER(CONCAT('%', :status, '%')))
              AND (:cliente IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :cliente, '%')))
              AND (:servico IS NULL OR LOWER(s.nome) LIKE LOWER(CONCAT('%', :servico, '%')))
            ORDER BY os.idOs DESC
            """)
    List<OrdemServico> buscarComFiltros(
            @Param("status") String status,
            @Param("cliente") String cliente,
            @Param("servico") String servico
    );

    @EntityGraph(attributePaths = {"cliente", "cliente.cidade", "usuario"})
    Optional<OrdemServico> findWithClienteByIdOs(Long idOs);

    boolean existsByCliente_IdCliente(Long idCliente);

    long countByStatusIgnoreCase(String status);
}