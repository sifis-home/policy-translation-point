package it.polito.elite.sifis.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polito.elite.sifis.entities.db.Dbrule;
import it.polito.elite.sifis.entities.db.User;


public interface RuleRepository extends JpaRepository<Dbrule, Long>{

	List<Dbrule> findByUser(User u);

	List<Dbrule> findByUserAndType(User u, String type);

	List<Dbrule> findByType(String type);

}
