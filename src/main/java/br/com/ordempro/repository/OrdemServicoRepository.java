package br.com.ordempro.repository;

import br.com.ordempro.model.OrdemServico;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    @EntityGraph(attributePaths = {"cliente", "cliente.cidade"})
    List<OrdemServico> findAllByOrderByIdOsDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "cliente.cidade"})
    List<OrdemServico> findByStatusInOrderByIdOsDesc(List<String> status, Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "cliente.cidade"})
    @Query("""
            SELECT DISTINCT os
            FROM OrdemServico os
            LEFT JOIN os.cliente c
            LEFT JOIN ItemOrdemServico ios ON ios.ordemServico = os
            LEFT JOIN ios.servico s
            WHERE (:status IS NULL OR UPPER(os.status) = UPPER(:status))
              AND (:idServico IS NULL OR s.idServico = :idServico)
              AND (:cliente IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :cliente, '%')))
              AND (:dataInicial IS NULL OR os.dataAbertura >= :dataInicial)
              AND (:dataFinal IS NULL OR os.dataAbertura <= :dataFinal)
            ORDER BY os.idOs DESC
            """)
    List<OrdemServico> buscarComFiltros(
            @Param("status") String status,
            @Param("idServico") Long idServico,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal,
            @Param("cliente") String cliente
    );

    @EntityGraph(attributePaths = {"cliente", "cliente.cidade", "usuario"})
    Optional<OrdemServico> findWithClienteByIdOs(Long idOs);

    boolean existsByCliente_IdCliente(Long idCliente);

    long countByStatusIgnoreCase(String status);
}