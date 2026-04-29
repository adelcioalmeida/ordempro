package br.com.ordempro.controller;

import br.com.ordempro.model.Cidade;
import br.com.ordempro.service.CidadeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cidades")
public class CidadeController {

    private final CidadeService cidadeService;

    public CidadeController(CidadeService cidadeService) {
        this.cidadeService = cidadeService;
    }

    @GetMapping
    public String listarCidades(
            @RequestParam(required = false) String filtro,
            Model model
    ) {
        model.addAttribute("cidades", cidadeService.buscarComFiltro(filtro));
        model.addAttribute("filtroSelecionado", filtro);
        return "cidade-lista";
    }

    @GetMapping("/nova")
    public String exibirFormularioNovaCidade(Model model) {
        model.addAttribute("cidade", new Cidade());
        return "cidade-form";
    }

    @PostMapping("/salvar")
    public String salvarCidade(
            @ModelAttribute Cidade cidade,
            RedirectAttributes redirectAttributes
    ) {
        cidadeService.salvar(cidade);
        redirectAttributes.addFlashAttribute("sucesso", "Cidade salva com sucesso.");
        return "redirect:/cidades";
    }

    @GetMapping("/excluir/{id}")
    public String excluirCidade(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            cidadeService.excluirPorId(id);
            redirectAttributes.addFlashAttribute("sucesso", "Cidade excluída com sucesso.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/cidades";
    }

    @GetMapping("/importar")
    public String importarCidades(RedirectAttributes redirectAttributes) {
        cidadeService.importarCidadesIBGE();
        redirectAttributes.addFlashAttribute("sucesso", "Importação de cidades realizada com sucesso.");
        return "redirect:/cidades";
    }
}