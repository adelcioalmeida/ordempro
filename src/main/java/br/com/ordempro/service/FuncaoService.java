package br.com.ordempro.service;

import br.com.ordempro.model.Funcao;
import br.com.ordempro.repository.FuncaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncaoService {

    private final FuncaoRepository funcaoRepository;

    public FuncaoService(FuncaoRepository funcaoRepository) {
        this.funcaoRepository = funcaoRepository;
    }

    public List<Funcao> listarTodas() {
        return funcaoRepository.findAll();
    }

    public Funcao buscarPorId(Long id) {
        return funcaoRepository.findById(id).orElse(null);
    }
}
