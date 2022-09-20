package it.polito.elite.sifis.entities.petrinet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import it.polito.elite.sifis.entities.owl.Rule;


public class RestSimulator {
	
	protected boolean paused = false;
	protected boolean stop = false;
    
	/** Petri Net pointer **/
    private PetriNet net;
    
    public RestSimulator(PetriNet net){
    	this.net = net;
    }
    
    public Rule step(){
    	if(!isFinished()){
    		return fireTransition();
    	}
    	return null;
    }
    
    private Rule fireTransition() {
    	enabledTransitionList();
        
    	if (!this.enabledTransitionList().isEmpty()) {
        	Transition t;
        	t = getRandomTransition();
        	//if the transition is a Copy or Triggers transition -> fire anyway
        	while(!(t instanceof RuleTransition)){
        		t.fire(0);
        		if(this.enabledTransitionList().isEmpty())
        			return null;
        		t = getRandomTransition();
        	}
            
        	//There is a Rule transition -> fire the transition and return the activated rule
        	return ((RuleTransition)t).fireAndGetRule(0);            
        }
        
		return null;
	}

	/** Checks whether the Simulation has ended */
    private boolean isFinished() {
        return this.net.isDead();
    }

    /** Returns a random transition from the enabled transition list */
    public Transition getRandomTransition() {
        ArrayList<Transition> enabledTransitions = this.enabledTransitionList();
        Random generator = new Random();
        int rand = generator.nextInt(enabledTransitions.size());
        return (Transition) enabledTransitions.get(rand);
    }

    /** Returns a list of enabled transitions */
    public ArrayList<Transition> enabledTransitionList() {
        Iterator<Transition> it = this.net.getTransitions().iterator();
        ArrayList<Transition> enabledTransitions = new ArrayList<Transition>();
        while (it.hasNext()) {
            Transition transition = (Transition) it.next();
            if (transition.enabled(0)) {
                enabledTransitions.add(transition);
            }
        }
        return enabledTransitions;
    }

    
}
