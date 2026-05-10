package br.com.ordempro.controller;

import br.com.ordempro.model.Cidade;
import br.com.ordempro.model.Cliente;
import br.com.ordempro.service.CidadeService;
import br.com.ordempro.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class ClienteController {

    private final ClienteService clienteService;
    private final CidadeService cidadeService;

    public ClienteController(
            ClienteService clienteService,
            CidadeService cidadeService
    ) {
        this.clienteService = clienteService;
        this.cidadeService = cidadeService;
    }

    @GetMapping("/clientes")
    public String listarClientes(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String origem,
            Model model
    ) {
        model.addAttribute("clientes", clienteService.buscarComFiltro(filtro));
        model.addAttribute("filtroSelecionado", filtro);
        model.addAttribute("origem", origem);

        return "cliente-lista";
    }

    @GetMapping("/clientes/novo")
    public String novoCliente(
            @RequestParam(required = false) String origem,
            Model model
    ) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("cidades", cidadeService.listarTodas());
        model.addAttribute("origem", origem);

        return "cliente-form";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(
            @PathVariable Long id,
            @RequestParam(required = false) String origem,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Cliente cliente = clienteService.buscarPorId(id);

        if (cliente == null) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("cidades", cidadeService.listarTodas());
        model.addAttribute("origem", origem);

        return "cliente-form";
    }

    @PostMapping("/clientes/salvar")
    public String salvarCliente(
            @ModelAttribute Cliente cliente,
            @RequestParam(required = false) String origem,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            clienteService.salvar(cliente);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente salvo com sucesso.");

            if ("ordem".equalsIgnoreCase(origem)) {
                return "redirect:/clientes?origem=ordem";
            }

            return "redirect:/clientes";

        } catch (IllegalStateException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("cliente", cliente);
            model.addAttribute("cidades", cidadeService.listarTodas());
            model.addAttribute("origem", origem);

            return "cliente-form";
        }
    }

    @PostMapping("/clientes/salvar-rapido")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> salvarClienteRapido(@ModelAttribute Cliente cliente) {
        try {
            if (cliente.getNome() == null || cliente.getNome().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(criarRespostaErro("Informe o nome do cliente."));
            }

            if (cliente.getCidade() == null || cliente.getCidade().getIdCidade() == null) {
                return ResponseEntity.badRequest()
                        .body(criarRespostaErro("Selecione uma cidade para o cliente."));
            }

            Cidade cidadeSelecionada = cidadeService.buscarPorId(cliente.getCidade().getIdCidade());

            if (cidadeSelecionada == null) {
                return ResponseEntity.badRequest()
                        .body(criarRespostaErro("Cidade selecionada não encontrada."));
            }

            cliente.setCidade(cidadeSelecionada);

            Cliente clienteSalvo = clienteService.salvar(cliente);

            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Cliente cadastrado com sucesso.");
            resposta.put("idCliente", clienteSalvo.getIdCliente());
            resposta.put("nome", clienteSalvo.getNome());
            resposta.put("cpf", clienteSalvo.getCpf());
            resposta.put("telefone", clienteSalvo.getTelefone());
            resposta.put("celular", clienteSalvo.getCelular());
            resposta.put("email", clienteSalvo.getEmail());
            resposta.put("cidade", cidadeSelecionada.getNome());
            resposta.put("uf", cidadeSelecionada.getUf());

            return ResponseEntity.ok(resposta);

        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body(criarRespostaErro("Erro ao salvar cliente: " + exception.getMessage()));
        }
    }

    @GetMapping("/clientes/excluir/{id}")
    public String excluirCliente(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            clienteService.excluirPorId(id);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente inativado com sucesso.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/clientes";
    }

    private Map<String, Object> criarRespostaErro(String mensagem) {
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("sucesso", false);
        resposta.put("mensagem", mensagem);

        return resposta;
    }
}