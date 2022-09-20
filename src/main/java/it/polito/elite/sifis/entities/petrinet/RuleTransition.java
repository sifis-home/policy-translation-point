package it.polito.elite.sifis.entities.petrinet;

import java.util.Iterator;

import it.polito.elite.sifis.entities.owl.Rule;


public class RuleTransition extends Transition {
	
	/** Global clock when the transition fires.*/
    private long globalClock;
    
    private Rule rule;
    
	public RuleTransition(PetriNet net, Rule rule) {
		super(net);
		this.setRule(rule);
	}
	
	public RuleTransition(PetriNet net, String id, Rule rule) {
	    super(net, id);
		this.setRule(rule);
	}

	public RuleTransition(PetriNet net, String id, String guardText, Rule rule) {
	   super(net, id, guardText);
	   this.setRule(rule);
	}

	@Override
    public void fire(long globalClock) {
		//remove tokens from the input places and generates a token in the output place corresponding to the rule action
		this.globalClock = globalClock;
				
		//remove all tokens from places
        Iterator<InputArc> itIN = this.getNet().getInputArcs().iterator();
        while (itIN.hasNext()) {
            InputArc arc = (InputArc) itIN.next();
            if (arc.getTransition().getId().equals(getId())) {
                arc.getPlace().removeTokens(new TokenSet(this.rule.getTrigger()));
                System.out.println("Removed token from " + arc.getPlace().getLabel());
            }
        }
        
        if (!this.getLabel().equals(this.getId())) {
        	System.out.println(this.getLabel() + " (" + this.getId() + ") fired!\n");
        } else {
        	System.out.println(this.getId() + " fired.\n");
        }
        
        //generate the corresponding Action token in the output places
        Iterator<OutputArc> itOUT = this.getNet().getOutputArcs().iterator();
        while (itOUT.hasNext()) {
            OutputArc arc = (OutputArc) itOUT.next();
            if (arc.getTransition().getId().equals(getId())) {
            	Token token;
        		token = new Token(this.rule.getAction());
                TokenSet tokenSet = new TokenSet(token);
                tokenSet.incrementTime(globalClock);// set time of all new tokens of the tokenSet
                arc.getPlace().addToken(tokenSet);
                System.out.println("+ " + arc.getExecuteText() + "\n");
            }
        }
        System.out.println("----------------------------\n");
	}
	
	public Rule fireAndGetRule(long globalClock){
		this.fire(globalClock);
		
		return this.rule;
	}
	
	@Override
	public long getGlobalClock() {
		return globalClock;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	
	
}
