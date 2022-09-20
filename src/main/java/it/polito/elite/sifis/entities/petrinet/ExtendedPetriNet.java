package it.polito.elite.sifis.entities.petrinet;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.alg.cycle.HawickJamesSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.owl.Trigger;


public class ExtendedPetriNet extends PetriNet{
	
	private HashMap<Trigger,TriggerPlace> triggerPlaces;
	private HashMap<Action,ActionPlace> actionPlaces;
	private DirectedGraph<Place,DefaultEdge> graph;
	
	public ExtendedPetriNet(){
		super();
		triggerPlaces = new HashMap<Trigger,TriggerPlace>();
		actionPlaces = new HashMap<Action,ActionPlace>();
		graph = new DefaultDirectedGraph<>(DefaultEdge.class);
	}
	
	public HashMap<Trigger,TriggerPlace> getTriggerPlaces() {
		return triggerPlaces;
	}
	public void setTriggerPlaces(HashMap<Trigger,TriggerPlace> triggerPlaces) {
		this.triggerPlaces = triggerPlaces;
	}
	public HashMap<Action,ActionPlace> getActionPlaces() {
		return actionPlaces;
	}
	public void setActionPlaces(HashMap<Action,ActionPlace> actionPlaces) {
		this.actionPlaces = actionPlaces;
	}
	
	public void addTriggerPlace(TriggerPlace place){
		if(!this.triggerPlaces.containsKey(place.getTrigger())){
			this.triggerPlaces.put(place.getTrigger(), place);
		}
	}
	
	public void addActionPlace(ActionPlace place){
		if(!this.actionPlaces.containsKey(place.getAction())){
			this.actionPlaces.put(place.getAction(), place);
		}
	}
	
	public ActionPlace getActionPlace(Action action){
		return this.actionPlaces.get(action);
	}
	
	public TriggerPlace getTriggerPlace(Trigger trigger){
		return this.triggerPlaces.get(trigger);
	}
	
	public void addGraphVertex(Place p){
		this.graph.addVertex(p);
	}
	
	public void addGraphEdge(Place p1, Place p2){
		this.graph.addEdge(p1, p2);
	}
	
	public DirectedGraph<Place,DefaultEdge> getGraph(){
		return this.graph;
	}
	
	public void removeAllTokens(){
		for(Place p : this.getPlaces()){
			try{
				TokenSet toRemove = new TokenSet(p.getTokens());
				p.removeTokens(toRemove);
			}
			catch(Throwable t){t.printStackTrace();}
		}
	}
	
	
	public void simulate() throws OWLOntologyCreationException {
		
		Simulation simulator = new Simulation(this, false);
		simulator.start();
		try {
			Thread.sleep(1000);
			simulator.setStop(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(!simulator.isFinished()){
			System.out.println("Possible Loops");
		}
		
	}
	
	public Trigger getRoot(Rule rule){
		//get the connected component in the graph that includes the rule
		ConnectivityInspector<Place, DefaultEdge> inspector = new ConnectivityInspector<>(this.graph);
		TriggerPlace tp = this.triggerPlaces.get(rule.getTrigger());
		Set<Place> connectedComponent = inspector.connectedSetOf(tp);
		//get the root of the connected component
		Place root = null;
		for(Place p : connectedComponent){
			if(this.graph.inDegreeOf(p) == 0){
				root = p;
				break;
			}
		}
		//insert a token in the root and simulate the net
		if(root != null && root instanceof TriggerPlace)
			return ((TriggerPlace)root).getTrigger();
		else
			return null;
	}

	public Rule[] getSimualtedRules(Trigger trigger) throws OWLOntologyCreationException {
		List<Rule> rules = new LinkedList<Rule>();
		this.removeAllTokens();
		TriggerPlace tp = this.triggerPlaces.get(trigger);
		Token token;
		token = new Token(trigger);
		token.setInitialMarkingtExpression("1");
		tp.addToken(new TokenSet(token));
		
		Simulation simulator = new Simulation(this, false);
		simulator.start();
		try {
			Thread.sleep(1000);
			simulator.setStop(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
		if(!simulator.isFinished()){
			System.out.println("Possible Loops");
		}
		rules.addAll(simulator.getSimulatedRules());
		
		Rule[] rulesArray = new Rule[rules.size()];
		rulesArray = rules.toArray(rulesArray);
		return rulesArray;
	}

	
	public List<List<Place>> getCycle(Rule rule, List<Rule> rules) {
		DirectedSimpleCycles<Place, DefaultEdge> cycleDetector = new HawickJamesSimpleCycles<Place, DefaultEdge>(this.graph);
		List<List<Place>> cycles = cycleDetector.findSimpleCycles();
		return cycles;
	}

	
}
