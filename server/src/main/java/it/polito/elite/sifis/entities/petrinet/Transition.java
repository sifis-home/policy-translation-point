package it.polito.elite.sifis.entities.petrinet;

import java.util.Iterator;

public class Transition extends NetObject implements Inscription {

    private String guardText = "return true;";
    /** Global clock when the transition fires.*/
    private long globalClock;
    
    /** Petri Net pointer **/
    private PetriNet net;

    public Transition(PetriNet net) {
    	this.net = net;
        this.id = "t" + this.id;
    }

    public Transition(PetriNet net, String id) {
    	this.net = net;
    	this.id = id;
    }

    public Transition(PetriNet net, String id, String guardText) {
    	this.net = net;
        this.id = id;
        this.guardText = guardText;
    }

    /** Fires a transition. */
    public void fire(long globalClock) {
        this.globalClock = globalClock;

        //remove all tokens from places
        Iterator<InputArc> itIN = this.net.getInputArcs().iterator();
        while (itIN.hasNext()) {
            InputArc arc = (InputArc) itIN.next();
            if (arc.getTransition().getId().equals(getId())) {
                arc.getPlace().removeTokens(arc.execute());
                System.out.println("- " + arc.getExecuteText());
            }
        }

        if (!this.getLabel().equals(this.getId())) {
        	System.out.println(this.getLabel() + " (" + this.getId() + ") fired!\n");
        } else {
        	System.out.println(this.getId() + " fired.\n");
        }
        
        //create all tokens to output places
        Iterator<OutputArc> itOUT = this.net.getOutputArcs().iterator();
        while (itOUT.hasNext()) {
            OutputArc arc = (OutputArc) itOUT.next();
            if (arc.getTransition().getId().equals(getId())) {
                TokenSet tokenSet = arc.execute();
                tokenSet.incrementTime(globalClock);// set time of all new tokens of the tokenSet
                arc.getPlace().addToken(tokenSet);
                System.out.println("+ " + arc.getExecuteText() + "\n");
            }
        }
        System.out.println("----------------------------\n");

    }

    public boolean enabled(long time) {
        // transition guard evaluation
        boolean enabled = evaluate();

        // input arc guards
        if (enabled && !this.net.getInputArcs().isEmpty()) {
            Iterator<InputArc> it = this.net.getInputArcs().iterator();
            while (enabled && it.hasNext()) {
                InputArc arc = (InputArc) it.next();
                if (arc.getTransition().getId().equals(getId())) {
                    TokenSet tokensList = arc.getPlace().getTokens();
                    enabled = tokensList.containsTime(time);
                    // check arc's evaluation expression
                    enabled = enabled & arc.evaluate();
                }
            }
        }

        // check output arc place capacity restriction
        if (enabled && this.net.getOutputArcs().isEmpty()) {
            Iterator<OutputArc> it = this.net.getOutputArcs().iterator();
            while (enabled && it.hasNext()) {
                OutputArc arc = (OutputArc) it.next();
                if (arc.getTransition().getId().equals(getId())) {
                    TokenSet tokensList = arc.getPlace().getTokens();
                    // check if places have capacity limit
                    if (arc.getPlace().getCapacity() != 0) {
                        enabled = enabled & arc.getPlace().getCapacity() > tokensList.size();
                    }
                }
            }
        }

        return enabled;
    }

    public boolean evaluate() {
        return true;
    }

    public TokenSet execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TokenSet getTokenSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the inscriptionText
     */
    public String getGuardText() {
        return guardText;
    }

    /**
     * @param inscriptionText the inscriptionText to set
     */
    public void setGuardText(String guardText) {
        this.guardText = guardText;
    }

    /**
     * @return the globalClock
     */
    public long getGlobalClock() {
        return globalClock;
    }

	public PetriNet getNet() {
		return net;
	}

	public void setNet(PetriNet net) {
		this.net = net;
	}
    
    
}


