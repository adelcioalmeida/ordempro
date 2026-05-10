package br.com.ordempro.repository;

import br.com.ordempro.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    List<Cidade> findByNomeContainingIgnoreCaseOrUfContainingIgnoreCase(String nome, String uf);

    boolean existsByNomeIgnoreCaseAndUfIgnoreCase(String nome, String uf);
}