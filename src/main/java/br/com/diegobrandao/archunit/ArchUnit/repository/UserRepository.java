package br.com.diegobrandao.archunit.ArchUnit.repository;

import br.com.diegobrandao.archunit.ArchUnit.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void deleteById(Long id);
}