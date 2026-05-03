package br.com.ordempro.controller;

import br.com.ordempro.model.Usuario;
import br.com.ordempro.service.UsuarioService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioLogadoControllerAdvice {

    private static final String NOME_PADRAO = "USUÁRIO";
    private static final String PERFIL_PADRAO = "SEM PERFIL";

    private final UsuarioService usuarioService;

    public UsuarioLogadoControllerAdvice(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("usuarioLogado")
    public Usuario usuarioLogado() {
        try {
            return usuarioService.buscarUsuarioLogado();
        } catch (Exception exception) {
            return null;
        }
    }

    @ModelAttribute("usuarioLogadoNome")
    public String usuarioLogadoNome(@ModelAttribute("usuarioLogado") Usuario usuario) {
        if (usuario == null || textoEmBranco(usuario.getNome())) {
            return NOME_PADRAO;
        }

        return usuario.getNome().trim();
    }

    @ModelAttribute("usuarioLogadoPerfil")
    public String usuarioLogadoPerfil(@ModelAttribute("usuarioLogado") Usuario usuario) {
        if (usuario == null ||
                usuario.getFuncao() == null ||
                textoEmBranco(usuario.getFuncao().getNome())) {
            return PERFIL_PADRAO;
        }

        return usuario.getFuncao().getNome().trim().toUpperCase();
    }

    private boolean textoEmBranco(String texto) {
        return texto == null || texto.isBlank();
    }
}