package br.com.ordempro.service;

import br.com.ordempro.model.Usuario;
import br.com.ordempro.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        String nomeFuncao = "USUARIO";
        if (usuario.getFuncao() != null && usuario.getFuncao().getNome() != null) {
            nomeFuncao = usuario.getFuncao().getNome();
        }

        List<GrantedAuthority> autoridades = List.of(
                new SimpleGrantedAuthority("ROLE_" + nomeFuncao.toUpperCase())
        );

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .authorities(autoridades)
                .disabled(false)
                .build();
    }
}