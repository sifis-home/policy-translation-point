package it.polito.elite.sifis.entities.petrinet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Rule;


public class Simulation extends Thread {

    protected boolean step = false;
    protected boolean paused = false;
    protected boolean stop = false;
    /** Time delay between a transition is fired */
    public static int DELAY = 0;
    /** Default time delay between each transition firing process */
    public static int COMPONENTDELAY = 100;
    
    /** Petri Net pointer **/
    private PetriNet net;
    
    private List<Action> simualtedActions;
    private List<Rule> simulatedRules;
    
    /** Initializes Simulation. */
    public Simulation(PetriNet net, boolean step) {
    	this.net = net;
        this.step = step;
        this.simualtedActions = new LinkedList<Action>();
        this.simulatedRules = new LinkedList<Rule>();
    }

    @Override
    public void run() {
        while (!isFinished() && !stop) {
            fireTransition();
        }
        if (stop) {
            System.out.println("Stopped.\n");
        } else {
        	 System.out.println("Deadlock.\n");
        }      
    }

    /** Checks whether the Simulation has ended */
    public boolean isFinished() {
        return this.net.isDead();
    }

    /** Fires a single transition of the enabled transition list */
    protected void fireTransition() {
    	
    	enabledRuleTransitionList();
    	while(!this.enabledRuleTransitionList().isEmpty()){
    		Transition t;
        	t = getRandomRuleTransition();
        	Rule rule = ((RuleTransition)t).fireAndGetRule(0);
        	this.simualtedActions.add(rule.getAction());
        	this.simulatedRules.add(rule);
    	}
    	
    	enabledTransitionList();
        if (!this.enabledTransitionList().isEmpty()) {
        	Transition t;	
        	t = getRandomTransition();
        	t.fire(0);
        }
        
        pauseResumeSimulation();
        /*enabledTransitionList();
        if (!this.enabledTransitionList().isEmpty()) {
        	Transition t;
        	
        	t = getRandomTransition();
        	if(t instanceof RuleTransition){
        		Rule rule = ((RuleTransition)t).fireAndGetRule(0);   
        		this.simualtedActions.add(rule.getAction());
        		this.simulatedRules.add(rule);
        		
        	}
        	else
        		t.fire(0);
        	pauseResumeSimulation();
                
        }*/
    	
    	
    }

    public synchronized void pauseResumeSimulation() {
        if (step && !stop) {
            paused = true;
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException l) {
            }
        }
    }
    
    /** Returns a random transition from the enabled rule transition list */
    public RuleTransition getRandomRuleTransition() {
    	ArrayList<RuleTransition> enabledTransitions = this.enabledRuleTransitionList();
        Random generator = new Random();
        int rand = generator.nextInt(enabledTransitions.size());
        return enabledTransitions.get(rand);
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
    
    /** Returns a list of enabled rule transitions */
    public ArrayList<RuleTransition> enabledRuleTransitionList() {
        Iterator<Transition> it = this.net.getTransitions().iterator();
        ArrayList<RuleTransition> enabledTransitions = new ArrayList<RuleTransition>();
        while (it.hasNext()) {
            Transition transition = (Transition) it.next();
            if (transition.enabled(0) && transition instanceof RuleTransition) {
                enabledTransitions.add((RuleTransition)transition);
            }
        }
        return enabledTransitions;
    }

    /**
     * @return the step
     */
    public boolean isStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(boolean step) {
        this.step = step;
    }

    /**
     * @return the paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * @param paused the paused to set
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * @return the stop
     */
    public boolean isStop() {
        return stop;
    }

    /**
     * @param stop the stop to set
     */
    public void setStop(boolean stop) {
        if (this.paused) {
            synchronized (this) {
                this.notify();
            }
        }
        this.stop = stop;
    }

	public List<Action> getSimualtedActions() {
		return simualtedActions;
	}

	public List<Rule> getSimulatedRules() {
		return simulatedRules;
	}

	public void setSimulatedRules(List<Rule> simulatedRules) {
		this.simulatedRules = simulatedRules;
	}


    
    
}

