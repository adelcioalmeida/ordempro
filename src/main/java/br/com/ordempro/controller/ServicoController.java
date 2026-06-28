package br.com.ordempro.controller;

import br.com.ordempro.model.Servico;
import br.com.ordempro.service.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping("/servicos")
    public String listar(
            @RequestParam(required = false) String termoBusca,
            @RequestParam(required = false) String status,
            Model model
    ) {

        model.addAttribute(
                "servicos",
                servicoService.buscar(termoBusca, status)
        );

        model.addAttribute("termoBusca", termoBusca);
        model.addAttribute("statusSelecionado", status);

        return "servico-lista";
    }

    @GetMapping("/servicos/novo")
    public String novo(Model model) {

        Servico servico = new Servico();

        servico.setStatus(Servico.STATUS_ATIVO);

        model.addAttribute("servico", servico);

        return "servico-form";
    }

    @PostMapping("/servicos/salvar")
    public String salvar(
            @ModelAttribute("servico") Servico servico,
            RedirectAttributes redirectAttributes
    ) {

        if (servico.getNome() == null || servico.getNome().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Informe o nome do serviço."
            );

            return "redirect:/servicos/novo";
        }

        servicoService.salvar(servico);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Serviço salvo com sucesso."
        );

        return "redirect:/servicos";
    }

    @GetMapping("/servicos/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        Servico servico = servicoService.buscarPorId(id);

        if (servico == null) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Serviço não encontrado."
            );

            return "redirect:/servicos";
        }

        model.addAttribute("servico", servico);

        return "servico-form";
    }

    @GetMapping("/servicos/inativar/{id}")
    public String inativar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        boolean inativado = servicoService.inativar(id);

        if (!inativado) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não é possível ocultar este serviço, pois ele já está vinculado a uma ordem de serviço."
            );

            return "redirect:/servicos";
        }

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Serviço ocultado com sucesso."
        );

        return "redirect:/servicos";
    }

    @GetMapping("/servicos/ativar/{id}")
    public String ativar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        servicoService.ativar(id);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Serviço ativado novamente."
        );

        return "redirect:/servicos";
    }
}