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
	Technology getTechnologyFromName(String technologyName) throws InterruptedException;
	IoTEntity createEntity(IoTEntity entity, String name) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException ;
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
	Collection<IoTEntity> getIoTEntitiesByServices(List<Service> services, String username) throws OWLOntologyCreationException;
	Collection<Trigger> getSupportedTriggers(String username) throws OWLOntologyCreationException, InterruptedException;
	Collection<Action> getSupportedActions(String username) throws OWLOntologyCreationException, InterruptedException;
	Set<OWLClass> getSubClasses(String string) throws InterruptedException;
	Set<Service> getAllServices() throws InterruptedException;
	List<String> getClasses(String individualURL) throws InterruptedException;
	String getTechnology(String triggerUrl, int type) throws InterruptedException;
	Map<Long, List<Service>> getServicesMap() throws InterruptedException;
	void deleteAllEntities(String username) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException;
		Collection<Service> getTriggerServices(String username) throws InterruptedException, OWLOntologyCreationException;
	Collection<Service> getActionServices(String username) throws InterruptedException, OWLOntologyCreationException;
	Set<Command> getCommands(Action action, Location location, String username) throws InterruptedException, OWLOntologyCreationException;
	Set<Notification> getNotifications(Trigger trigger, Location location, String username) throws InterruptedException, OWLOntologyCreationException;
	
	Collection<Trigger> getSupportedTriggers(Collection<IoTEntity> entities, String username)
			throws OWLOntologyCreationException, InterruptedException;
	Collection<Action> getSupportedActions(Collection<IoTEntity> entities, String string)
			throws OWLOntologyCreationException, InterruptedException;

	
	Collection<IoTEntity> getIoTEntities(String string) throws OWLOntologyCreationException;
	boolean isIn(IoTEntity e, String triggerWhere, String username);
	boolean isA(String url, String string);
	boolean isA(String url, String cl, String username);
	IoTEntity createEntity(IoTEntity entity, String location, String username)
			throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException;
	
	List<Service> getServicesByIoTEntity(String url, String username) throws OWLOntologyCreationException;
	List<String> getLocation(IoTEntity entity, String username);
	
	void registerDemoPlot() throws OWLOntologyCreationException, OWLOntologyStorageException,
	FileNotFoundException, InterruptedException;
	


}

