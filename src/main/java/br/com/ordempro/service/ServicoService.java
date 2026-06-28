package br.com.ordempro.service;

import br.com.ordempro.model.Servico;
import br.com.ordempro.repository.ItemOrdemServicoRepository;
import br.com.ordempro.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ItemOrdemServicoRepository itemOrdemServicoRepository;

    public ServicoService(
            ServicoRepository servicoRepository,
            ItemOrdemServicoRepository itemOrdemServicoRepository
    ) {
        this.servicoRepository = servicoRepository;
        this.itemOrdemServicoRepository = itemOrdemServicoRepository;
    }

    public List<Servico> listarTodos() {
        return servicoRepository.findByStatusOrderByNomeAsc(
                Servico.STATUS_ATIVO
        );
    }

    public List<Servico> listarAtivos() {
        return servicoRepository.findByStatusOrderByNomeAsc(
                Servico.STATUS_ATIVO
        );
    }

    public List<Servico> listarUltimos5Ativos() {
        return servicoRepository.findTop5ByStatusOrderByIdServicoDesc(
                Servico.STATUS_ATIVO
        );
    }

    public List<Servico> buscar(
            String termoBusca,
            String status
    ) {

        boolean semBusca = termoBusca == null || termoBusca.isBlank();
        boolean semStatus = status == null || status.isBlank();

        if (semBusca && semStatus) {
            return listarUltimos5Ativos();
        }

        if (!semBusca && !semStatus) {

            if ("TODOS".equalsIgnoreCase(status)) {
                return servicoRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(
                        termoBusca.trim()
                );
            }

            return servicoRepository
                    .findByStatusAndNomeContainingIgnoreCaseOrderByNomeAsc(
                            status,
                            termoBusca.trim()
                    );
        }

        if (semBusca) {

            if ("TODOS".equalsIgnoreCase(status)) {
                return servicoRepository.findAllByOrderByIdServicoDesc();
            }

            return servicoRepository.findByStatusOrderByNomeAsc(status);
        }

        return servicoRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(
                termoBusca.trim()
        );
    }

    public Servico salvar(Servico servico) {

        if (servico.getNome() != null) {
            servico.setNome(servico.getNome().trim());
        }

        if (servico.getStatus() == null || servico.getStatus().isBlank()) {
            servico.setStatus(Servico.STATUS_ATIVO);
        }

        return servicoRepository.save(servico);
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id).orElse(null);
    }

    public boolean podeInativar(Long id) {

        return !itemOrdemServicoRepository.existsByServico_IdServico(id);
    }

    public boolean inativar(Long id) {

        Servico servico = buscarPorId(id);

        if (servico == null) {
            return false;
        }

        if (!podeInativar(id)) {
            return false;
        }

        servico.setStatus(Servico.STATUS_INATIVO);

        servicoRepository.save(servico);

        return true;
    }

    public void ativar(Long id) {

        Servico servico = buscarPorId(id);

        if (servico == null) {
            return;
        }

        servico.setStatus(Servico.STATUS_ATIVO);

        servicoRepository.save(servico);
    }
}