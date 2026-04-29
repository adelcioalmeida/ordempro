package br.com.ordempro.repository;

import br.com.ordempro.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Override
    @EntityGraph(attributePaths = "funcao")
    List<Usuario> findAll();

    @Override
    @EntityGraph(attributePaths = "funcao")
    Optional<Usuario> findById(Long id);

    @EntityGraph(attributePaths = "funcao")
    Optional<Usuario> findByEmail(String email);
}