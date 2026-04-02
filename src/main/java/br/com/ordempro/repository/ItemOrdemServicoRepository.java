package br.com.ordempro.repository;

import br.com.ordempro.model.ItemOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemOrdemServicoRepository extends JpaRepository<ItemOrdemServico, Long> {

    List<ItemOrdemServico> findByOrdemServico_IdOs(Long idOs);
}