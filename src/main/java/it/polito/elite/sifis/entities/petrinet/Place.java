package it.polito.elite.sifis.entities.petrinet;

public class Place extends NetObject {

    /** List of tokens this place contains. */
    private TokenSet tokens = new TokenSet();
    /** Maximum number of tokens this place can hold (0 = no limit).*/
    private int capacity = 0;
    /** Petri Net pointer **/
    private PetriNet net;
    
    /** Place constructor. */
    public Place(PetriNet net) {
    	this.setNet(net);
        this.id = "p" + this.id;
    }

    /** Constructor to create a new place with specific attributes. */
    public Place(PetriNet net, String id) {
    	this.setNet(net);
        this.id = id;
    }

    /**
     * @return the tokens
     */
    public TokenSet getTokens() {
        return tokens;
    }

    /**
     * @param tokens the tokens to set
     */
    public void setTokens(TokenSet tokens) {
        this.tokens = tokens;
    }

    /** Adds a TokenSet to the current place TokenSet*/
    public void addToken(TokenSet tokenSet) {
        tokens.addAll(tokenSet);
    }

    /** Removes a TokenSet in the current place TokenSet*/
    public void removeTokens(TokenSet tokenSet) {
        tokens.removeAll(tokenSet);
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

	public PetriNet getNet() {
		return net;
	}

	public void setNet(PetriNet net) {
		this.net = net;
	}
}