package br.com.ordempro.repository;

import br.com.ordempro.model.ItemOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOrdemServicoRepository extends JpaRepository<ItemOrdemServico, Long> {
}