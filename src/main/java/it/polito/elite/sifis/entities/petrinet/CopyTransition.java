package it.polito.elite.sifis.entities.petrinet;

import java.util.Iterator;

import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Trigger;


public class CopyTransition extends Transition {
	public enum Type {
	    TRIGGER,
	    ACTION,
	}
	
	/** Global clock when the transition fires.*/
    private long globalClock;
    
    private Trigger trigger;
    
    private Action action;
    
    private Type type;
    
	public CopyTransition(PetriNet net, Trigger trigger) {
		super(net);
		this.setTrigger(trigger);
		this.type = Type.TRIGGER;
	}
	 
	public CopyTransition(PetriNet net, String id, Trigger trigger) {
	    super(net, id);
	    this.setTrigger(trigger);
		this.type = Type.TRIGGER;
	}

	public CopyTransition(PetriNet net, String id, String guardText, Trigger trigger) {
	   super(net, id, guardText);
	   this.setTrigger(trigger);
		this.type = Type.TRIGGER;
	}
	
	public CopyTransition(PetriNet net, Action action) {
		super(net);
		this.setAction(action);
		this.type = Type.ACTION;

	}
	 
	public CopyTransition(PetriNet net, String id, Action action) {
	    super(net, id);
		this.setAction(action);
		this.type = Type.ACTION;
	}

	public CopyTransition(PetriNet net, String id, String guardText, Action action) {
	   super(net, id, guardText);
		this.setAction(action);
		this.type = Type.ACTION;
	}
	
	@Override
    public void fire(long globalClock) {
		//remove tokens from the input places and replicate them on all the output places
		this.globalClock = globalClock;

		//remove all tokens from places
        Iterator<InputArc> itIN = this.getNet().getInputArcs().iterator();
        while (itIN.hasNext()) {
            InputArc arc = (InputArc) itIN.next();
            if (arc.getTransition().getId().equals(getId())) {
            	if(this.type.equals(Type.TRIGGER))
            		arc.getPlace().removeTokens(new TokenSet(this.trigger));
            	else if(this.type.equals(Type.ACTION))
            		arc.getPlace().removeTokens(new TokenSet(this.action));
                System.out.println("Removed token from " + arc.getPlace().getLabel());
            }
        }
        
        if (!this.getLabel().equals(this.getId())) {
        	System.out.println(this.getLabel() + " (" + this.getId() + ") fired!\n");
        } else {
        	System.out.println(this.getId() + " fired.\n");
        }
        
        //replicate the tokens in all the output places
        Iterator<OutputArc> itOUT = this.getNet().getOutputArcs().iterator();
        while (itOUT.hasNext()) {
            OutputArc arc = (OutputArc) itOUT.next();
            if (arc.getTransition().getId().equals(getId())) {
            	TokenSet tokenSet =  null;
            	if(this.type.equals(Type.TRIGGER))
            		 tokenSet = new TokenSet(this.trigger);
            	else if(this.type.equals(Type.ACTION))
            		tokenSet = new TokenSet(this.action);
                tokenSet.incrementTime(globalClock);// set time of all new tokens of the tokenSet
                arc.getPlace().addToken(tokenSet);
                System.out.println("Copied token in " + arc.getPlace().getLabel());
            }
        }
        System.out.println("----------------------------\n");
	}
	
	@Override
	public long getGlobalClock() {
		return globalClock;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
