package it.polito.elite.sifis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.polito.elite.sifis.entities.db.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String email);
}
