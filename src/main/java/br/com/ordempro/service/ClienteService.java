package br.com.ordempro.service;

import br.com.ordempro.model.Cliente;
import br.com.ordempro.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(
            ClienteRepository clienteRepository
    ) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente salvar(Cliente cliente) {
        if (cliente.getAtivo() == null) {
            cliente.setAtivo(true);
        }

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
        Cliente cliente = buscarPorId(id);

        if (cliente == null) {
            throw new IllegalStateException("Cliente não encontrado.");
        }

        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    public boolean existeClienteNaCidade(Long idCidade) {
        return clienteRepository.existsByCidade_IdCidade(idCidade);
    }
}
