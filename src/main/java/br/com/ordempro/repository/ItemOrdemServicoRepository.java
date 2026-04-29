package br.com.ordempro.repository;

import br.com.ordempro.model.ItemOrdemServico;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemOrdemServicoRepository extends JpaRepository<ItemOrdemServico, Long> {

    @EntityGraph(attributePaths = {"servico", "ordemServico"})
    Optional<ItemOrdemServico> findFirstByOrdemServico_IdOs(Long idOs);

    @EntityGraph(attributePaths = {"servico", "ordemServico"})
    List<ItemOrdemServico> findByOrdemServico_IdOs(Long idOs);

    void deleteByOrdemServico_IdOs(Long idOs);
}