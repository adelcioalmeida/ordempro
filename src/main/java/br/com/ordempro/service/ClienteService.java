package br.com.ordempro.service;

import br.com.ordempro.model.Cliente;
import br.com.ordempro.repository.ClienteRepository;
import br.com.ordempro.repository.OrdemServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            OrdemServicoRepository ordemServicoRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente salvar(Cliente cliente) {
        if (cliente.getCep() != null) {
            cliente.setCep(cliente.getCep().replaceAll("\\D", ""));
        }

        if (cliente.getCpf() != null) {
            cliente.setCpf(cliente.getCpf().replaceAll("\\D", ""));
        }

        if (cliente.getTelefone() != null) {
            cliente.setTelefone(cliente.getTelefone().replaceAll("\\D", ""));
        }

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public List<Cliente> buscarComFiltro(String filtro) {
        return clienteRepository.buscarComFiltro(filtro);
    }

    public void excluirPorId(Long id) {
        if (ordemServicoRepository.existsByCliente_IdCliente(id)) {
            throw new IllegalStateException("Não é possível excluir este cliente, pois ele possui ordem de serviço vinculada.");
        }

        clienteRepository.deleteById(id);
    }

    public boolean existeClienteNaCidade(Long idCidade) {
        return clienteRepository.existsByCidade_IdCidade(idCidade);
    }
}