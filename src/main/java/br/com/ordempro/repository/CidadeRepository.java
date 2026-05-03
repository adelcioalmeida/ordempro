package br.com.ordempro.repository;

import br.com.ordempro.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    List<Cidade> findByNomeContainingIgnoreCaseOrUfContainingIgnoreCase(String nome, String uf);

    Optional<Cidade> findByNomeIgnoreCaseAndUfIgnoreCase(String nome, String uf);
}
