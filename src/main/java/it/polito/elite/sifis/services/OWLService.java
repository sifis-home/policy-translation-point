package it.polito.elite.sifis.services;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Command;
import it.polito.elite.sifis.entities.owl.Detail;
import it.polito.elite.sifis.entities.owl.IoTEntity;
import it.polito.elite.sifis.entities.owl.Location;
import it.polito.elite.sifis.entities.owl.Notification;
import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.owl.Service;
import it.polito.elite.sifis.entities.owl.Technology;
import it.polito.elite.sifis.entities.owl.Trigger;


public interface OWLService {
	OWLOntology loadFromFile(String ontologyUrl) throws OWLOntologyCreationException ; 
	OWLOntology loadFromWeb(String ontologyUrl)throws OWLOntologyCreationException;
	OWLOntology getBaseOntology();
	OWLReasoner initHermiTReasoner(OWLOntology ontology) ;
	void addOntology(String fileName,IRI iri) throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException ;
	IRI getBaseIRI();
	Collection<Service> getTriggerServices() throws InterruptedException;
	Set<Trigger> getTriggers(String service, String username) throws InterruptedException, OWLOntologyCreationException;
	List<Detail> getTriggerDetails(OWLReasoner reasoner, String service, String trigger, String username) throws OWLOntologyCreationException, InterruptedException;
	Collection<Service> getActionServices() throws InterruptedException;
	Set<Action> getActions(String service, String username) throws InterruptedException, OWLOntologyCreationException;
	List<Detail> getActionDetails(OWLReasoner reasoner, String service, String action, String username) throws OWLOntologyCreationException, InterruptedException;
	void saveRule(Rule rule);
	Collection<IoTEntity> getIoTEntitiesByService(String service,String username) throws OWLOntologyCreationException;	
	Trigger getTrigger(String url, String username) throws OWLOntologyCreationException, InterruptedException;
	Action getAction(String url, String username) throws OWLOntologyCreationException, InterruptedException;
	boolean triggers(Action action, Trigger trigger) throws InterruptedException;
	boolean equalsML(Action action, Action ruleAction);
	IoTEntity getIoTEntityByUrl(String url, String username) throws OWLOntologyCreationException;
	Set<OWLClass> getSubClasses(String string) throws InterruptedException;
	List<String> getClasses(String individualURL) throws InterruptedException;
	String getTechnology(String triggerUrl, int type) throws InterruptedException;
	Set<Command> getCommands(Action action, Location location, String username) throws InterruptedException, OWLOntologyCreationException;
	List<Service> getServicesByIoTEntity(String url, String username) throws OWLOntologyCreationException;
	List<String> getDirectClasses(String individualURL) throws InterruptedException;
	


}

