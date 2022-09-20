package it.polito.elite.sifis.entities.petrinet;


public class OutputArc extends Arc implements Inscription {

    /** Text string that represents the expression this arc will execute by default */
    private String executeText = "1";

    /** Petri Net pointer **/
    private PetriNet net;
    
    /** Constructor to create an output arc. */
    public OutputArc(PetriNet net, Place place, Transition transition) {
        this.net = net;
    	this.id = "o" + this.id;
        this.place = place;
        this.transition = transition;
    }

    /** Constructor to create a new output arc with specific attributes. */
    public OutputArc(PetriNet net, String id, Place place, Transition transition, String action) {
        this.net = net;
    	this.id = id;
        this.place = place;
        this.transition = transition;
        this.executeText = action;
    }

    public boolean evaluate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Default method that is executed when a transition fires. */
    public TokenSet execute() {
        return new TokenSet(executeText);
    }

    /** Returns the TokenSet of the place this arc is connected*/
    public TokenSet getTokenSet() {
        return this.getPlace().getTokens();
    }

    /**
     * @return the executeText
     */
    public String getExecuteText() {
        return executeText;
    }

    /**
     * @param executeText the executeText to set
     */
    public void setExecuteText(String executeText) {
        this.executeText = executeText;
    }
}
