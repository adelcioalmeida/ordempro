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

    @Override
    @EntityGraph(attributePaths = "cidade")
    List<Cliente> findAll();

    @EntityGraph(attributePaths = "cidade")
    @Query("""
            SELECT c
            FROM Cliente c
            LEFT JOIN c.cidade cidade
            WHERE c.ativo = true
              AND (:filtro IS NULL OR :filtro = ''
                OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
                OR LOWER(c.cpf) LIKE LOWER(CONCAT('%', :filtro, '%'))
                OR LOWER(cidade.nome) LIKE LOWER(CONCAT('%', :filtro, '%')))
            ORDER BY c.idCliente DESC
            """)
    List<Cliente> buscarComFiltro(@Param("filtro") String filtro);

    boolean existsByCidade_IdCidade(Long idCidade);
}
