package it.polito.elite.sifis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polito.elite.sifis.entities.db.Dbdetail;


public interface DetailRepository extends JpaRepository<Dbdetail, Long>{

}
