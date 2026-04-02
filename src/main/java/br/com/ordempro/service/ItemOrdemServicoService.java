package br.com.ordempro.service;

import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemOrdemServicoService {

    private final ItemOrdemServicoRepository itemOrdemServicoRepository;

    public ItemOrdemServicoService(ItemOrdemServicoRepository itemOrdemServicoRepository) {
        this.itemOrdemServicoRepository = itemOrdemServicoRepository;
    }

    public ItemOrdemServico salvar(ItemOrdemServico item) {
        return itemOrdemServicoRepository.save(item);
    }
}