package it.polito.elite.sifis.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.polito.elite.sifis.entities.db.Dbentity;
import it.polito.elite.sifis.entities.db.User;

@Repository
public interface EntityRepository extends JpaRepository<Dbentity, Long>{

	Dbentity findByUrl(String url);

	List<Dbentity> findByUser(User u);

}
