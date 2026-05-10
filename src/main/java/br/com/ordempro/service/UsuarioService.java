package br.com.ordempro.service;

import br.com.ordempro.model.Usuario;
import br.com.ordempro.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario buscarUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String emailUsuarioLogado = authentication.getName();

        if (emailUsuarioLogado == null || emailUsuarioLogado.isBlank()) {
            return null;
        }

        return buscarPorEmail(emailUsuarioLogado);
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario.getIdUsuario() != null) {
            Usuario existente = buscarPorId(usuario.getIdUsuario());

            if (existente != null) {
                if (usuario.getSenhaHash() == null || usuario.getSenhaHash().isBlank()) {
                    usuario.setSenhaHash(existente.getSenhaHash());
                }
            }
        }

        boolean senhaPrecisaSerCodificada =
                usuario.getSenhaHash() != null
                        && !usuario.getSenhaHash().isBlank()
                        && !usuario.getSenhaHash().startsWith("$2a$")
                        && !usuario.getSenhaHash().startsWith("$2b$")
                        && !usuario.getSenhaHash().startsWith("$2y$");

        if (senhaPrecisaSerCodificada) {
            usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        }

        return usuarioRepository.save(usuario);
    }

    public void alterarSenhaDoUsuarioLogado(String emailUsuarioLogado,
                                            String senhaAtual,
                                            String novaSenha,
                                            String confirmacaoSenha) {
        validarCamposAlteracaoSenha(emailUsuarioLogado, senhaAtual, novaSenha, confirmacaoSenha);

        Usuario usuario = buscarPorEmail(emailUsuarioLogado);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário logado não encontrado.");
        }

        boolean senhaAtualCorreta = passwordEncoder.matches(senhaAtual, usuario.getSenhaHash());

        if (!senhaAtualCorreta) {
            throw new IllegalArgumentException("A senha atual informada está incorreta.");
        }

        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    private void validarCamposAlteracaoSenha(String emailUsuarioLogado,
                                             String senhaAtual,
                                             String novaSenha,
                                             String confirmacaoSenha) {
        if (emailUsuarioLogado == null || emailUsuarioLogado.isBlank()) {
            throw new IllegalArgumentException("Não foi possível identificar o usuário logado.");
        }

        if (senhaAtual == null || senhaAtual.isBlank()) {
            throw new IllegalArgumentException("Informe a senha atual.");
        }

        if (novaSenha == null || novaSenha.isBlank()) {
            throw new IllegalArgumentException("Informe a nova senha.");
        }

        if (confirmacaoSenha == null || confirmacaoSenha.isBlank()) {
            throw new IllegalArgumentException("Confirme a nova senha.");
        }

        if (novaSenha.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
        }

        if (!novaSenha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("A nova senha e a confirmação não conferem.");
        }

        if (senhaAtual.equals(novaSenha)) {
            throw new IllegalArgumentException("A nova senha deve ser diferente da senha atual.");
        }
    }

    public void excluirPorId(Long id) {
        usuarioRepository.deleteById(id);
    }
}