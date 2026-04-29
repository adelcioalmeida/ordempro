package br.com.ordempro.security;

import br.com.ordempro.model.Usuario;
import br.com.ordempro.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        String permissao = "ROLE_VENDEDOR";

        if (usuario.getFuncao() != null && usuario.getFuncao().getNome() != null && !usuario.getFuncao().getNome().isBlank()) {
            String nomeFuncao = usuario.getFuncao().getNome().trim().toUpperCase().replace(" ", "_");

            if (!nomeFuncao.startsWith("ROLE_")) {
                permissao = "ROLE_" + nomeFuncao;
            } else {
                permissao = nomeFuncao;
            }
        }

        boolean ativo = usuario.getAtivo() != null ? usuario.getAtivo() : false;

        return new User(
                usuario.getEmail(),
                usuario.getSenhaHash(),
                ativo,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(permissao))
        );
    }
}