package it.polito.elite.sifis.controllers;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.elite.sifis.entities.db.Dbrule;
import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Detail;
import it.polito.elite.sifis.entities.owl.IoTEntity;
import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.owl.RuleProblems;
import it.polito.elite.sifis.entities.owl.Service;
import it.polito.elite.sifis.entities.owl.Trigger;
import it.polito.elite.sifis.entities.petrinet.ActionPlace;
import it.polito.elite.sifis.entities.petrinet.ExtendedPetriNet;
import it.polito.elite.sifis.entities.petrinet.Place;
import it.polito.elite.sifis.entities.petrinet.TriggerPlace;
import it.polito.elite.sifis.entities.xacml.PolicyType;
import it.polito.elite.sifis.services.DBService;
import it.polito.elite.sifis.services.OWLService;
import it.polito.elite.sifis.services.PetriNetService;
import it.polito.elite.sifis.services.UserService;
import it.polito.elite.sifis.services.XACMLService;
import it.polito.elite.sifis.utils.PropertyFileReader;

@RestController
@CrossOrigin("*")
public class SifisController {
	
	private static String configFile = "config.properties";
	Properties prop;

	
	@Autowired
	OWLService owlService;
	
	@Autowired
	XACMLService xacmlService;
	
	
	@Autowired
	DBService dbService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PetriNetService petriNetService;
	
	@GetMapping(value = "/sifis/triggerservice")
	public List<Service> getTriggerServices() throws InterruptedException{
		Collection<Service> services = owlService.getTriggerServices();
		List<Service> serviceList = new LinkedList<Service>(services);
		Collections.sort(serviceList);
		return serviceList;
	}
	
	@GetMapping(value = "/sifis/triggerservice/{service}/triggers")
	public List<Trigger> getTriggers(@PathVariable String service, Principal user) throws InterruptedException, OWLOntologyCreationException{
		return new LinkedList<Trigger>(owlService.getTriggers(service, user.getName()));
	}
	
	@GetMapping(value = "/sifis/triggerservice/{service}/triggers/{trigger}/details")
	public List<Detail> getTriggerDetails(@PathVariable String service, @PathVariable String trigger, Principal user) throws OWLOntologyCreationException, InterruptedException{
		return new LinkedList<Detail>(owlService.getTriggerDetails(null, service, trigger, user.getName()));
	}
	
	@GetMapping(value = "/sifis/actionservice")
	public List<Service> getActionServices() throws InterruptedException{
		Collection<Service> services = owlService.getActionServices();
		List<Service> serviceList = new LinkedList<Service>(services);
		Collections.sort(serviceList);
		return serviceList;
	}
	
	@GetMapping(value = "/sifis/actionservice/{service}/actions")
	public List<Action> getActions(@PathVariable String service, Principal user) throws InterruptedException, OWLOntologyCreationException{
		return new LinkedList<Action>(owlService.getActions(service, user.getName()));
	}
	
	@GetMapping(value = "/sifis/actionservice/{service}/actions/{action}/details")
	public List<Detail> getActionDetails(@PathVariable String service, @PathVariable String action, Principal user) throws OWLOntologyCreationException, InterruptedException{
		return new LinkedList<Detail>(owlService.getActionDetails(null, service, action, user.getName()));
	}
	
	@DeleteMapping(value = "/sifis/rule/{ruleId}")
	@ResponseStatus(value = HttpStatus.OK)
	public Long deleteRule(@PathVariable Long ruleId, Principal user){
		dbService.deleteRule(ruleId);
		return ruleId;
	}
	
	@GetMapping(value = "/sifis/rule")
	public List<Rule> getRules(Principal user) throws OWLOntologyCreationException, InterruptedException {
		List<Rule> rules = dbService.getRulesByType("sifis",user.getName());
		
		
		return rules;
	}
	
	@GetMapping(value = "/sifis/rule/trigger")
	public Set<Trigger> getDefinedTriggers(Principal user) throws OWLOntologyCreationException, InterruptedException {
		return dbService.getDefinedTriggersByType("sifis",user.getName());
	}
	
	@PostMapping(value = "/sifis/rule")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveRule(@RequestBody Rule rule, Principal user) throws OWLOntologyCreationException{
		for(Detail d : rule.getAction().getDetails()){
			if(d.getType().equals("entity")){
				ObjectMapper mapper = new ObjectMapper();
				IoTEntity entity = mapper.convertValue(d.getValue(), IoTEntity.class);
				d.setValue(entity);
			}
		}
		for(Detail d : rule.getTrigger().getDetails()){
			if(d.getType().equals("entity")){
				ObjectMapper mapper = new ObjectMapper();
				IoTEntity entity = mapper.convertValue(d.getValue(), IoTEntity.class);
				d.setValue(entity);
			}
		}
		Dbrule dbrule = dbService.saveRule(rule, "sifis", user.getName());
		rule.setDbId(dbrule.getId());
	}
	
	@PostMapping(value = "/sifis/translate")
	@ResponseStatus(value = HttpStatus.OK)
	public void translate(HttpServletResponse response, @RequestBody Rule rule, Principal user) throws OWLOntologyCreationException, InterruptedException, JAXBException, IOException {
		prop = PropertyFileReader.loadProperties(configFile);

		List<PolicyType> policies = xacmlService.translate(rule, user.getName());
		
		JAXBContext jaxbContext = JAXBContext.newInstance(PolicyType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		
		for(PolicyType policy : policies) {
		    jaxbMarshaller.marshal(policy, new File(prop.getProperty("baseAddress") + prop.getProperty("policiesDir") + policy.getPolicyId()+ ".xml"));
		}
	}
	
	@PostMapping(value = "/sifis/check")
	@ResponseStatus(value = HttpStatus.OK)
	public RuleProblems checkRule(@RequestBody Rule rule, Principal user) throws OWLOntologyCreationException, InterruptedException{
		RuleProblems ruleProblems = new RuleProblems();
		
		List<Rule> rulesBefore = new ArrayList<Rule>();
		List<Rule> rulesAfter = dbService.getRulesByType("sifis", user.getName());
		for (Rule r : rulesAfter) {
			if(r.getDbId() != rule.getDbId()) {
				rulesBefore.add(r);
			} else {
				ruleProblems.setRule(r);
			}
		}

		ExtendedPetriNet net = (ExtendedPetriNet) petriNetService.getNet(user.getName(),rulesAfter,true);
		
		//search for loops
		List<List<Place>> cycles = net.getCycle(rule, rulesAfter);
		
		if(cycles != null && cycles.size() > 0){
			//there are cycles!
			
			for(List<Place> cycle : cycles){
				List<Rule> loop = new LinkedList<Rule>();
				
				Collections.reverse(cycle);
				Iterator<Place> it = cycle.iterator();
				boolean involved = false;
				while(it.hasNext()){
					Rule r = new Rule();
					r.setTrigger(((TriggerPlace)it.next()).getTrigger());
					r.setAction(((ActionPlace)it.next()).getAction());
					
					for(Rule ra : rulesAfter){
						if(r.getTrigger().equals(ra.getTrigger()) && r.getAction().equals(ra.getAction())){
							loop.add(ra);
							if(r.getTrigger().equals(rule.getTrigger()) && r.getAction().equals(rule.getAction())){
								involved = true;
							}	
						}
					}
				}
				//Collections.reverse(loop);
				//return the loop only if the saved rule is involved
				if(involved)
					ruleProblems.addLoop(loop);
			}
		}
		if(ruleProblems.getLoops() == null){
			//search for inconsistencies and redundancies
		
			Trigger startTrigger;
			if(ruleProblems.getLoops() == null)
				startTrigger = net.getRoot(rule);
			else
				startTrigger = rule.getTrigger();
			
			ruleProblems.setStartTrigger(startTrigger);
			Rule[] simulatedRules = net.getSimualtedRules(startTrigger);
			List<Rule> simulatedRulesList = Arrays.asList(simulatedRules);
			if(simulatedRulesList.size() > 40){
				simulatedRulesList = simulatedRulesList.subList(0, 40);
				simulatedRules = new Rule[40];
				int i = 0;
				for(Rule r : simulatedRulesList)
					simulatedRules[i++] = r;
			}
			
			//Action ruleAction = rule.getAction();
			
			for(int i = 0; i < simulatedRules.length; i++) {
				Rule r1 = simulatedRules[i];
				Action a1 = simulatedRules[i].getAction();
				List<Rule> redundant = new LinkedList<Rule>();
				List<Rule> inconsistent = new LinkedList<Rule>();
				inconsistent.add(r1);
				redundant.add(r1);
				
				for(int j = 0; j < simulatedRules.length && j<100; j++){
					if(j!=i){
						Rule r2 = simulatedRules[j];
						Action a2 = simulatedRules[j].getAction();
						
						if(owlService.equalsML(a1,a2)){
							redundant.add(r2);
						}
						else{
							inconsistent.add(r2);
						}
						
					}
				}
				
				if(inconsistent != null && inconsistent.size() > 1 && !ruleProblems.containsInconsistency(inconsistent)) ruleProblems.addInconsistentRules(inconsistent);
				if(redundant != null && redundant.size() > 1 && !ruleProblems.containsRedundancy(redundant)) ruleProblems.addRedundantRules(redundant);
				Trigger lastTrigger = simulatedRules[0].getTrigger();
				List<Rule> temp = new LinkedList<Rule>();

				for(Rule simulatedRule : simulatedRulesList){
					
					if(simulatedRule.getTrigger().equals(lastTrigger)){
						temp.add(simulatedRule);
						continue;
					}
					simulatedRule.setParents(petriNetService.getParents(simulatedRule, temp));
					temp.add(simulatedRule);
					lastTrigger = simulatedRule.getTrigger();
				}
				
			
				ruleProblems.setExecutedRules(simulatedRulesList);		
			}
		}
			
		

		return ruleProblems;
	}
	

}
