package br.com.ordempro.controller;

import br.com.ordempro.model.Cliente;
import br.com.ordempro.service.CidadeService;
import br.com.ordempro.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            RedirectAttributes redirectAttributes
    ) {
        clienteService.salvar(cliente);
        redirectAttributes.addFlashAttribute("sucesso", "Cliente salvo com sucesso.");

        if ("ordem".equalsIgnoreCase(origem)) {
            return "redirect:/clientes?origem=ordem";
        }

        return "redirect:/clientes";
    }

    @GetMapping("/clientes/excluir/{id}")
    public String excluirCliente(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            clienteService.excluirPorId(id);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente excluído com sucesso.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/clientes";
    }
}