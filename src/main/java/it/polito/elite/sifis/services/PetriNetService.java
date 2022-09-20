package it.polito.elite.sifis.services;


import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.owl.Trigger;
import it.polito.elite.sifis.entities.petrinet.PetriNet;
import it.polito.elite.sifis.entities.petrinet.RestSimulator;


public interface PetriNetService {
	PetriNet getNet(String username, List<Rule> rules, boolean refresh) throws OWLOntologyCreationException, InterruptedException;
	RestSimulator getRestSimulator(String username); 
	Rule stepRestSimulation(String username);
	void stopRestSimulator(String name);
	Rule startRestSimulation(Trigger trigger, List<Rule> rules, String name) throws OWLOntologyCreationException, InterruptedException;
	List<Rule> getParents(Rule simulatedRule, List<Rule> executedRules) throws InterruptedException;
}
