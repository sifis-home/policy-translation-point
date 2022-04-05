package it.polito.elite.sifis.services;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import it.polito.elite.sifis.entities.db.Dbentity;
import it.polito.elite.sifis.entities.db.Dbrule;
import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.owl.Trigger;
import it.polito.elite.sifis.entities.db.User;

public interface DBService {

	public Dbentity saveEntity(Dbentity entity);
	
	public Dbrule saveRule(Rule rule, String type, String username) throws OWLOntologyCreationException;

	public List<Rule> getRulesByType(String type, String username) throws OWLOntologyCreationException, InterruptedException;

	public void deleteRule(Long ruleId);

	public Set<Trigger> getDefinedTriggersByType(String type, String username) throws OWLOntologyCreationException, InterruptedException;

	public Rule getRule(Long ruleId, String username) throws OWLOntologyCreationException, InterruptedException;

	public List<Rule> getPossibleRules(String type, String username) throws InterruptedException, OWLOntologyCreationException;

	public void deleteAllRules(String username) throws OWLOntologyCreationException, InterruptedException;

	public void deleteAllEntities(String username);

	public List<Dbentity> getEntities(String username);
}
