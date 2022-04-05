package it.polito.elite.sifis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polito.elite.sifis.entities.db.Dbaction;


public interface ActionRepository extends JpaRepository<Dbaction, Long>{

}
