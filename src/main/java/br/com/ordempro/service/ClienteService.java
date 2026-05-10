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
        return clienteRepository.findAllByAtivoTrue();
    }

    public Cliente salvar(Cliente cliente) {
        validarCliente(cliente);

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

        if (cliente.getCelular() != null) {
            cliente.setCelular(cliente.getCelular().replaceAll("\\D", ""));
        }

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public List<Cliente> buscarComFiltro(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return clienteRepository.findTop5ByAtivoTrueOrderByIdClienteDesc();
        }

        String filtroTexto = filtro.trim();
        String filtroNumerico = filtroTexto.replaceAll("\\D", "");

        return clienteRepository.buscarComFiltroAtivos(filtroTexto, filtroNumerico);
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

    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalStateException("Dados do cliente não informados.");
        }

        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalStateException("Informe o nome do cliente.");
        }

        if (cliente.getCidade() == null || cliente.getCidade().getIdCidade() == null) {
            throw new IllegalStateException("Selecione uma cidade para o cliente.");
        }
    }
}