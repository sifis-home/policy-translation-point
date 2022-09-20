package it.polito.elite.sifis.services;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.elite.sifis.entities.db.Dbaction;
import it.polito.elite.sifis.entities.db.Dbdetail;
import it.polito.elite.sifis.entities.db.Dbentity;
import it.polito.elite.sifis.entities.db.Dbrule;
import it.polito.elite.sifis.entities.db.Dbtrigger;
import it.polito.elite.sifis.entities.db.User;
import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Detail;
import it.polito.elite.sifis.entities.owl.IoTEntity;
import it.polito.elite.sifis.entities.owl.Location;
import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.owl.Service;
import it.polito.elite.sifis.entities.owl.Trigger;
import it.polito.elite.sifis.repositories.ActionRepository;
import it.polito.elite.sifis.repositories.DetailRepository;
import it.polito.elite.sifis.repositories.EntityRepository;
import it.polito.elite.sifis.repositories.RuleRepository;
import it.polito.elite.sifis.repositories.TriggerRepository;

public class DBServiceImpl implements DBService {

	@Autowired
	UserService userService;
	@Autowired
	EntityRepository entityRepository;
	@Autowired
	RuleRepository ruleRepository;
	@Autowired
	TriggerRepository triggerRepository;
	@Autowired
	ActionRepository actionRepository;
	@Autowired
	DetailRepository detailRepository;
	@Autowired
	OWLService owlService;
	
	
	@Override
	public Dbentity saveEntity(Dbentity entity) {
		return entityRepository.save(entity);
	}

	@Override
	public Dbrule saveRule(Rule rule, String type, String username) throws OWLOntologyCreationException {
		Dbrule r = new Dbrule();
		r.setTimestamp(rule.getTimestamp());
		r.setType(type);
		User u = userService.findUserByUsername(username);
		User ruleUser = new User();
		ruleUser.setId(u.getId());
		r.setUser(ruleUser);
		List<Dbtrigger> ts = new LinkedList<Dbtrigger>();
		List<Dbaction> as = new LinkedList<Dbaction>();
		Dbtrigger t = new Dbtrigger();
		t.setUrl(rule.getTrigger().getURL());
		
		List<Dbdetail> tds = new LinkedList<Dbdetail>();
		for(Detail detail : rule.getTrigger().getDetails()){
			if(detail.getType().equals("entity")){
				ObjectMapper mapper = new ObjectMapper();
				IoTEntity entity = mapper.convertValue(detail.getValue(), IoTEntity.class);
				if(entity != null){
					Dbentity dbentity = entityRepository.findByUrl(entity.getURL());
					Dbentity e = new Dbentity();
					e.setId(dbentity.getId());
					t.setEntity(e);
				}
			}
			else if(detail.getType().equals("location")) {
				ObjectMapper mapper = new ObjectMapper();
				Location location = mapper.convertValue(detail.getValue(), Location.class);
				if(location != null) {
					Dbdetail d = new Dbdetail();
					d.setTrigger(t);
					d.setType(detail.getType());
					d.setUrl(detail.getURL());
					d.setValue(location.getName());
					d.setValueURL(location.getURL());
					tds.add(d);
				}
			}
			else if(detail.getValue()!= null){
				Dbdetail d = new Dbdetail();
				d.setTrigger(t);
				d.setType(detail.getType());
				d.setUrl(detail.getURL());
				d.setValue(detail.getValue().toString());
				tds.add(d);
			}
		}
		t.setDetails(tds);
		
		if(t.getEntity() == null){
			Collection<IoTEntity> entities = owlService.getIoTEntitiesByService(rule.getTrigger().getService().getId(), username);
			Dbentity dbentity = null;
			try{dbentity = entityRepository.findByUrl(entities.iterator().next().getURL());}catch(Throwable tr){};
			if(dbentity != null){
				Dbentity e = new Dbentity();
				e.setId(dbentity.getId());
				t.setEntity(e);
			}
		}
		
		Dbaction a = new Dbaction();
		a.setUrl(rule.getAction().getURL());
		
		List<Dbdetail> ads = new LinkedList<Dbdetail>();
		for(Detail detail : rule.getAction().getDetails()){
			if(detail.getType().equals("entity")){
				ObjectMapper mapper = new ObjectMapper();
				IoTEntity entity = mapper.convertValue(detail.getValue(), IoTEntity.class);
				if(entity != null){
					Dbentity dbentity = entityRepository.findByUrl(entity.getURL());
					Dbentity e = new Dbentity();
					e.setId(dbentity.getId());
					a.setEntity(e);
				}
			}
			else if(detail.getType().equals("location")) {
				ObjectMapper mapper = new ObjectMapper();
				Location location = mapper.convertValue(detail.getValue(), Location.class);
				if(location != null) {
					Dbdetail d = new Dbdetail();
					d.setAction(a);
					d.setType(detail.getType());
					d.setUrl(detail.getURL());
					d.setValue(location.getName());
					d.setValueURL(location.getURL());
					ads.add(d);
				}
			}
			else if(detail.getValue()!= null){
				Dbdetail d = new Dbdetail();
				d.setType(detail.getType());
				d.setUrl(detail.getURL());
				d.setValue(detail.getValue().toString());
				d.setAction(a);
				ads.add(d);
			}
		}
		a.setDetails(ads);
		
		if(a.getEntity() == null){
			Collection<IoTEntity> entities = owlService.getIoTEntitiesByService(rule.getAction().getService().getId(), username);
			Dbentity dbentity = null;
			try{dbentity = entityRepository.findByUrl(entities.iterator().next().getURL());}catch(Throwable tr){};
			if(dbentity != null){
				Dbentity e = new Dbentity();
				e.setId(dbentity.getId());
				a.setEntity(e);
			}
		}
		
	
		t.setRule(r);
		a.setRule(r);
		ts.add(t);
		as.add(a);
		r.setActions(as);
		r.setTriggers(ts);
		return ruleRepository.save(r);
	}


	@Override
	public List<Rule> getRulesByType(String type, String username) throws OWLOntologyCreationException, InterruptedException {
		List<Rule> rules = new LinkedList<Rule>();
		User u = userService.findUserByUsername(username);
		List<Dbrule> dbRules = ruleRepository.findByUserAndType(u,type);
		for(Dbrule dbRule : dbRules){
			Rule rule = new Rule();
			rule.setDbId(dbRule.getId());
			Trigger trigger = new Trigger();
			Action action = new Action();
			Dbtrigger dbTrigger = dbRule.getTriggers().iterator().next();
			Dbaction dbAction = dbRule.getActions().iterator().next();
			
			trigger = owlService.getTrigger(dbTrigger.getUrl(), username);
			action = owlService.getAction(dbAction.getUrl(), username);
			//boolean entityDetail = false;
			try{
				for(Detail d : trigger.getDetails()){
					if(d.getType().equals("entity")){
						//entityDetail = true;
						d.setValue(owlService.getIoTEntityByUrl(dbTrigger.getEntity().getUrl(), username));
					} else if(d.getType().equals("location")){
						Location loc = new Location();
						for(Dbdetail db : dbTrigger.getDetails()){
							if(d.getURL() != null && db.getUrl() != null && d.getURL().equals(db.getUrl()) && d.getType() != "entitiy"){
								loc.setName(db.getValue());
								loc.setURL(db.getValueURL());
								d.setValue(loc);
							}
						}						
					} else {
						for(Dbdetail db : dbTrigger.getDetails()){
							if(d.getURL() != null && db.getUrl() != null && d.getURL().equals(db.getUrl()) && d.getType() != "entitiy" &&  d.getType() != "location"){
								d.setValue(db.getValue());
							}
						}
					}
					
				}
				
				trigger.getDetails().sort(new Comparator<Detail>() {
					@Override
					public int compare(Detail o1, Detail o2) {
						return o1.getName().compareTo(o2.getName());
					}
					
				});
				/*if(!entityDetail){
					//create attribute to set the entity
					Detail detail = new Detail();
					detail.setName("Which entity?");
					detail.setType("entity");
					detail.setURL("entity_detail");
					detail.setValue(owlService.getIoTEntityByUrl(dbTrigger.getEntity().getUrl(), username));
					trigger.addDetail(detail);
				}*/
				
				//entityDetail = false;
				for(Detail d : action.getDetails()){
					if(d.getType().equals("entity")){
						//entityDetail = true;
						d.setValue(owlService.getIoTEntityByUrl(dbAction.getEntity().getUrl(), username));
					} else if(d.getType().equals("location")){
						Location loc = new Location();
						for(Dbdetail db : dbAction.getDetails()){
							if(d.getURL() != null && db.getUrl() != null && d.getURL().equals(db.getUrl()) && d.getType() != "entitiy"){
								loc.setName(db.getValue());
								loc.setURL(db.getValueURL());
								d.setValue(loc);
							}
						}						
					} else {
						for(Dbdetail db : dbAction.getDetails()){
							if(d.getURL() != null && db.getUrl() != null && d.getURL().equals(db.getUrl()) && d.getType() != "entitiy" &&  d.getType() != "location"){
								d.setValue(db.getValue());
							}
						}
					}
				}
				action.getDetails().sort(new Comparator<Detail>() {
					@Override
					public int compare(Detail o1, Detail o2) {
						return o1.getName().compareTo(o2.getName());
					}
					
				});
				/*if(!entityDetail){
					//create attribute to set the entity
					Detail detail = new Detail();
					detail.setName("Which entity?");
					detail.setType("entity");
					detail.setValue(owlService.getIoTEntityByUrl(dbAction.getEntity().getUrl(), username));
					action.addDetail(detail);
				}*/
			} catch(Throwable t) {}
			rule.setAction(action);
			rule.setTrigger(trigger);
			rule.setType(dbRule.getType());
			rule.setTimestamp(dbRule.getTimestamp());

			
			rules.add(rule);
			
		}
		return rules;
	}

	@Override
	public void deleteRule(Long ruleId) {
		Dbrule rule = ruleRepository.findById(ruleId).get();
		ruleRepository.delete(rule);
	}
	
	@Override
	public void deleteAllRules(String username) throws OWLOntologyCreationException, InterruptedException {
		User u = userService.findUserByUsername(username);
		List<Dbrule> rules = ruleRepository.findByUser(u);
		for(Dbrule r : rules) {
			ruleRepository.delete(r);
		}
	}
	@Override
	public List<Dbentity> getEntities(String username) {
		User u = userService.findUserByUsername(username);
		return this.entityRepository.findByUser(u);
	}

	@Override
	public void deleteAllEntities(String username) {
		User u = userService.findUserByUsername(username);
		List<Dbentity> entities = entityRepository.findByUser(u);
		for(Dbentity e : entities)
			entityRepository.delete(e);
	}

	@Override
	public Set<Trigger> getDefinedTriggersByType(String type, String username) throws OWLOntologyCreationException, InterruptedException {
		List<Rule> rules = this.getRulesByType(type, username);
		Set<Trigger> triggers = new HashSet<Trigger>();
		for(Rule rule : rules)
			triggers.add(rule.getTrigger());
		return triggers;
	}

	@Override
	public Rule getRule(Long ruleId, String username) throws OWLOntologyCreationException, InterruptedException {
		Dbrule dbRule = ruleRepository.findById(ruleId).get();
		Rule rule = new Rule();
		rule.setDbId(dbRule.getId());
		Trigger trigger = new Trigger();
		Action action = new Action();
		Dbtrigger dbTrigger = dbRule.getTriggers().iterator().next();
		Dbaction dbAction = dbRule.getActions().iterator().next();
		
		trigger = owlService.getTrigger(dbTrigger.getUrl(), username);
		action = owlService.getAction(dbAction.getUrl(), username);

		for(Dbdetail db : dbTrigger.getDetails()){
			for(Detail d : trigger.getDetails()){
				if(d.getType().equals("entity"))
					d.setValue(dbTrigger.getEntity().getUrl());
				if(d.getURL() != null && db.getUrl() != null && d.getURL().equals(db.getUrl())){
					d.setValue(db.getValue());
				}
			}
		}
		for(Dbdetail db : dbAction.getDetails()){
			for(Detail d : action.getDetails()){
				if(d.getType().equals("entity"))
					d.setValue(dbAction.getEntity().getUrl());
				else if(d.getURL() != null && db.getUrl() != null && d.getURL().equals(db.getUrl())){
					d.setValue(db.getValue());
				}
				
			}
		}
		rule.setAction(action);
		rule.setTrigger(trigger);
		return rule;
	}

	@Override
	public List<Rule> getPossibleRules(String type, String username) throws InterruptedException, OWLOntologyCreationException {
		List<Rule> rules = new LinkedList<Rule>();
		
		Collection <Service> triggerServices = owlService.getTriggerServices();
		Collection <Service> actionServices = owlService.getTriggerServices();
		
		Collection<Trigger> triggers = new LinkedList<Trigger>();
		Collection<Action> actions = new LinkedList<Action>();
		for(Service service : triggerServices)
			triggers.addAll(owlService.getTriggers(service.getId(), username));
		for(Service service : actionServices)
			actions.addAll(owlService.getActions(service.getId(), username));
		
		
		for(Trigger trigger : triggers) {
			for(Action action : actions) {
				Rule rule = new Rule();
				rule.setTrigger(trigger);
				rule.setAction(action);
				rules.add(rule);
			}
		}
		
		return rules;
	}

}
