package it.polito.elite.sifis.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;

import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Detail;
import it.polito.elite.sifis.entities.owl.IoTEntity;
import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.owl.Trigger;
import it.polito.elite.sifis.entities.petrinet.PetriNet;
import it.polito.elite.sifis.entities.petrinet.Place;
import it.polito.elite.sifis.entities.petrinet.RestSimulator;
import it.polito.elite.sifis.entities.petrinet.RuleTransition;
import it.polito.elite.sifis.entities.petrinet.Token;
import it.polito.elite.sifis.entities.petrinet.TokenSet;
import it.polito.elite.sifis.entities.petrinet.TriggerPlace;
import it.polito.elite.sifis.entities.petrinet.TriggerTransition;
import it.polito.elite.sifis.entities.petrinet.ActionPlace;
import it.polito.elite.sifis.entities.petrinet.CopyTransition;
import it.polito.elite.sifis.entities.petrinet.ExtendedPetriNet;
import it.polito.elite.sifis.entities.petrinet.InputArc;
import it.polito.elite.sifis.entities.petrinet.OutputArc;

public class PetriNetServiceImpl implements PetriNetService{

	@Autowired
	DBService dbService;
	
	@Autowired
	OWLService owlService;
	
	private HashMap<String,PetriNet> nets;
	private HashMap<String,RestSimulator> simulators;
	
	public PetriNetServiceImpl(){
		nets = new HashMap<String,PetriNet>();
		simulators = new HashMap<String,RestSimulator>();
	}
	
	@Override
	public PetriNet getNet(String username, List<Rule> rules, boolean refresh) throws OWLOntologyCreationException, InterruptedException{
		ExtendedPetriNet petriNet; 
		//force refresh
		if(refresh)
			petriNet = (ExtendedPetriNet) buildNet(username, rules);
		
		petriNet = (ExtendedPetriNet) nets.get(username);
		if(petriNet == null)
			petriNet = (ExtendedPetriNet) buildNet(username, rules);
		return petriNet;

	}
	
	@Override
	public RestSimulator getRestSimulator(String username) {
		return simulators.get(username);
	}
	

	@Override
	public Rule startRestSimulation(Trigger trigger, List<Rule> rules, String username) throws OWLOntologyCreationException, InterruptedException {
		// TODO Auto-generated method stub
		ExtendedPetriNet net = (ExtendedPetriNet) this.getNet(username, rules, false);
		//add the token for the simulation
		net.removeAllTokens();
		
		TriggerPlace tp = net.getTriggerPlaces().get(trigger);
		Token token;
		token = new Token(tp.getTrigger());
	    token.setInitialMarkingtExpression("1");
	    tp.addToken(new TokenSet(token));
		RestSimulator simulator = new RestSimulator(net);
		this.simulators.put(username, simulator);
		
		return simulator.step();		
	}

	@Override
	public Rule stepRestSimulation(String username) {
		RestSimulator simulator =  simulators.get(username);
		if (simulator !=  null) {
			return simulator.step();
        }
		return null;
	}

	@Override
	public void stopRestSimulator(String username) {
		// TODO Auto-generated method stub
		this.simulators.remove(username);
	}


	private PetriNet buildNet(String username, List<Rule> rules) throws OWLOntologyCreationException, InterruptedException {
		
		ExtendedPetriNet petriNet = new ExtendedPetriNet();	
		petriNet.setLabel(username);
		

		//Loading unique places
		for(Rule rule :  rules){
			TriggerPlace tp = new TriggerPlace(petriNet,rule.getTrigger());
			tp.setLabel(rule.getTrigger().toString());
			petriNet.addTriggerPlace(tp);
			petriNet.addPlace(tp);
			
			ActionPlace ap = new ActionPlace(petriNet,rule.getAction());
			ap.setLabel(rule.getAction().toString());
			petriNet.addActionPlace(ap);
			petriNet.addPlace(ap);
		}		
		
		//adding unique places in the graph
		for(TriggerPlace tp: petriNet.getTriggerPlaces().values())
			petriNet.addGraphVertex(tp);
		for(ActionPlace ap: petriNet.getActionPlaces().values())
			petriNet.addGraphVertex(ap);
		
		//Search for trigger copies
		for(TriggerPlace triggerPlace : petriNet.getTriggerPlaces().values()){
			List<Rule> rulesByTrigger = getByTrigger(rules, triggerPlace.getTrigger());
			if(rulesByTrigger.size() == 1){
				//attach the action place directly with the unique trigger place
				Rule rule = rulesByTrigger.iterator().next();
				ActionPlace actionPlace = petriNet.getActionPlace(rule.getAction());
				RuleTransition ruleTransition = new RuleTransition(petriNet,rule);
				petriNet.addTransition(ruleTransition);
				InputArc ruleArc1 = new InputArc(petriNet, triggerPlace, ruleTransition);
				ruleArc1.setEvaluateText("Token > 0");
		        petriNet.addInputArc(ruleArc1);
		        OutputArc ruleArc2 = new OutputArc(petriNet, actionPlace, ruleTransition);
		        ruleArc2.setExecuteText("new " + rule.getAction().toString());
		        petriNet.addOutputArc(ruleArc2);
		      
		        petriNet.addGraphEdge(triggerPlace, actionPlace);
			}
			else if(rulesByTrigger.size() > 1){
				//create the copy transition
				CopyTransition copyTransition = new CopyTransition(petriNet, triggerPlace.getTrigger());
				petriNet.addTransition(copyTransition);
				InputArc copyArc1 = new InputArc(petriNet, triggerPlace, copyTransition);
				copyArc1.setEvaluateText("Token > 0");
		        petriNet.addInputArc(copyArc1);
				//create copied places
				for(Rule rule : rulesByTrigger){
					Place copyPlace = new Place(petriNet);
					copyPlace.setLabel("copy_of_" + rule.getTrigger().toString());
					petriNet.addPlace(copyPlace);
					//attach the copied place to the original one
			        OutputArc copyArc2 = new OutputArc(petriNet, copyPlace, copyTransition);
			        copyArc2.setExecuteText("Copy the token");
			        petriNet.addOutputArc(copyArc2);
			        //attach the copied place to the relative action
					ActionPlace actionPlace = petriNet.getActionPlace(rule.getAction());
					RuleTransition ruleTransition = new RuleTransition(petriNet,rule);
					//ruleTransition.setAction(rule.getAction());
					petriNet.addTransition(ruleTransition);
					InputArc ruleArc1 = new InputArc(petriNet, copyPlace, ruleTransition);
					ruleArc1.setEvaluateText("Token > 0");
			        petriNet.addInputArc(ruleArc1);
			        OutputArc ruleArc2 = new OutputArc(petriNet, actionPlace, ruleTransition);
			        ruleArc2.setExecuteText("new " + rule.getAction().toString());
			        petriNet.addOutputArc(ruleArc2);
			        
			        petriNet.addGraphEdge(triggerPlace,actionPlace);
			        
			        
				}
			}
		}
		
		//Adding triggers transitions
		for(ActionPlace actionPlace : petriNet.getActionPlaces().values()){
			List<Rule> triggeredRules = getTriggeredRules(rules, actionPlace.getAction());
			Set<Trigger> triggeredTriggers = new HashSet<Trigger>();
			for(Rule rule :  triggeredRules)
				triggeredTriggers.add(rule.getTrigger());
			
			
			if(triggeredTriggers.size() == 1){
				//attach the action place directly with the triggered place
				//Rule rule = triggeredRules.iterator().next();
				Trigger trigger = triggeredTriggers.iterator().next();
				TriggerPlace triggerPlace = petriNet.getTriggerPlace(trigger);
				TriggerTransition triggerTransition = new TriggerTransition(petriNet, trigger, actionPlace.getAction());
				petriNet.addTransition(triggerTransition);
				InputArc ruleArc1 = new InputArc(petriNet, actionPlace, triggerTransition);
				ruleArc1.setEvaluateText("Token > 0");
		        petriNet.addInputArc(ruleArc1);
		        OutputArc ruleArc2 = new OutputArc(petriNet, triggerPlace, triggerTransition);
		        ruleArc2.setExecuteText("new " + trigger.toString());
		        petriNet.addOutputArc(ruleArc2);
		        
		        petriNet.addGraphEdge(actionPlace, triggerPlace);
			}
			else if(triggeredTriggers.size() > 1){
				//duplicate the action place and attach each copy to the relative triggered place
				//create the copy transition
				CopyTransition copyTransition = new CopyTransition(petriNet, actionPlace.getAction());
				petriNet.addTransition(copyTransition);
				InputArc copyArc1 = new InputArc(petriNet, actionPlace, copyTransition);
				copyArc1.setEvaluateText("Token > 0");
		        petriNet.addInputArc(copyArc1);
		        //create copied places
				for(Trigger trigger : triggeredTriggers){
					Place copyPlace = new Place(petriNet);
					copyPlace.setLabel("copy_of_" + actionPlace.getAction().toString());
					petriNet.addPlace(copyPlace);
					//attach the copied place to the original one
			        OutputArc copyArc2 = new OutputArc(petriNet, copyPlace, copyTransition);
			        copyArc2.setExecuteText("Copy the token");
			        petriNet.addOutputArc(copyArc2);
			        //attach the copied place to the relative trigger
					TriggerPlace triggerPlace = petriNet.getTriggerPlace(trigger);
					TriggerTransition triggerTransition = new TriggerTransition(petriNet,triggerPlace.getTrigger(), actionPlace.getAction());
					petriNet.addTransition(triggerTransition);
					InputArc ruleArc1 = new InputArc(petriNet, copyPlace, triggerTransition);
					ruleArc1.setEvaluateText("Token > 0");
			        petriNet.addInputArc(ruleArc1);
			        OutputArc ruleArc2 = new OutputArc(petriNet, triggerPlace, triggerTransition);
			        ruleArc2.setExecuteText("new " + trigger.toString());
			        petriNet.addOutputArc(ruleArc2);

			        petriNet.addGraphEdge(actionPlace, triggerPlace);

				}
			}
		}
		nets.put(username, petriNet);
		return petriNet;
	}
	private List<Rule> getByTrigger(List<Rule> ruleset, Trigger trigger){
		List<Rule> rules = new LinkedList<Rule>();
		for(Rule r : ruleset){
			if(r.getTrigger().equals(trigger))
				rules.add(r);
		}
		return rules;
	}
	
	private List<Rule> getTriggeredRules(List<Rule> ruleset, Action action) throws InterruptedException{
		List<Rule> rules = new LinkedList<Rule>();
		for(Rule r : ruleset){
			if(action.getService().equals(r.getTrigger().getService())){
				//trigger and action act on the same service: possible implication
				
				//check entity
				boolean sameEntity = false;
				for(Detail ad : action.getDetails()){
					if(ad.getValue() instanceof IoTEntity){
						IoTEntity actionEntity = (IoTEntity) ad.getValue();
						for(Detail td : r.getTrigger().getDetails()){
							if(td.getValue() instanceof IoTEntity){
								IoTEntity triggerEntity = (IoTEntity) td.getValue();
								if(triggerEntity.equals(actionEntity))
									sameEntity = true;
							}
						}
					}
				}
				
				if(sameEntity){
					if(this.owlService.triggers(action, r.getTrigger())){
						boolean error = false, warning = false;
						if(r.getTrigger().getDetails() != null && r.getTrigger().getDetails().size() > 0){
							//the trigger has some details -> warning
							warning = true;
						}else{
							//the trigger do not have any details -> error
							error = true;
						}
						rules.add(r);
					}
				}
			}
		}
		return rules;
	}

	@Override
	public List<Rule> getParents(Rule simulatedRule, List<Rule> executedRules) throws InterruptedException {
		List<Rule> rules = new LinkedList<Rule>();
		for(Rule executedRule : executedRules){
			
			
			if(executedRule.getAction().getService().equals(simulatedRule.getTrigger().getService())){
				//trigger and action act on the same service: possible implication
				
				//check entity
				boolean sameEntity = false;
				for(Detail ad : executedRule.getAction().getDetails()){
					if(ad.getValue() instanceof IoTEntity){
						IoTEntity actionEntity = (IoTEntity) ad.getValue();
						for(Detail td : simulatedRule.getTrigger().getDetails()){
							if(td.getValue() instanceof IoTEntity){
								IoTEntity triggerEntity = (IoTEntity) td.getValue();
								if(triggerEntity.equals(actionEntity))
									sameEntity = true;
							}
						}
					}
				}
				
				if(sameEntity){
					if(this.owlService.triggers(executedRule.getAction(), simulatedRule.getTrigger())){
						boolean error = false, warning = false;
						if(simulatedRule.getTrigger().getDetails() != null && simulatedRule.getTrigger().getDetails().size() > 0){
							//the trigger has some details -> warning
							warning = true;
						}else{
							//the trigger do not have any details -> error
							error = true;
						}
						rules.add(executedRule);
					}
				}
			}
			
		}
		return rules;
	}



	


}
