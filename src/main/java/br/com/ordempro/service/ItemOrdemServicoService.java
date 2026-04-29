package br.com.ordempro.service;

import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemOrdemServicoService {

    private final ItemOrdemServicoRepository itemOrdemServicoRepository;

    public ItemOrdemServicoService(ItemOrdemServicoRepository itemOrdemServicoRepository) {
        this.itemOrdemServicoRepository = itemOrdemServicoRepository;
    }

    public ItemOrdemServico salvar(ItemOrdemServico itemOrdemServico) {
        return itemOrdemServicoRepository.save(itemOrdemServico);
    }

    public ItemOrdemServico buscarPrimeiroPorIdOrdem(Long idOs) {
        return itemOrdemServicoRepository.findFirstByOrdemServico_IdOs(idOs).orElse(null);
    }

    public List<ItemOrdemServico> listarPorIdOrdem(Long idOs) {
        return itemOrdemServicoRepository.findByOrdemServico_IdOs(idOs);
    }

    @Transactional
    public void excluirPorIdOrdem(Long idOs) {
        itemOrdemServicoRepository.deleteByOrdemServico_IdOs(idOs);
    }
}