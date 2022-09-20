package it.polito.elite.sifis.entities.petrinet;

import java.util.Iterator;

import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Trigger;


public class TriggerTransition extends Transition {
	/** Global clock when the transition fires.*/
    private long globalClock;
    
	private Trigger trigger;
	    
	private Action action;
	    
	public TriggerTransition(PetriNet net, Trigger trigger, Action action) {
		super(net);
		this.setTrigger(trigger);
		this.setAction(action);
	}

	public TriggerTransition(PetriNet net, String id, Trigger trigger, Action action) {
	    super(net, id);
	    this.setTrigger(trigger);
	}

	public TriggerTransition(PetriNet net, String id, String guardText, Trigger trigger, Action action) {
	   super(net, id, guardText);
	   this.setTrigger(trigger);
	}
	
	@Override
    public void fire(long globalClock) {
		//remove tokens from the input places and generates a token in the output place corresponding to the rule trigger
		this.globalClock = globalClock;

		//remove all tokens from places
        Iterator<InputArc> itIN = this.getNet().getInputArcs().iterator();
        while (itIN.hasNext()) {
            InputArc arc = (InputArc) itIN.next();
            if (arc.getTransition().getId().equals(getId())) {
            	arc.getPlace().removeTokens(new TokenSet(this.action));
                System.out.println("Removed token from " + arc.getPlace().getLabel());
            }
        }
        
        if (!this.getLabel().equals(this.getId())) {
        	System.out.println(this.getLabel() + " (" + this.getId() + ") fired!\n");
        } else {
        	System.out.println(this.getId() + " fired.\n");
        }
        
        //generate the corresponding Trigger token 
        Iterator<OutputArc> itOUT = this.getNet().getOutputArcs().iterator();
        while (itOUT.hasNext()) {
            OutputArc arc = (OutputArc) itOUT.next();
            if (arc.getTransition().getId().equals(getId())) {
            	Token token;
        		token = new Token(this.trigger);
                TokenSet tokenSet = new TokenSet(token);
                tokenSet.incrementTime(globalClock);// set time of all new tokens of the tokenSet
                arc.getPlace().addToken(tokenSet);
                System.out.println("+ " + arc.getExecuteText() + "\n");
            }
        }
        System.out.println("----------------------------\n");
	}
	
	@Override
	public long getGlobalClock() {
		return globalClock;
	}
	
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	
	public Trigger getTrigger(){
		return this.trigger;
	}
	
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
