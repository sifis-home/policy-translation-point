package it.polito.elite.sifis.entities.owl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RuleProblems {
	private Rule rule;
	private Set<List<Rule>> loops;
	private Set<List<Rule>> redundantRules;
	private Set<List<Rule>> inconsistentRules;
	private List<Rule> executedRules;
	private Trigger startTrigger;
	
	@JsonIgnore
	public void addLoop(List<Rule> rules){
		if(loops == null) loops = new HashSet<List<Rule>>();
		loops.add(rules);
	}
	@JsonIgnore
	public void addRedundantRules(List<Rule> rules){
		if(redundantRules == null) redundantRules = new HashSet<List<Rule>>();
		redundantRules.add(rules);
	}

	@JsonIgnore
	public void addInconsistentRules(List<Rule> rules){
		if(inconsistentRules == null) inconsistentRules =  new HashSet<List<Rule>>();
		inconsistentRules.add(rules);
	}
	
	public Set<List<Rule>> getLoops() {
		return loops;
	}
	
	public void setLoops(Set<List<Rule>> loops) {
		this.loops = loops;
	}
	
	public Set<List<Rule>> getRedundantRules() {
		return redundantRules;
	}
	
	public void setRedundantRules(Set<List<Rule>> redundantRules) {
		this.redundantRules = redundantRules;
	}
	
	public Set<List<Rule>> getInconsistentRules() {
		return inconsistentRules;
	}
	
	public void setInconsistentRules(Set<List<Rule>> inconsistentRules) {
		this.inconsistentRules = inconsistentRules;
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	@JsonIgnore
	public boolean containsInconsistency(List<Rule> inconsistent) {
		if(this.inconsistentRules == null || this.inconsistentRules.size() == 0)
			return false;
		for(List<Rule> inconsistency : this.inconsistentRules){
			if(inconsistency.containsAll(inconsistent))
				return true;
		}

		return false;
	}
	@JsonIgnore
	public boolean containsRedundancy(List<Rule> redundant) {
		if(this.redundantRules == null || this.redundantRules.size() == 0)
			return false;

		for(List<Rule> redundancy : this.redundantRules){
			if(redundancy.containsAll(redundant))
				return true;
		}

		return false;
	}
	public List<Rule> getExecutedRules() {
		return executedRules;
	}
	public void setExecutedRules(List<Rule> executedRules) {
		this.executedRules = executedRules;
	}
	
	@JsonIgnore
	public void addExecutedRule(Rule rule){
		if(this.executedRules == null) this.executedRules = new LinkedList<Rule>();
		this.executedRules.add(rule);
	}
	
	public Trigger getStartTrigger() {
		return startTrigger;
	}
	public void setStartTrigger(Trigger startTrigger) {
		this.startTrigger = startTrigger;
	}
}
