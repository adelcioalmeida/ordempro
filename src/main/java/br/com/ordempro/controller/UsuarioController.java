package br.com.ordempro.controller;

import br.com.ordempro.model.Usuario;
import br.com.ordempro.service.FuncaoService;
import br.com.ordempro.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FuncaoService funcaoService;

    public UsuarioController(UsuarioService usuarioService, FuncaoService funcaoService) {
        this.usuarioService = usuarioService;
        this.funcaoService = funcaoService;
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, Authentication authentication) {
        model.addAttribute("usuarios", usuarioService.listarTodos());

        String emailUsuarioLogado = authentication != null ? authentication.getName() : null;
        model.addAttribute("emailUsuarioLogado", emailUsuarioLogado);

        return "usuario-lista";
    }

    @GetMapping("/usuarios/novo")
    public String novoUsuario(Model model) {
        Usuario usuario = new Usuario();
        usuario.setAtivo(true);

        model.addAttribute("usuario", usuario);
        model.addAttribute("funcoes", funcaoService.listarTodas());

        return "usuario-form";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/usuarios";
        }

        usuario.setSenhaHash("");

        model.addAttribute("usuario", usuario);
        model.addAttribute("funcoes", funcaoService.listarTodas());

        return "usuario-form";
    }

    @PostMapping("/usuarios/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }

        boolean edicao = usuario.getIdUsuario() != null;

        usuarioService.salvar(usuario);

        if (edicao) {
            redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso.");
        } else {
            redirectAttributes.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso.");
        }

        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/alterar-senha")
    public String alterarSenhaDoUsuarioLogado(@RequestParam String senhaAtual,
                                              @RequestParam String novaSenha,
                                              @RequestParam String confirmacaoSenha,
                                              Authentication authentication,
                                              RedirectAttributes redirectAttributes) {
        if (authentication == null || authentication.getName() == null) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não autenticado.");
            return "redirect:/login";
        }

        String emailUsuarioLogado = authentication.getName();

        try {
            usuarioService.alterarSenhaDoUsuarioLogado(
                    emailUsuarioLogado,
                    senhaAtual,
                    novaSenha,
                    confirmacaoSenha
            );

            redirectAttributes.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }

        return "redirect:/ordens";
    }

    @GetMapping("/usuarios/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes,
                                 Authentication authentication) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/usuarios";
        }

        String emailUsuarioLogado = authentication != null ? authentication.getName() : null;

        if (emailUsuarioLogado != null && emailUsuarioLogado.equalsIgnoreCase(usuario.getEmail())) {
            redirectAttributes.addFlashAttribute("erro", "Você não pode excluir o usuário que está logado no sistema.");
            return "redirect:/usuarios";
        }

        usuarioService.excluirPorId(id);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário excluído com sucesso.");

        return "redirect:/usuarios";
    }
}