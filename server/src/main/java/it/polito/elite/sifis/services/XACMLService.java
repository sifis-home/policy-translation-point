package it.polito.elite.sifis.services;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.xacml.PolicyType;

public interface XACMLService {

	List<PolicyType> translate(Rule rule, String username) throws OWLOntologyCreationException, InterruptedException;
	
	
}
