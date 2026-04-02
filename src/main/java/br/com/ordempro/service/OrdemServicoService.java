package br.com.ordempro.service;

import br.com.ordempro.model.OrdemServico;
import br.com.ordempro.repository.OrdemServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    public List<OrdemServico> listarTodas() {
        return ordemServicoRepository.findAll();
    }

    public OrdemServico salvar(OrdemServico ordemServico) {
        return ordemServicoRepository.save(ordemServico);
    }

    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id).orElse(null);
    }
}