package it.polito.elite.sifis.entities.petrinet;

import it.polito.elite.sifis.entities.owl.Trigger;

public class TriggerPlace extends Place{

	private Trigger trigger;
	
	public TriggerPlace(PetriNet net, Trigger trigger) {
		super(net);
		this.setTrigger(trigger);
	}

	public TriggerPlace(PetriNet net, String id, Trigger trigger) {
    	super(net, id);
    	this.setTrigger(trigger);
    }

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	
	

}
