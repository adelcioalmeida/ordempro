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

        return clienteRepository.buscarComFiltroAtivos(
                filtroTexto,
                filtroNumerico
        );
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
            throw new IllegalStateException("Selecione uma cidade válida.");
        }

        String cpf = limpar(cliente.getCpf());

        if (cpf.isBlank()) {
            throw new IllegalStateException("Informe o CPF.");
        }

        if (cpf.length() != 11) {
            throw new IllegalStateException("CPF inválido. Informe os 11 dígitos.");
        }

        if (cliente.getIdCliente() == null) {

            if (clienteRepository.existsByCpfAndAtivoTrue(cpf)) {
                throw new IllegalStateException("Já existe um cliente cadastrado com este CPF.");
            }

        } else {

            if (clienteRepository.existsByCpfAndAtivoTrueAndIdClienteNot(
                    cpf,
                    cliente.getIdCliente()
            )) {
                throw new IllegalStateException("Já existe um cliente cadastrado com este CPF.");
            }
        }

        String email = cliente.getEmail() == null
                ? ""
                : cliente.getEmail().trim();

        if (email.isBlank()) {
            throw new IllegalStateException("Informe o e-mail.");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalStateException("E-mail inválido.");
        }

        if (cliente.getIdCliente() == null) {

            if (clienteRepository.existsByEmailIgnoreCaseAndAtivoTrue(email)) {
                throw new IllegalStateException("Já existe um cliente cadastrado com este e-mail.");
            }

        } else {

            if (clienteRepository.existsByEmailIgnoreCaseAndAtivoTrueAndIdClienteNot(
                    email,
                    cliente.getIdCliente()
            )) {
                throw new IllegalStateException("Já existe um cliente cadastrado com este e-mail.");
            }
        }

        String telefone = limpar(cliente.getTelefone());
        String celular = limpar(cliente.getCelular());

        if (telefone.isBlank() && celular.isBlank()) {
            throw new IllegalStateException(
                    "Informe pelo menos um telefone ou celular para contato."
            );
        }

        if (!telefone.isBlank() && telefone.length() != 10) {
            throw new IllegalStateException(
                    "Telefone inválido. Informe DDD + número completo."
            );
        }

        if (!celular.isBlank() && celular.length() != 11) {
            throw new IllegalStateException(
                    "Celular inválido. Informe DDD + número completo."
            );
        }

        String cep = limpar(cliente.getCep());

        if (!cep.isBlank() && cep.length() != 8) {
            throw new IllegalStateException(
                    "CEP inválido."
            );
        }
    }

    private String limpar(String valor) {

        if (valor == null) {
            return "";
        }

        return valor.replaceAll("\\D", "");
    }
}