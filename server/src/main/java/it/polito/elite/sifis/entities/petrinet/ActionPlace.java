package it.polito.elite.sifis.entities.petrinet;

import it.polito.elite.sifis.entities.owl.Action;

public class ActionPlace extends Place{

	private Action action;
		
	public ActionPlace(PetriNet net, Action action) {
		super(net);
		this.setAction(action);
	}
	
	public ActionPlace(PetriNet net, String id, Action action) {
    	super(net, id);
    	this.setAction(action);
    }

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

}
