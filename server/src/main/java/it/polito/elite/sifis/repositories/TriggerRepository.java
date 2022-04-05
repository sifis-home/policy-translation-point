package it.polito.elite.sifis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polito.elite.sifis.entities.db.Dbtrigger;

public interface TriggerRepository extends JpaRepository<Dbtrigger, Long>{

}
