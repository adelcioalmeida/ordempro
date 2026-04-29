package br.com.ordempro.service;

import br.com.ordempro.model.Usuario;
import br.com.ordempro.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

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

    public void excluirPorId(Long id) {
        usuarioRepository.deleteById(id);
    }
}