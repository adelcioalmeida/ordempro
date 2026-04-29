package br.com.ordempro.service;

import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import br.com.ordempro.repository.OrdemServicoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ItemOrdemServicoRepository itemRepository;

    public OrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            ItemOrdemServicoRepository itemRepository
    ) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodas() {
        return ordemServicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> listarUltimas5() {
        return ordemServicoRepository.findAllByOrderByIdOsDesc(PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> buscarComFiltros(String status, String cliente, String servico) {
        return ordemServicoRepository.buscarComFiltros(
                normalizarFiltro(status),
                normalizarFiltro(cliente),
                normalizarFiltro(servico)
        );
    }

    public OrdemServico salvar(OrdemServico ordemServico) {
        return ordemServicoRepository.save(ordemServico);
    }

    @Transactional(readOnly = true)
    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public OrdemServico buscarComClientePorId(Long id) {
        return ordemServicoRepository.findWithClienteByIdOs(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return ordemServicoRepository.existsById(id);
    }

    public void excluirPorId(Long id) {
        ordemServicoRepository.deleteById(id);
    }

    private String normalizarFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    // 🔥 NOVO MÉTODO
    public String buscarDescricaoServico(Long idOs) {
        ItemOrdemServico item = itemRepository
                .findFirstByOrdemServico_IdOs(idOs)
                .orElse(null);

        if (item == null) return "NÃO INFORMADO";

        if (item.getDescricao() != null && !item.getDescricao().isBlank()) {
            return item.getDescricao();
        }

        if (item.getServico() != null) {
            return item.getServico().getNome();
        }

        return "NÃO INFORMADO";
    }
}