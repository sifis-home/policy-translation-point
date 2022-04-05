package it.polito.elite.sifis.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.InferredObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.springframework.beans.factory.annotation.Autowired;

import it.polito.elite.sifis.entities.db.Dbentity;
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
import it.polito.elite.sifis.utils.PropertyFileReader;


public class OWLServiceImpl implements OWLService{
	
	@Autowired
	UserService userService;
	
	@Autowired
	DBService dbService;
	
	// default configuration file
	private static String configFile = "config.properties";
	private static Logger logger = LogManager
			.getLogger(OWLServiceImpl.class.getName());
	
	
	private OWLOntology baseOntology;
	BlockingQueue<OWLReasoner> reasoners = new LinkedBlockingDeque<>();

	
	private OWLOntologyManager manager;
	private DefaultPrefixManager prefix;
	private Map<String, OWLReasoner> userReasoners;
	Properties prop;
	
	public OWLServiceImpl() throws IOException, OWLOntologyCreationException {
		prop = PropertyFileReader.loadProperties(configFile);
		manager = OWLManager.createOWLOntologyManager();
		this.userReasoners = new HashMap<String,OWLReasoner>();
		if(this.baseOntology != null) 
			manager.removeOntology(baseOntology);
		
		//åthis.baseOntology = loadFromWeb(prop.getProperty("ontologyBaseEndPoint"));
		this.baseOntology = loadFromFile(prop.getProperty("ontologySifisEndPoint"));
		
		int n = Integer.valueOf(prop.getProperty("queueSize"));
		
		for(int i = 0; i < n; i++){
			try {reasoners.put(initHermiTReasoner(this.baseOntology));} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		//this.reasoner = initHermiTReasoner(this.baseOntology);
		this.prefix = new DefaultPrefixManager(null, null,baseOntology.getOntologyID().getOntologyIRI().get().toString() + "#");
	    this.prefix.setPrefix("eupont:", "http://elite.polito.it/ontologies/eupont.owl#");
	    //this.prefix.setPrefix("eupont-conversational", "http://elite.polito.it/ontologies/eupont-conversational.owl");
	    this.prefix.setPrefix("foaf:", "http://xmlns.com/foaf/0.1/");
	    System.out.println(baseOntology.getOntologyID().getOntologyIRI().get());
	}
	
	
	/**
	 * This method loads an ontology from the web
	 * 
	 * 
	 * @param ontologyUrl URL of the base ontology
	 * @return OWLOntology
	 * @throws OWLOntologyCreationException
	 */
	public OWLOntology loadFromWeb(String ontologyUrl) throws OWLOntologyCreationException{
		logger.info("Load ontology from the web at: " + ontologyUrl);
		IRI ontologyIri = IRI.create(ontologyUrl);
		OWLOntology ontology =  manager.loadOntologyFromOntologyDocument(ontologyIri);
		logger.info("Ontology succesfully loaded");
		return ontology;
	}

	/**
	 * This method loads an ontology from a local file
	 * 
	 * @param ontologyUrl URL of the base ontology
	 * @return OWLOntology
	 * @throws OWLOntologyCreationException
	 */
	public OWLOntology loadFromFile(String ontologyUrl) throws OWLOntologyCreationException {
		logger.info("Load ontology from file: " + ontologyUrl);
		File ontologyF = new File(prop.getProperty("baseAddress") + prop.getProperty("ontologyDir") + ontologyUrl);
		manager.getIRIMappers().add(new AutoIRIMapper(new File(prop.getProperty("baseAddress") + prop.getProperty("ontologyDir")), true));
		OWLOntology ontology =  manager.loadOntologyFromOntologyDocument(ontologyF);
		logger.info("Ontology succesfully loaded");
		return ontology;
	}
	
	/**
	 * This method returns an instance of the base ontology
	 *  
	 * @return OWLOntology
	 */
	public OWLOntology getBaseOntology(){
		return this.baseOntology;
	}

	/**
	 * This methods creates a HermiT reasoner for a loaded ontology
	 */
	public OWLReasoner initHermiTReasoner(OWLOntology ontology) {
		logger.info("Create reasoner");
		OWLReasoner reasoner = new Reasoner.ReasonerFactory().createNonBufferingReasoner(ontology);
		reasoner.precomputeInferences(InferenceType.values());
		logger.info("Reasoner succesfully created");
		return reasoner;
	}

	/**
	 * This method duplicate the base ontology for a new user
	 * 
	 * @param fileName the name for the new ontology
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 * @throws OWLOntologyCreationException 
	 */
	public void addOntology(String fileName, IRI iri) throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException{
		
		logger.info("Add a new ontology with file name: " + fileName);
		//Creation of the new ontology with a custom user-URI
		OWLOntology newOntology = this.manager.createOntology(iri);
		this.prefix.setPrefix(fileName + ":", iri + "#");
		//Add the import of the base ontology (i.e., EUPont-composition)
		OWLImportsDeclaration importDeclaration = this.manager.getOWLDataFactory().getOWLImportsDeclaration(this.baseOntology.getOntologyID().getOntologyIRI().get());
		this.manager.applyChange(new AddImport(newOntology,importDeclaration));
		
		OWLNamedIndividual user = newOntology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(fileName + ":") + fileName));
		OWLClass userClass = newOntology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("foaf:") + "Person"));
		OWLClassAssertionAxiom userAxiom = newOntology.getOWLOntologyManager().getOWLDataFactory().getOWLClassAssertionAxiom(userClass, user);
		this.manager.applyChange(new AddAxiom(newOntology, userAxiom));
				
		//Save the new ontology
		File file = new File(prop.getProperty("baseAddress") + prop.getProperty("ontologyDir") + fileName + ".owl");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));	
		manager.saveOntology(newOntology, outputStream);

		OWLReasoner userReasoner = this.initHermiTReasoner(newOntology);
		this.userReasoners.put(fileName, userReasoner);
		logger.info("Ontology succesfully added");
	}

	/**
	 * This method returns the IRI of the base ontology
	 */
	@Override
	public IRI getBaseIRI() {
		return baseOntology.getOntologyID().getOntologyIRI().get();
	}
	

	@Override
	public Set<Service> getAllServices() throws InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		Set<Service> services = new HashSet<Service>();
		try{
			services.addAll(this.getTriggerServices());
			services.addAll(this.getActionServices());
		}finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return services;
	}
	
	@Override
	public Map<Long, List<Service>> getServicesMap() throws InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		Map<Long, List<Service>> map = new HashMap<Long, List<Service>>();
		try{
			OWLClass serviceClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + "Channel"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			Set<OWLClass> owlServiceClasses = reasoner.getSubClasses(serviceClass, true).getFlattened();
			for(OWLClass owlServiceClass : owlServiceClasses){
				Set<OWLNamedIndividual> owlServices = reasoner.getInstances(owlServiceClass, true).getFlattened();
				List<Service> services = new ArrayList<Service>();

				for(OWLNamedIndividual owlService : owlServices){
					Service service = new Service();
					service.setURL(owlService.getIRI().toString());
					service.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
					
					try{service.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
					try{service.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
					services.add(service);
					
				}
				if(services.size() > 0)
					map.put(services.get(0).getWebId(), services);
			}
			
		}finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return map;
	}
	
	@Override
	public Collection<Service> getTriggerServices(String username) throws InterruptedException, OWLOntologyCreationException {
		
		OWLReasoner reasoner = this.reasoners.take();
		Set<Service> services = new HashSet<Service>();
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
		
		try{
			OWLObjectProperty hasRegisteredEntity = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasRegisteredEntity"));			
			OWLClass serviceClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + "Channel"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			OWLObjectProperty offerTrigger = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerTrigger"));
			Set<OWLClass> owlServiceClasses = reasoner.getSubClasses(serviceClass, true).getFlattened();
			
			for(OWLClass owlServiceClass : owlServiceClasses){
				Set<OWLNamedIndividual> owlServices = reasoner.getInstances(owlServiceClass, true).getFlattened();
				boolean add = false;
				Set<Service> tempServices = new HashSet<Service>();
	
				for(OWLNamedIndividual owlService : owlServices){
					
					if(reasoner.getObjectPropertyValues(owlService, offerTrigger).getFlattened().size() > 0 & userReasoner.getObjectPropertyValues(owlService, hasRegisteredEntity).getFlattened().size() > 0)
						add = true;
									
					Service service = new Service();
					service.setURL(owlService.getIRI().toString());
					service.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
					
					try{service.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
					try{service.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
	
					tempServices.add(service);
				}
				if(add)
					services.addAll(tempServices);
			}
		
		}finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return services;
	}


	@Override
	public Collection<Service> getActionServices(String username) throws InterruptedException, OWLOntologyCreationException {
		OWLReasoner reasoner = this.reasoners.take();
		Set<Service> services = new HashSet<Service>();
		
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
		try{
			OWLObjectProperty hasRegisteredEntity = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasRegisteredEntity"));			
			OWLClass serviceClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + "Channel"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			OWLObjectProperty offerAction = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerAction"));
			
			Set<OWLClass> owlServiceClasses = reasoner.getSubClasses(serviceClass, true).getFlattened();
			
			for(OWLClass owlServiceClass : owlServiceClasses){
				Set<OWLNamedIndividual> owlServices = reasoner.getInstances(owlServiceClass, true).getFlattened();
				boolean add = false;
				Set<Service> tempServices = new HashSet<Service>();
	
				for(OWLNamedIndividual owlService : owlServices){
					
					if(reasoner.getObjectPropertyValues(owlService, offerAction).getFlattened().size() > 0 & userReasoner.getObjectPropertyValues(owlService, hasRegisteredEntity).getFlattened().size() > 0)
						add = true;
					
					Service service = new Service();
					service.setURL(owlService.getIRI().toString());
					service.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
					try{service.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
					try{service.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
	
					tempServices.add(service);
				}
				if(add)
					services.addAll(tempServices);
		}
		}finally{
			if(reasoner != null)  this.reasoners.put(reasoner);
		}
		return services;
	}
	
	@Override
	public Set<Service> getTriggerServices() throws InterruptedException {
		
		OWLReasoner reasoner = this.reasoners.take();
		Set<Service> services = new HashSet<Service>();

		try{
			
			OWLClass serviceClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + "Channel"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			OWLObjectProperty offerTrigger = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerTrigger"));
			Set<OWLClass> owlServiceClasses = reasoner.getSubClasses(serviceClass, true).getFlattened();
			
			for(OWLClass owlServiceClass : owlServiceClasses){
				Set<OWLNamedIndividual> owlServices = reasoner.getInstances(owlServiceClass, true).getFlattened();
				boolean add = false;
				Set<Service> tempServices = new HashSet<Service>();
	
				for(OWLNamedIndividual owlService : owlServices){
					
					if(reasoner.getObjectPropertyValues(owlService, offerTrigger).getFlattened().size() > 0)
						add = true;
					
					Service service = new Service();
					service.setURL(owlService.getIRI().toString());
					service.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
					
					try{service.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
					try{service.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
	
					tempServices.add(service);
				}
				if(add)
					services.addAll(tempServices);
			}
		
		}finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return services;
	}
	
	@Override
	public Trigger getTrigger(String url, String username) throws OWLOntologyCreationException, InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		Trigger trigger = new Trigger();
		try{
			OWLObjectProperty isOfChannel = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "isOfChannel"));
			OWLDataProperty description = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getPrefix("eupont:") + "description"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			
			
			OWLNamedIndividual owlTrigger = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(url));
			trigger.setURL(owlTrigger.getIRI().toString());
			trigger.setId(this.prefix.getShortForm(owlTrigger.getIRI()).substring(1));
			try{trigger.setName(reasoner.getDataPropertyValues(owlTrigger,name).iterator().next().getLiteral());}catch(Throwable t){}
			try{trigger.setDescription(reasoner.getDataPropertyValues(owlTrigger,description).iterator().next().getLiteral());}catch(Throwable t){}
			Set<OWLNamedIndividual> channels = reasoner.getObjectPropertyValues(owlTrigger, isOfChannel).getFlattened();
			OWLNamedIndividual owlService = channels.iterator().next();
			if(owlService != null){
				Service service = new Service();
				service.setURL(owlService.getIRI().toString());
				service.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
				try{service.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
				try{service.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
				try{service.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
				try{service.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
				trigger.setService(service);
				trigger.setDetails(this.getTriggerDetails(reasoner, service.getId(), trigger.getId(), username));
			}
		}
		finally{
			if(reasoner != null)  this.reasoners.put(reasoner);
		}
		return trigger;
	}

	
	@Override
	public Set<Trigger> getTriggers(String service, String username) throws InterruptedException, OWLOntologyCreationException {
		OWLReasoner reasoner = this.reasoners.take();
		Set<Trigger> triggers = new HashSet<Trigger>();
		try{
			OWLDataProperty description = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getPrefix("eupont:") + "description"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			OWLNamedIndividual owlService = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getDefaultPrefix() + service));
			Service s = new Service();
			s.setURL(owlService.getIRI().toString());
			s.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
			try{s.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
			try{s.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
			try{s.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
			try{s.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
			
			
			OWLObjectProperty offerTrigger = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerTrigger"));
			
			Set<OWLNamedIndividual> owlTriggers;
		
			if(reasoner != null && reasoner.getObjectPropertyValues(owlService, offerTrigger) != null){
				owlTriggers = reasoner.getObjectPropertyValues(owlService, offerTrigger).getFlattened();
				for(OWLNamedIndividual owlTrigger : owlTriggers){
					Trigger trigger = new Trigger();
					trigger.setURL(owlTrigger.getIRI().toString());
					trigger.setId(this.prefix.getShortForm(owlTrigger.getIRI()).substring(1));
					trigger.setService(s);
					try{trigger.setName(reasoner.getDataPropertyValues(owlTrigger,name).iterator().next().getLiteral());}catch(Throwable t){}
					try{trigger.setDescription(reasoner.getDataPropertyValues(owlTrigger,description).iterator().next().getLiteral());}catch(Throwable t){}
					triggers.add(trigger);
					trigger.setDetails(this.getTriggerDetails(reasoner, s.getId(), trigger.getId(), username));

				}
			}
		}
		finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return triggers;
	}



	@Override
	public List<Detail> getTriggerDetails(OWLReasoner reasoner, String service, String trigger, String username) throws OWLOntologyCreationException, InterruptedException {
		List<Detail> details = new LinkedList<Detail>();
		boolean taken = false;
		if(reasoner == null){
			taken = true;
			reasoner = this.reasoners.take();
		}
		
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
		
		try{
			OWLClass owlLocationClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + "Location"));

			OWLNamedIndividual owlTrigger = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getDefaultPrefix() + trigger));
			OWLObjectProperty offerDetail = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerDetail"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty type = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getPrefix("eupont:") + "type"));
			Set<OWLNamedIndividual> owlDetails = reasoner.getObjectPropertyValues(owlTrigger, offerDetail).getFlattened();
			
			
			Set<Object> entities = new HashSet<Object>();
			for(IoTEntity entity : this.getIoTEntitiesByService(service, username)){
				entities.add(entity);
			}
			
			
			boolean entityDetail = false;
			
			for(OWLNamedIndividual owlDetail : owlDetails){
				Detail detail = new Detail();
				detail.setId(this.prefix.getShortForm(owlDetail.getIRI()).substring(1));
				detail.setURL(owlDetail.getIRI().toString());
				try{detail.setName(reasoner.getDataPropertyValues(owlDetail,name).iterator().next().getLiteral());}catch(Throwable t){}
				String detailType = null;
				try{detailType = reasoner.getDataPropertyValues(owlDetail,type).iterator().next().getLiteral();}catch(Throwable t){}
				detail.setType(detailType);
				
				if(detailType.equals("entity")){
					entityDetail = true;
					detail.setAlternatives(entities);
				}
				if(detailType.equals("location")){
					Set<Object> locations = new HashSet<Object>();
					
					Set<OWLNamedIndividual> owlLocations = userReasoner.getInstances(owlLocationClass, false).getFlattened();
					for (OWLNamedIndividual owlLocation : owlLocations) {
						Location loc = new Location();
						loc.setURL(owlLocation.getIRI().toString());
						try{loc.setName(userReasoner.getDataPropertyValues(owlLocation,name).iterator().next().getLiteral());}catch(Throwable t){}
						locations.add(loc);
					}
					

					detail.setAlternatives(locations);
				}
				details.add(detail);
			}
			
			if(!entityDetail && entities.size() > 0){
				Detail detail = new Detail();
				detail.setName("Which entity?");
				detail.setType("entity");
				detail.setURL("entity_detail");
				detail.setAlternatives(entities);
				details.add(detail);
			}
		}
		finally{
			if(taken && reasoner != null) this.reasoners.put(reasoner);
		}
		return details;
	}

	
	@Override
	public Set<Service> getActionServices() throws InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		Set<Service> services = new HashSet<Service>();
		try{
			OWLClass serviceClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + "Channel"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			OWLObjectProperty offerAction = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerAction"));
			
			Set<OWLClass> owlServiceClasses = reasoner.getSubClasses(serviceClass, true).getFlattened();
			
			for(OWLClass owlServiceClass : owlServiceClasses){
				Set<OWLNamedIndividual> owlServices = reasoner.getInstances(owlServiceClass, true).getFlattened();
				boolean add = false;
				Set<Service> tempServices = new HashSet<Service>();
	
				for(OWLNamedIndividual owlService : owlServices){
					
					if(reasoner.getObjectPropertyValues(owlService, offerAction).getFlattened().size() > 0)
						add = true;
					
					Service service = new Service();
					service.setURL(owlService.getIRI().toString());
					service.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
					
					try{service.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
					try{service.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
					try{service.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
	
					tempServices.add(service);
				}
				if(add)
					services.addAll(tempServices);
		}
		}finally{
			if(reasoner != null)  this.reasoners.put(reasoner);
		}
		return services;
	}
	
	@Override
	public Action getAction(String url, String username) throws OWLOntologyCreationException, InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		Action action = new Action();
		try{
			OWLObjectProperty isOfChannel = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "isOfChannel"));
			OWLDataProperty description = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getPrefix("eupont:") + "description"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			
			
			OWLNamedIndividual owlAction = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(url));
			action.setURL(owlAction.getIRI().toString());
			action.setId(this.prefix.getShortForm(owlAction.getIRI()).substring(1));
			try{action.setName(reasoner.getDataPropertyValues(owlAction,name).iterator().next().getLiteral());}catch(Throwable t){}
			try{action.setDescription(reasoner.getDataPropertyValues(owlAction,description).iterator().next().getLiteral());}catch(Throwable t){}
			Set<OWLNamedIndividual> channels = reasoner.getObjectPropertyValues(owlAction, isOfChannel).getFlattened();
			OWLNamedIndividual owlService = channels.iterator().next();
			if(owlService != null){
				Service service = new Service();
				service.setURL(owlService.getIRI().toString());
				service.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
				try{service.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
				try{service.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
				try{service.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
				try{service.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
				action.setService(service);
				action.setDetails(this.getActionDetails(reasoner, service.getId(), action.getId(), username));
		}
		} finally {
			if(reasoner != null)  this.reasoners.put(reasoner);
		}
		return action;
	}
	
	
	@Override
	public Set<Action> getActions(String service, String username) throws InterruptedException, OWLOntologyCreationException {
		OWLReasoner reasoner = this.reasoners.take();
		Set<Action> actions = new HashSet<Action>();
		try{
			OWLDataProperty description = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getPrefix("eupont:") + "description"));
			OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
			OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
			
			OWLNamedIndividual owlService = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getDefaultPrefix() + service));
			
			OWLObjectProperty offerAction = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerAction"));
			Set<OWLNamedIndividual> owlActions;
			
			if(reasoner != null && reasoner.getObjectPropertyValues(owlService, offerAction) != null){
				owlActions = reasoner.getObjectPropertyValues(owlService, offerAction).getFlattened();
			
			
				Service s = new Service();
				s.setURL(owlService.getIRI().toString());
				s.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
				try{s.setName(reasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
				try{s.setWebId(Long.valueOf(reasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
				try{s.setColor(reasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
				try{s.setImage(reasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
				
				for(OWLNamedIndividual owlAction : owlActions){
					Action action = new Action();
					action.setURL(owlAction.getIRI().toString());
					action.setId(this.prefix.getShortForm(owlAction.getIRI()).substring(1));
					action.setService(s);
					try{action.setName(reasoner.getDataPropertyValues(owlAction,name).iterator().next().getLiteral());}catch(Throwable t){}
					try{action.setDescription(reasoner.getDataPropertyValues(owlAction,description).iterator().next().getLiteral());}catch(Throwable t){}
					actions.add(action);
					action.setDetails(this.getActionDetails(reasoner, s.getId(), action.getId(), username));
				}
			}
		}
		finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return actions;
	}
	
	@Override
	public List<Detail> getActionDetails(OWLReasoner reasoner, String service, String action, String username) throws OWLOntologyCreationException, InterruptedException {

		List<Detail> details = new LinkedList<Detail>();
		boolean taken = false;
		if(reasoner == null){
			taken = true;
			reasoner = this.reasoners.take();
		}
		
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
		
		try{
			OWLClass owlLocationClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + "Location"));

			OWLNamedIndividual owlAction = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getDefaultPrefix() + action));
			OWLObjectProperty offerDetail = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerDetail"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
			OWLDataProperty type = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getPrefix("eupont:") + "type"));
			Set<OWLNamedIndividual> owlDetails = reasoner.getObjectPropertyValues(owlAction, offerDetail).getFlattened();
			
			Set<Object> entities = new HashSet<Object>();
			for(IoTEntity entity : this.getIoTEntitiesByService(service, username)){
				entities.add(entity);
			}
			
			boolean entityDetail = false;
			for(OWLNamedIndividual owlDetail : owlDetails){
				Detail detail = new Detail();
				detail.setId(this.prefix.getShortForm(owlDetail.getIRI()).substring(1));
				detail.setURL(owlDetail.getIRI().toString());
				try{detail.setName(reasoner.getDataPropertyValues(owlDetail,name).iterator().next().getLiteral());}catch(Throwable t){}
				String detailType = null;
				try{detailType = reasoner.getDataPropertyValues(owlDetail,type).iterator().next().getLiteral();}catch(Throwable t){}
				detail.setType(detailType);
				
				if(detailType.equals("entity")){
					entityDetail = true;
					detail.setAlternatives(entities);
				}
				if(detailType.equals("location")){
					Set<Object> locations = new HashSet<Object>();
					
					Set<OWLNamedIndividual> owlLocations = userReasoner.getInstances(owlLocationClass, false).getFlattened();
					for (OWLNamedIndividual owlLocation : owlLocations) {
						Location loc = new Location();
						loc.setURL(owlLocation.getIRI().toString());
						try{loc.setName(userReasoner.getDataPropertyValues(owlLocation,name).iterator().next().getLiteral());}catch(Throwable t){}
						locations.add(loc);
					}
					

					detail.setAlternatives(locations);
				}
				details.add(detail);
			}
			
			if(!entityDetail && entities.size() > 0){
				Detail detail = new Detail();
				detail.setName("Which entity?");
				detail.setType("entity");
				detail.setURL("entity_detail");
				detail.setAlternatives(entities);
				details.add(detail);
			}
		}
		finally{
			if(taken && reasoner != null) this.reasoners.put(reasoner);
		}
		return details;
	}
	
	@Override
	public IoTEntity getIoTEntityByUrl(String url, String username) throws OWLOntologyCreationException {
		OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));

		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
		
		OWLNamedIndividual owlEntity = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(url));
		IoTEntity e = new IoTEntity();
		try{e.setName(userReasoner.getDataPropertyValues(owlEntity,name).iterator().next().getLiteral());}catch(Throwable t){}
		e.setId(this.prefix.getShortForm(owlEntity.getIRI()).substring(1));
		e.setURL(owlEntity.getIRI().toString());				
		List<Service> services = this.getServicesByIoTEntity(e.getURL(),username);
		Set<String> servicesURLS = new HashSet<String>();
		for(Service service : services)
			servicesURLS.add(this.prefix.getShortForm(IRI.create(service.getURL())).substring(1));
		e.setServices(servicesURLS);
		try{
			e.setType(this.prefix.getShortForm(userReasoner.getTypes(owlEntity, true).getFlattened().iterator().next()).replace("eupont:", ""));
		}catch(Throwable t) {}
		return e;
	}
	

	@Override
	public Set<Command> getCommands(Action action, Location location, String username) throws InterruptedException, OWLOntologyCreationException {
		Set<Command> commands = new HashSet<Command>();
		OWLObjectProperty reproduceBy = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "reproduceBy"));
		OWLObjectProperty commandIsOfService = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "commandIsOfService"));
		OWLObjectProperty isOfEntity = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "isOfEntity"));
		OWLObjectProperty position = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "position"));

		OWLNamedIndividual owlAction = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(action.getURL()));

		OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
		OWLDataProperty type = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "type"));

		
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
		
		userReasoner.precomputeInferences(InferenceType.values());
		
		Set<OWLNamedIndividual> owlCommands = userReasoner.getObjectPropertyValues(owlAction, reproduceBy).getFlattened();
		for (OWLNamedIndividual owlCommand : owlCommands) {
			
			//getIoTEntity
			OWLNamedIndividual owlService = userReasoner.getObjectPropertyValues(owlCommand, commandIsOfService).getFlattened().iterator().next();
			OWLNamedIndividual owlEntity = userReasoner.getObjectPropertyValues(owlService, isOfEntity).getFlattened().iterator().next();

			//check IoTEntitiy position
			if(location != null) {
				try{
					OWLNamedIndividual owlLocation = userReasoner.getObjectPropertyValues(owlEntity, position).getFlattened().iterator().next();
					if(owlLocation != null && ! owlLocation.getIRI().toString().equals(location.getURL()))
						continue;
				}catch (Throwable t) {
					System.err.print(t.getStackTrace());
				}
			}
			
			Command command = new Command();
			command.setURL(this.prefix.getShortForm(owlCommand.getIRI()).substring(1));
			try{command.setId(userReasoner.getDataPropertyValues(owlCommand,id).iterator().next().getLiteral());}catch(Throwable t){}
			try{command.setType(userReasoner.getDataPropertyValues(owlCommand,type).iterator().next().getLiteral());}catch(Throwable t){}

			command.setEntitiy(this.getIoTEntityByUrl(owlEntity.getIRI().toString(), username));
			
			commands.add(command);
		}
		
		
		return commands;
	}


	@Override
	public Set<Notification> getNotifications(Trigger trigger, Location location, String username)
			throws InterruptedException, OWLOntologyCreationException {
		Set<Notification> notifications = new HashSet<Notification>();
		OWLObjectProperty allowTo = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "allowTo"));
		OWLObjectProperty notificationIsOfService = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "notificationIsOfService"));
		OWLObjectProperty isOfEntity = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "isOfEntity"));
		OWLObjectProperty position = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "position"));

		OWLNamedIndividual owlTrigger = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(trigger.getURL()));

		OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
		
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
		
		Set<OWLNamedIndividual> owlNotifications = userReasoner.getObjectPropertyValues(owlTrigger, allowTo).getFlattened();
		for (OWLNamedIndividual owlNotification : owlNotifications) {
			
			//getIoTEntity
			OWLNamedIndividual owlService = userReasoner.getObjectPropertyValues(owlNotification, notificationIsOfService).getFlattened().iterator().next();
			OWLNamedIndividual owlEntity = userReasoner.getObjectPropertyValues(owlService, isOfEntity).getFlattened().iterator().next();

			//check IoTEntitiy position
			if(location != null) {
				OWLNamedIndividual owlLocation = userReasoner.getObjectPropertyValues(owlEntity, position).getFlattened().iterator().next();
				if(owlLocation != null && ! owlLocation.getIRI().toString().equals(location.getURL()))
					continue;
			}
			
			Notification notification = new Notification();
			notification.setURL(this.prefix.getShortForm(owlNotification.getIRI()).substring(1));
			try{notification.setId(userReasoner.getDataPropertyValues(owlNotification,id).iterator().next().getLiteral());}catch(Throwable t){}
			
			
			notification.setEntitiy(this.getIoTEntityByUrl(owlEntity.getIRI().toString(), username));
		}
		
		
		return notifications;		
		
	}
	
	@Override
	public List<Service> getServicesByIoTEntity(String url, String username) throws OWLOntologyCreationException {
		List<Service> services = new LinkedList<Service>();
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
		
		OWLObjectProperty hasTechnology = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasTechnology"));
		OWLNamedIndividual owlEntity = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(url));

		OWLDataProperty image = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "image"));
		OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));
		OWLDataProperty id = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "id"));
		OWLDataProperty color = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "color"));
		
		Set<OWLNamedIndividual> owlServices = userReasoner.getObjectPropertyValues(owlEntity,hasTechnology).getFlattened();
		for(OWLNamedIndividual owlService : owlServices){
			Service service = new Service();
			service.setURL(owlService.getIRI().toString());
			service.setId(prefix.getShortForm(owlService.getIRI()).substring(1));
			
			try{service.setName(userReasoner.getDataPropertyValues(owlService,name).iterator().next().getLiteral());}catch(Throwable t){}
			try{service.setWebId(Long.valueOf(userReasoner.getDataPropertyValues(owlService,id).iterator().next().getLiteral()));}catch(Throwable t){}
			try{service.setColor(userReasoner.getDataPropertyValues(owlService,color).iterator().next().getLiteral());}catch(Throwable t){}
			try{service.setImage(userReasoner.getDataPropertyValues(owlService,image).iterator().next().getLiteral());}catch(Throwable t){}
			services.add(service);
			
		}
		
		return services;
	}


	@Override
	public Collection<IoTEntity> getIoTEntitiesByService(String service, String username) throws OWLOntologyCreationException {
		
		OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));

		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}

		Set<IoTEntity> entities = new HashSet<IoTEntity>();
		OWLNamedIndividual owlService = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getDefaultPrefix() + service));
		OWLObjectProperty hasRegisteredEntity = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasRegisteredEntity"));
		Set<OWLNamedIndividual> owlEntities = userReasoner.getObjectPropertyValues(owlService, hasRegisteredEntity).getFlattened();
		for(OWLNamedIndividual owlEntity : owlEntities){
			IoTEntity e = new IoTEntity();
			try{e.setName(userReasoner.getDataPropertyValues(owlEntity,name).iterator().next().getLiteral());}catch(Throwable t){}
			e.setId(this.prefix.getShortForm(owlEntity.getIRI()).substring(1));
			e.setURL(owlEntity.getIRI().toString());
			entities.add(e);
		}

		return entities;
	}
	
	@Override
	public Collection<Trigger> getSupportedTriggers(String username) throws OWLOntologyCreationException, InterruptedException{
		Set<Trigger> triggers = new HashSet<Trigger>();
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}

		OWLNamedIndividual userIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") + username));
		OWLObjectProperty canControlProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "canControl"));
		OWLObjectProperty hasTechnologyProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasTechnology"));

		Set<OWLNamedIndividual> owlEntities = userReasoner.getObjectPropertyValues(userIndividual, canControlProp).getFlattened();
		
		for(OWLNamedIndividual owlEntity : owlEntities){
			Set<OWLNamedIndividual> owlServices = userReasoner.getObjectPropertyValues(owlEntity, hasTechnologyProp).getFlattened();
			for(OWLNamedIndividual owlService : owlServices){
				triggers.addAll(this.getTriggers(this.prefix.getShortForm(owlService.getIRI()).substring(1), username));
			}
		}
		
		return triggers;
	}
	
	@Override
	public Collection<Trigger> getSupportedTriggers(Collection<IoTEntity> entities,String username) throws OWLOntologyCreationException, InterruptedException{
		Set<Trigger> triggers = new HashSet<Trigger>();
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}

		OWLObjectProperty hasTechnologyProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasTechnology"));

		Set<OWLNamedIndividual> owlEntities = new HashSet<OWLNamedIndividual>();
		for(IoTEntity entity : entities) {
			owlEntities.add(this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(entity.getURL())));
		}
				
		for(OWLNamedIndividual owlEntity : owlEntities){
			Set<OWLNamedIndividual> owlServices = userReasoner.getObjectPropertyValues(owlEntity, hasTechnologyProp).getFlattened();
			for(OWLNamedIndividual owlService : owlServices){
				triggers.addAll(this.getTriggers(this.prefix.getShortForm(owlService.getIRI()).substring(1), username));
			}
		}
		
		return triggers;
	}
	
	@Override
	public Collection<Action> getSupportedActions(String username) throws OWLOntologyCreationException, InterruptedException{
		Set<Action> actions = new HashSet<Action>();
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}

		OWLNamedIndividual userIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") + username));
		OWLObjectProperty canControlProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "canControl"));
		OWLObjectProperty hasTechnologyProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasTechnology"));

		Set<OWLNamedIndividual> owlEntities = userReasoner.getObjectPropertyValues(userIndividual, canControlProp).getFlattened();
		
		for(OWLNamedIndividual owlEntity : owlEntities){
			Set<OWLNamedIndividual> owlServices = userReasoner.getObjectPropertyValues(owlEntity, hasTechnologyProp).getFlattened();
			for(OWLNamedIndividual owlService : owlServices){
				actions.addAll(this.getActions(this.prefix.getShortForm(owlService.getIRI()).substring(1), username));
			}
		}
		
		return actions;
	}
	
	@Override
	public Collection<Action> getSupportedActions(Collection<IoTEntity> entities, String username)
			throws OWLOntologyCreationException, InterruptedException {
		Set<Action> actions = new HashSet<Action>();
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}

		OWLObjectProperty hasTechnologyProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasTechnology"));

		Set<OWLNamedIndividual> owlEntities = new HashSet<OWLNamedIndividual>();
		for(IoTEntity entity : entities) {
			owlEntities.add(this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(entity.getURL())));
		}
				
		for(OWLNamedIndividual owlEntity : owlEntities){
			Set<OWLNamedIndividual> owlServices = userReasoner.getObjectPropertyValues(owlEntity, hasTechnologyProp).getFlattened();
			for(OWLNamedIndividual owlService : owlServices){
				actions.addAll(this.getActions(this.prefix.getShortForm(owlService.getIRI()).substring(1), username));
			}
		}
		return actions;
	}
	
	
	@Override
	public Collection<IoTEntity> getIoTEntitiesByServices(List<Service> services, String username) throws OWLOntologyCreationException {
		OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));

		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}

		Set<IoTEntity> entities = new HashSet<IoTEntity>();
		for(Service service : services){
			OWLNamedIndividual owlService = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getDefaultPrefix() + service.getId()));
			OWLObjectProperty hasRegisteredEntity = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasRegisteredEntity"));
			Set<OWLNamedIndividual> owlEntities = userReasoner.getObjectPropertyValues(owlService, hasRegisteredEntity).getFlattened();
			for(OWLNamedIndividual owlEntity : owlEntities){
				IoTEntity e = new IoTEntity();
				try{e.setName(userReasoner.getDataPropertyValues(owlEntity,name).iterator().next().getLiteral());}catch(Throwable t){}
				e.setId(this.prefix.getShortForm(owlEntity.getIRI()).substring(1));
				e.setURL(owlEntity.getIRI().toString());
				entities.add(e);
			}
		}
		return entities;
	}

	
	
	@Override
	public boolean triggers(Action action, Trigger trigger) throws InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		try{
			OWLNamedIndividual owlAction = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(action.getURL()));
			OWLObjectProperty owlTriggersProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "triggers"));
			
			Set<OWLNamedIndividual> owlTriggers = reasoner.getObjectPropertyValues(owlAction, owlTriggersProp).getFlattened();
			for(OWLNamedIndividual owlTrigger : owlTriggers){
				if(owlTrigger.getIRI().toString().equals(trigger.getURL()))
					return true;
			}
		}
		finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return false;
	}
	
	@Override
	public void saveRule(Rule rule) {
		
		
	}
	/**
	 * This method returns a Technology object that contains all the details of the specified technology
	 * 
	 * @param technologyName the name of the technology
	 * @return Technology
	 * @throws InterruptedException 
	 */
	@Override
	public Technology getTechnologyFromName(String technologyName) throws InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		Technology technology = new Technology();

		try{
			logger.info("Get the technology named " + technologyName);
	
			if(!this.baseOntology.containsEntityInSignature(IRI.create(this.prefix.getDefaultPrefix() + technologyName)))
				return null;
			
			//Get onological entitities
			OWLClass technologyClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getDefaultPrefix() + technologyName));
			OWLObjectProperty offerAction = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerAction"));
			OWLObjectProperty offerTrigger = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerTrigger"));
			OWLObjectProperty offerDetail = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "offerDetail"));
			OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getPrefix("foaf:") + "name"));
			OWLDataProperty description = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getPrefix("eupont:") + "description"));
			
			technology.setId(technologyName);
			technology.setURL(this.prefix.getDefaultPrefix() + technologyName);
			
			Set<Service> services = new HashSet<Service>();
			//Get services
			Set<OWLNamedIndividual> technologyServices = reasoner.getInstances(technologyClass, false).getFlattened();
			for(OWLNamedIndividual technologyService : technologyServices){
				Service service = new Service();
				service.setId(this.prefix.getShortForm(technologyService).substring(1));
				service.setURL(technologyService.getIRI().toString());
				Set<Action> actions = new HashSet<Action>();
				Set<Trigger> triggers = new HashSet<Trigger>();
				
				//Get offered triggers
				Set<OWLNamedIndividual> serviceTriggers = reasoner.getObjectPropertyValues(technologyService, offerTrigger).getFlattened();
				//Get offered actions
				Set<OWLNamedIndividual> serviceActions = reasoner.getObjectPropertyValues(technologyService, offerAction).getFlattened();
				
				for(OWLNamedIndividual serviceTrigger : serviceTriggers){
					Trigger trigger = new Trigger();
					trigger.setId(this.prefix.getShortForm(serviceTrigger));
					trigger.setURL(serviceTrigger.getIRI().toString());
					String triggerDescription = null;
					try{triggerDescription = reasoner.getDataPropertyValues(serviceTrigger, description).iterator().next().getLiteral();}catch(Throwable t){}
					if(triggerDescription != null)
						trigger.setDescription(triggerDescription);
					//Get trigger details
					Set<OWLNamedIndividual> triggerDetails = reasoner.getObjectPropertyValues(serviceTrigger, offerDetail).getFlattened();
					List<Detail> details = new LinkedList<Detail>();
					for(OWLNamedIndividual triggerDetail : triggerDetails){
						Detail detail = new Detail();
						detail.setId(this.prefix.getShortForm(triggerDetail));
						detail.setURL(triggerDetail.getIRI().toString());
						String detailName = null;
						try{detailName = reasoner.getDataPropertyValues(triggerDetail, name).iterator().next().getLiteral();}catch(Throwable t){}
						if(detailName == null)
							detail.setName(detailName);
						details.add(detail);
					}
					trigger.setDetails(details);
					triggers.add(trigger);
				}
				
				for(OWLNamedIndividual serviceAction : serviceActions){
					Action action = new Action();
					action.setId(this.prefix.getShortForm(serviceAction));
					action.setURL(serviceAction.getIRI().toString());
					String actionDescription = null;
					try{actionDescription = reasoner.getDataPropertyValues(serviceAction, description).iterator().next().getLiteral();}catch(Throwable t){}
					if(actionDescription != null)
						action.setDescription(actionDescription);
					//Get action details
					Set<OWLNamedIndividual> actionDetails = reasoner.getObjectPropertyValues(serviceAction, offerDetail).getFlattened();
					List<Detail> details = new LinkedList<Detail>();
					for(OWLNamedIndividual actionDetail : actionDetails){
						Detail detail = new Detail();
						detail.setId(this.prefix.getShortForm(actionDetail));
						detail.setURL(actionDetail.getIRI().toString());
						String detailName = null;
						try{detailName = reasoner.getDataPropertyValues(actionDetail, name).iterator().next().getLiteral();}catch(Throwable t){}
						if(detailName == null)
							detail.setName(detailName);
						details.add(detail);
					}
					action.setDetails(details);
					actions.add(action);
				}
				
				service.setActions(actions);
				service.setTriggers(triggers);
				services.add(service);
			}
			technology.setServices(services);
			logger.info("Technology succesfully retrieved");
		}
		finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return technology;
	}


	@Override
	public void deleteAllEntities(String username) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
		OWLOntology userOntology = this.manager.getOntology(userIRI);
		if(userOntology == null){
			userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
		}
		List<Dbentity> dbentities = this.dbService.getEntities(username);
		OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(userOntology));

		for(Dbentity e : dbentities) {
			OWLNamedIndividual ind = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(e.getUrl()));
			remover.visit(ind);
		}
		manager.applyChanges(remover.getChanges());

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(prop.getProperty("baseAddress") + prop.getProperty("ontologyDir") + username + ".owl");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));	
		manager.saveOntology(userOntology, outputStream);
	}
	
	/**
	 * This method save an IoT Entity in the ontology
	 * 
	 *  @param entity the IoT entity
	 *  @param username the username of the user
	 *  @return IoTEntity the saved IoT Entity object
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 * @throws FileNotFoundException 
	 */
	@Override
	public IoTEntity createEntity(IoTEntity entity, String username) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
		OWLOntology userOntology = this.manager.getOntology(userIRI);
		if(userOntology == null){
			userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
		}
		OWLNamedIndividual entityIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") + entity.getId()));
		OWLNamedIndividual userIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") + username));
		OWLClass entityClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + entity.getType()));
		OWLObjectProperty hasTechnologyProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasTechnology"));
		OWLObjectProperty canControlProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "canControl"));
		OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));

		OWLClassAssertionAxiom entityClassAxiom = this.manager.getOWLDataFactory().getOWLClassAssertionAxiom(entityClass, entityIndividual);
		this.manager.applyChange(new AddAxiom(userOntology, entityClassAxiom));
		
		OWLObjectPropertyAssertionAxiom entityUserPropAxiom = this.manager.getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(canControlProp, userIndividual, entityIndividual);
		this.manager.applyChange(new AddAxiom(userOntology, entityUserPropAxiom));
		
		OWLDataPropertyAssertionAxiom entityNamePropAxiom = this.manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(name, entityIndividual, entity.getName());
		this.manager.applyChange(new AddAxiom(userOntology, entityNamePropAxiom));

		
		if(entity.getServices() != null){
			for(String service : entity.getServices()){
				OWLNamedIndividual serviceIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(service));
				OWLObjectPropertyAssertionAxiom entityServicePropAxiom = this.manager.getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(hasTechnologyProp,entityIndividual,serviceIndividual);
				this.manager.applyChange(new AddAxiom(userOntology, entityServicePropAxiom));
			}
		}
				
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(prop.getProperty("baseAddress") + prop.getProperty("ontologyDir") + username + ".owl");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));	
		manager.saveOntology(userOntology, outputStream);
	
		//manager.saveOntology(userOntology);
		/*OWLReasoner userReasoner = this.userReasoners.get(username);
		if(userReasoner == null){
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			userReasoner.precomputeInferences(InferenceType.values());
			this.userReasoners.put(username, userReasoner);
		}*/
		entity.setURL(entityIndividual.getIRI().toString());
		prop.setProperty("extractOwl", String.valueOf(true));
		
		return entity;
	}
	
	/**
	 * This method save an IoT Entity in the ontology
	 * 
	 *  @param entity the IoT entity
	 *  @param location the location of the entity
	 *  @param username the username of the user
	 *  @return IoTEntity the saved IoT Entity object
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 * @throws FileNotFoundException 
	 */
	@Override
	public IoTEntity createEntity(IoTEntity entity, String location, String username) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
		OWLOntology userOntology = this.manager.getOntology(userIRI);
		if(userOntology == null){
			userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
		}
		OWLNamedIndividual entityIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") + entity.getId()));
		OWLNamedIndividual userIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") + username));
		
		OWLNamedIndividual locationIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") + location));

		
		OWLClass entityClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + entity.getType()));
		OWLObjectProperty hasTechnologyProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasTechnology"));
		OWLObjectProperty canControlProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "canControl"));
		OWLObjectProperty locationProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "location"));
		
		OWLDataProperty name = this.manager.getOWLDataFactory().getOWLDataProperty(IRI.create(this.prefix.getDefaultPrefix() + "name"));

		OWLClassAssertionAxiom entityClassAxiom = this.manager.getOWLDataFactory().getOWLClassAssertionAxiom(entityClass, entityIndividual);
		this.manager.applyChange(new AddAxiom(userOntology, entityClassAxiom));
		
		OWLObjectPropertyAssertionAxiom entityUserPropAxiom = this.manager.getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(canControlProp, userIndividual, entityIndividual);
		this.manager.applyChange(new AddAxiom(userOntology, entityUserPropAxiom));
		
		OWLDataPropertyAssertionAxiom entityNamePropAxiom = this.manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(name, entityIndividual, entity.getName());
		this.manager.applyChange(new AddAxiom(userOntology, entityNamePropAxiom));

		
		if(entity.getServices() != null){
			for(String service : entity.getServices()){
				OWLNamedIndividual serviceIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(service));
				OWLObjectPropertyAssertionAxiom entityServicePropAxiom = this.manager.getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(hasTechnologyProp,entityIndividual,serviceIndividual);
				this.manager.applyChange(new AddAxiom(userOntology, entityServicePropAxiom));
			}
		}
		
		OWLObjectPropertyAssertionAxiom entityLocationPropAxiom = this.manager.getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(locationProp,entityIndividual,locationIndividual);
		this.manager.applyChange(new AddAxiom(userOntology, entityLocationPropAxiom));
				
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(prop.getProperty("baseAddress") + prop.getProperty("ontologyDir") + username + ".owl");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));	
		manager.saveOntology(userOntology, outputStream);
	
		//manager.saveOntology(userOntology);
		/*OWLReasoner userReasoner = this.userReasoners.get(username);
		if(userReasoner == null){
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			userReasoner.precomputeInferences(InferenceType.values());
			this.userReasoners.put(username, userReasoner);
		}*/
		entity.setURL(entityIndividual.getIRI().toString());
		prop.setProperty("extractOwl", String.valueOf(true));
		
		return entity;
	}

	
	
	private void addClass(String id, String cl, String username) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
	
		IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
		OWLOntology userOntology = this.manager.getOntology(userIRI);
		if(userOntology == null){
			userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
		}
		OWLNamedIndividual entityIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") +id));
		OWLClass entityClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + cl));
	
		OWLClassAssertionAxiom entityClassAxiom = this.manager.getOWLDataFactory().getOWLClassAssertionAxiom(entityClass, entityIndividual);
		this.manager.applyChange(new AddAxiom(userOntology, entityClassAxiom));
		
					
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(prop.getProperty("baseAddress") + prop.getProperty("ontologyDir") + username + ".owl");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));	
		manager.saveOntology(userOntology, outputStream);
	}


	

	@Override
	public boolean equalsML(Action action1, Action action2) {
		OWLNamedIndividual owlAction1 = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(action1.getURL()));
		OWLNamedIndividual owlAction2 = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(action2.getURL()));

		Collection<OWLClassExpression> action1Types = (Collection<OWLClassExpression>) EntitySearcher.getTypes(owlAction1, this.baseOntology);
		Collection<OWLClassExpression> action2Types = (Collection<OWLClassExpression>) EntitySearcher.getTypes(owlAction2, this.baseOntology);
		
		for(OWLClassExpression c1 : action1Types){
			for(OWLClassExpression c2 : action2Types){
				if(c1.equals(c2))
					return true;
			}
		}
		return false;
	}


	@Override
	public Set<OWLClass> getSubClasses(String className) throws InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		Set<OWLClass> classes = new HashSet<OWLClass>();
		try{
			OWLClass owlClass = this.manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix.getPrefix("eupont:") + className));
			classes.addAll(reasoner.getSubClasses(owlClass, false).getFlattened());
		}
		finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return classes;		
	}


	@Override
	public List<String> getClasses(String individualURL) throws InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		List<String> classes = new LinkedList<String>();
		try{
			OWLNamedIndividual owlIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getDefaultPrefix() + individualURL.replace("_", "")));
			Set<OWLClass> owlClasses = reasoner.getTypes(owlIndividual, false).getFlattened();
			for(OWLClass owlClass : owlClasses){
				classes.add(owlClass.getIRI().toString());
			}

		}finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		return classes;
	}
	
	public List<String> getClasses(String individualURL, String username) throws OWLOntologyCreationException {
		OWLReasoner userReasoner = this.userReasoners.get(username);
		List<String> classes = new LinkedList<String>();

		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}
			
		OWLNamedIndividual owlIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(individualURL));

		Set<OWLClass> owlClasses = userReasoner.getTypes(owlIndividual, false).getFlattened();
		for(OWLClass owlClass : owlClasses){
			classes.add(owlClass.getIRI().toString());
		}
		return classes;
	}


	@Override
	public String getTechnology(String url, int type) throws InterruptedException {
		OWLReasoner reasoner = this.reasoners.take();
		Set<OWLNamedIndividual> results;
		try{
			OWLNamedIndividual ind = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getDefaultPrefix() + url.replace("_", "")));
			
			OWLObjectProperty op;
			if(type == 1)
				op = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "isOfChannel"));
			else
				op = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "isOfChannel"));
			
			results = reasoner.getObjectPropertyValues(ind, op).getFlattened();

			
			
		}finally{
			if(reasoner != null) this.reasoners.put(reasoner);
		}
		
		if(results.iterator().hasNext())
			return prefix.getShortForm(results.iterator().next()).substring(1);
		else 
			return null;
	}


	@Override
	public Collection<IoTEntity> getIoTEntities(String username) throws OWLOntologyCreationException {
		Set<IoTEntity> entities = new HashSet<IoTEntity>();
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		if(userReasoner == null){
			IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
			OWLOntology userOntology = this.manager.getOntology(userIRI);
			if(userOntology == null)
				userOntology = this.loadFromFile(username + ".owl");
			this.prefix.setPrefix(username + ":", userIRI + "#");
			userReasoner = this.initHermiTReasoner(userOntology);
			this.userReasoners.put(username, userReasoner);
		}

		OWLNamedIndividual userIndividual = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(this.prefix.getPrefix(username + ":") + username));
		OWLObjectProperty canControlProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "canControl"));
		//OWLObjectProperty hasTechnologyProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "hasTechnology"));

		Set<OWLNamedIndividual> owlEntities = userReasoner.getObjectPropertyValues(userIndividual, canControlProp).getFlattened();
		for(OWLNamedIndividual e : owlEntities)
			entities.add(this.getIoTEntityByUrl(e.getIRI().toString(), username));
		return entities;
	}


	@Override
	public List<String> getLocation(IoTEntity e, String username) {
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		try {
			if(userReasoner == null){
				IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
				OWLOntology userOntology = this.manager.getOntology(userIRI);
				if(userOntology == null)
					userOntology = this.loadFromFile(username + ".owl");
				this.prefix.setPrefix(username + ":", userIRI + "#");
				userReasoner = this.initHermiTReasoner(userOntology);
				this.userReasoners.put(username, userReasoner);
			}
			OWLObjectProperty isInProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "location"));
			OWLNamedIndividual owlEntity = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(e.getURL()));
			
			Set<OWLNamedIndividual> owlLocations = userReasoner.getObjectPropertyValues(owlEntity, isInProp).getFlattened();
			
			if(owlLocations == null || owlLocations.size() == 0)
				return null;
			
			List<String> locations = new ArrayList<String>();
			for(OWLNamedIndividual owlLocation : owlLocations) {
				locations.add(owlLocation.getIRI().toString());
			}
			return locations;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean isIn(IoTEntity e, String triggerWhere, String username)  {
		OWLReasoner userReasoner = this.userReasoners.get(username);
		
		try {
			if(userReasoner == null){
				IRI userIRI = IRI.create(this.getBaseIRI() + "/" + username);
				OWLOntology userOntology = this.manager.getOntology(userIRI);
				if(userOntology == null)
					userOntology = this.loadFromFile(username + ".owl");
				this.prefix.setPrefix(username + ":", userIRI + "#");
				userReasoner = this.initHermiTReasoner(userOntology);
				this.userReasoners.put(username, userReasoner);
			}
			OWLObjectProperty isInProp = this.manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(this.prefix.getPrefix("eupont:") + "location"));
			OWLNamedIndividual owlEntity = this.manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(e.getURL()));
			
			Set<OWLNamedIndividual> owlLocations = userReasoner.getObjectPropertyValues(owlEntity, isInProp).getFlattened();
			
			for(OWLNamedIndividual owlLocation : owlLocations) {
				
				for(String cl : this.getClasses(owlLocation.getIRI().toString(), username)) {
					if(cl.replace(" ","").toLowerCase().contains(triggerWhere.replace(" ","").toLowerCase()) || cl.replace(" ","").toLowerCase().equals(triggerWhere.replace(" ","").toLowerCase()))
						return true;
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
		return false;
	}


	@Override
	public boolean isA(String id, String cl)  {
		List<String> classes;
		try {
			classes = this.getClasses(id);
			for(String c : classes) {
				if(c.replace(" ","").toLowerCase().contains(cl.replace(" ","").toLowerCase()) || c.replace(" ","").toLowerCase().equals(cl.replace(" ","").toLowerCase()))
					return true;
			}
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean isA(String url, String cl, String username)  {
		List<String> classes;
		try {
			classes = this.getClasses(url,username);
			for(String c : classes) {
				if(c.replace(" ","").toLowerCase().contains(cl.replace(" ","").toLowerCase()) || c.replace(" ","").toLowerCase().equals(cl.replace(" ","").toLowerCase()))
					return true;
			}
			return false;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}


	@Override
	public void registerDemoPlot() throws OWLOntologyCreationException, OWLOntologyStorageException,
			FileNotFoundException, InterruptedException {
		// TODO Auto-generated method stub
	}




}
