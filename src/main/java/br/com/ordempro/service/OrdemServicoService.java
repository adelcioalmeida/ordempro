package br.com.ordempro.service;

import br.com.ordempro.model.ItemOrdemServico;
import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import br.com.ordempro.repository.OrdemServicoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrdemServicoService {

    private static final String STATUS_CANCELADA = "CANCELADA";
    private static final String STATUS_ABERTA = "ABERTA";
    private static final String STATUS_EM_ANDAMENTO = "EM_ANDAMENTO";

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
        return ordemServicoRepository.findByStatusInOrderByIdOsDesc(
                List.of(STATUS_ABERTA, STATUS_EM_ANDAMENTO),
                PageRequest.of(0, 5)
        );
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> buscarComFiltros(
            String status,
            Long idServico,
            LocalDate dataInicial,
            LocalDate dataFinal,
            String cliente
    ) {
        return ordemServicoRepository.buscarComFiltros(
                normalizarFiltro(status),
                idServico,
                converterDataInicial(dataInicial),
                converterDataFinal(dataFinal),
                normalizarFiltro(cliente)
        );
    }

    @Transactional(readOnly = true)
    public long contarOrdensAbertas() {
        return ordemServicoRepository.countByStatusIgnoreCase(STATUS_ABERTA);
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

    public void cancelarPorId(Long id) {
        OrdemServico ordemServico = buscarPorId(id);

        if (ordemServico == null) {
            return;
        }

        ordemServico.setStatus(STATUS_CANCELADA);
        ordemServicoRepository.save(ordemServico);
    }

    public void excluirPorId(Long id) {
        cancelarPorId(id);
    }

    @Transactional(readOnly = true)
    public String buscarDescricaoServico(Long idOs) {
        ItemOrdemServico item = itemRepository
                .findFirstByOrdemServico_IdOs(idOs)
                .orElse(null);

        if (item == null) {
            return "NÃO INFORMADO";
        }

        if (item.getServico() != null && item.getServico().getNome() != null && !item.getServico().getNome().isBlank()) {
            return item.getServico().getNome();
        }

        if (item.getDescricao() != null && !item.getDescricao().isBlank()) {
            return item.getDescricao();
        }

        return "NÃO INFORMADO";
    }

    private String normalizarFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private LocalDateTime converterDataInicial(LocalDate dataInicial) {
        if (dataInicial == null) {
            return null;
        }

        return dataInicial.atStartOfDay();
    }

    private LocalDateTime converterDataFinal(LocalDate dataFinal) {
        if (dataFinal == null) {
            return null;
        }

        return dataFinal.atTime(23, 59, 59);
    }
}