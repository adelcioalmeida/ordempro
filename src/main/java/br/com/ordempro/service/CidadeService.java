package br.com.ordempro.service;

import br.com.ordempro.model.Cidade;
import br.com.ordempro.repository.CidadeRepository;
import br.com.ordempro.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CidadeService {

    private final CidadeRepository cidadeRepository;
    private final ClienteRepository clienteRepository;

    public CidadeService(
            CidadeRepository cidadeRepository,
            ClienteRepository clienteRepository
    ) {
        this.cidadeRepository = cidadeRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Cidade> buscarComFiltro(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String filtroLimpo = filtro.trim();

        return cidadeRepository.findByNomeContainingIgnoreCaseOrUfContainingIgnoreCase(
                filtroLimpo,
                filtroLimpo
        );
    }

    public List<Cidade> listarTodas() {
        return cidadeRepository.findAll();
    }

    public Cidade buscarPorId(Long id) {
        return cidadeRepository.findById(id).orElse(null);
    }

    public Cidade buscarCidadePorCep(String cep) {
        return null;
    }

    public Cidade salvar(Cidade cidade) {
        return cidadeRepository.save(cidade);
    }

    public void excluirPorId(Long id) {
        if (clienteRepository.existsByCidade_IdCidade(id)) {
            throw new IllegalStateException("Não é possível excluir esta cidade, pois existem clientes vinculados a ela.");
        }

        cidadeRepository.deleteById(id);
    }

    public void importarCidadesIBGE() {
        // mantém aqui o seu código atual de importação
    }
}