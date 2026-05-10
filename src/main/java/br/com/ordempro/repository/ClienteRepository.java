package br.com.ordempro.repository;

import br.com.ordempro.model.Cliente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Override
    @EntityGraph(attributePaths = "cidade")
    Optional<Cliente> findById(Long id);

    @EntityGraph(attributePaths = "cidade")
    List<Cliente> findAllByAtivoTrue();

    @EntityGraph(attributePaths = "cidade")
    List<Cliente> findTop5ByAtivoTrueOrderByIdClienteDesc();

    @EntityGraph(attributePaths = "cidade")
    @Query("""
            SELECT c
            FROM Cliente c
            LEFT JOIN c.cidade cidade
            WHERE c.ativo = true
              AND (
                    :filtroTexto IS NULL
                    OR :filtroTexto = ''
                    OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :filtroTexto, '%'))
                    OR LOWER(c.email) LIKE LOWER(CONCAT('%', :filtroTexto, '%'))
                    OR LOWER(cidade.nome) LIKE LOWER(CONCAT('%', :filtroTexto, '%'))
                    OR (:filtroNumerico IS NOT NULL AND :filtroNumerico <> '' AND c.cpf LIKE CONCAT('%', :filtroNumerico, '%'))
                    OR (:filtroNumerico IS NOT NULL AND :filtroNumerico <> '' AND c.telefone LIKE CONCAT('%', :filtroNumerico, '%'))
                    OR (:filtroNumerico IS NOT NULL AND :filtroNumerico <> '' AND c.celular LIKE CONCAT('%', :filtroNumerico, '%'))
              )
            ORDER BY c.idCliente DESC
            """)
    List<Cliente> buscarComFiltroAtivos(
            @Param("filtroTexto") String filtroTexto,
            @Param("filtroNumerico") String filtroNumerico
    );

    boolean existsByCidade_IdCidade(Long idCidade);
}