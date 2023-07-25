package it.polito.elite.sifis.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;

import it.polito.elite.sifis.entities.owl.Action;
import it.polito.elite.sifis.entities.owl.Command;
import it.polito.elite.sifis.entities.owl.Detail;
import it.polito.elite.sifis.entities.owl.IoTEntity;
import it.polito.elite.sifis.entities.owl.Location;
import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.entities.owl.Trigger;
import it.polito.elite.sifis.entities.xacml.AllOfType;
import it.polito.elite.sifis.entities.xacml.AnyOfType;
import it.polito.elite.sifis.entities.xacml.ApplyType;
import it.polito.elite.sifis.entities.xacml.AttributeDesignatorType;
import it.polito.elite.sifis.entities.xacml.AttributeValueType;
import it.polito.elite.sifis.entities.xacml.ConditionType;
import it.polito.elite.sifis.entities.xacml.EffectType;
import it.polito.elite.sifis.entities.xacml.MatchType;
import it.polito.elite.sifis.entities.xacml.ObjectFactory;
import it.polito.elite.sifis.entities.xacml.PolicyType;
import it.polito.elite.sifis.entities.xacml.RuleType;
import it.polito.elite.sifis.entities.xacml.TargetType;



public class XACMLServiceImpl implements XACMLService {
	@Autowired
	OWLService owlService;

	@Override
	public List<PolicyType> translate(Rule rule, String username) throws OWLOntologyCreationException, InterruptedException {
		
		List<PolicyType> policies = new ArrayList<PolicyType>();
		
		//extract location
		Location loc = null;
		for(Detail d : rule.getAction().getDetails()) {
			if (d.getType().equals("location")) {
				loc = new Location();
				loc.setName(((java.util.LinkedHashMap)d.getValue()).get("name").toString());
				loc.setURL(((java.util.LinkedHashMap)d.getValue()).get("url").toString());
			}
		}
		
		Set<Command> commands = owlService.getCommands(rule.getAction(),loc,username);
		

		if(loc.getName().equals("Entire Home")) {
			//extract device type
			Command c = commands.iterator().next();
			String type = c.getEntitiy().getType();
			Boolean allActions = false;
			
			List<String> classes = owlService.getDirectClasses(rule.getAction().getURL());
			if(classes.contains("http://elite.polito.it/ontologies/eupont.owl#Action"))
				allActions = true;
			
			switch (rule.getEffect()) {
				case "Permit":
					if(rule.getSubject().equals("marketplace"))
						
						policies.add(getInstallationPolicy(EffectType.PERMIT, rule.getSubject(), rule.getTrigger(), c, type, allActions));
					else
						policies.add(getExecutionPolicy(EffectType.PERMIT, rule.getSubject(), rule.getTrigger(), c, type, allActions));
					break;
				case "Deny":
					if(rule.getSubject().equals("marketplace"))
						policies.add(getInstallationPolicy(EffectType.DENY, rule.getSubject(), rule.getTrigger(), c, type, allActions));
					else
						policies.add(getExecutionPolicy(EffectType.DENY, rule.getSubject(), rule.getTrigger(), c, type, allActions));
					break;
				default:
					break;
			}
		}else{
			Boolean allActions = false;
			if(rule.getAction().getId().equals("Action"))
				allActions = true;
			for(Command command : commands) {
				switch (rule.getEffect()) {
					case "Permit":
						if(rule.getSubject().equals("marketplace"))
							policies.add(getInstallationPolicy(EffectType.PERMIT, rule.getSubject(), rule.getTrigger(), command, null, allActions));
						else
							policies.add(getExecutionPolicy(EffectType.PERMIT, rule.getSubject(), rule.getTrigger(), command, null, allActions));
						break;
					case "Deny":
						if(rule.getSubject().equals("marketplace"))
							policies.add(getInstallationPolicy(EffectType.DENY, rule.getSubject(), rule.getTrigger(), command, null, allActions));
						else
							policies.add(getExecutionPolicy(EffectType.DENY, rule.getSubject(), rule.getTrigger(), command, null, allActions));
						break;
					default:
						break;
				}	
				
			}
		}
		return policies;
	}
	
	private PolicyType getInstallationPolicy(EffectType effect, String subject, Trigger trigger, Command command, String type, Boolean allActions) {
		PolicyType myPolicy = new PolicyType();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String policyName = "installation_";
		
		if (allActions)
			policyName += "any_action";
		else if(command != null && command.getType() != null)
			policyName += command.getType();
		
		if(type != null) {
        	myPolicy.setTarget(getExecutionTarget(null, subject, type));
        	policyName += "_";
        	policyName += type;
        }
        else {
        	myPolicy.setTarget(getExecutionTarget(command.getEntitiy(), subject, null));
        	policyName += "_";
        	policyName += command.getEntitiy().getId();
        }
		
        myPolicy.setPolicyId(policyName + "_" + String.valueOf(timestamp.getTime()));

	    myPolicy.setDescription("XACML policy for SIFIS-Home - " + timestamp.getTime());
        myPolicy.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny");
        
        myPolicy.setVersion("3.0");
        
        myPolicy.setTarget(getInstallationTarget(subject));

        List<Object> rules = new ArrayList<Object>();
        
        rules.add(getInstallationRule(effect, subject, trigger, command, type, allActions));
        rules.add(getDefaultPermitRule());
        myPolicy.combinerParametersOrRuleCombinerParametersOrVariableDefinition = rules;
		
		return myPolicy;
	}
	
	
	private PolicyType getExecutionPolicy(EffectType effect, String subject, Trigger trigger, Command command, String type, Boolean allActions) {
		PolicyType myPolicy = new PolicyType();

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String policyName = "execution_";
		
		if (allActions)
			policyName += "any_action";
		else if(command != null && command.getType() != null)
			policyName += command.getType();
		
		if(type != null) {
        	myPolicy.setTarget(getExecutionTarget(null, subject, type));
        	policyName += "_";
        	policyName += type;
        }
        else {
        	myPolicy.setTarget(getExecutionTarget(command.getEntitiy(), subject, null));
        	policyName += "_";
        	policyName += command.getEntitiy().getId();
        }
		
	    myPolicy.setDescription("XACML policy for SIFIS-Home - " + timestamp.getTime());
        myPolicy.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit");
        
        myPolicy.setVersion("3.0");
       
        List<Object> rules = new ArrayList<Object>();
        rules.add(getExecutionRule(effect, subject, trigger, command, allActions));
       
        rules.add(getDefaultDenyRule());
        myPolicy.combinerParametersOrRuleCombinerParametersOrVariableDefinition = rules;
        
        
        myPolicy.setPolicyId(String.valueOf(policyName + "_" + timestamp.getTime()));

		return myPolicy;
	}
	
	private TargetType getInstallationTarget(String subject) {
		
		TargetType target = new TargetType();
		List<AnyOfType> anyOfs = new ArrayList<AnyOfType>();
		List<AllOfType> allOfs = new ArrayList<AllOfType>();
		AllOfType allOf = new AllOfType();
		AnyOfType anyOf = new AnyOfType();
		List<MatchType> matches = new ArrayList<MatchType>();
        
        matches.add(getSubjectMatch(subject));
        matches.add(getActionMatch("install"));

        allOf.match = matches;
        allOfs.add(allOf);
        anyOf.allOf = allOfs;
        anyOfs.add(anyOf);
        target.anyOf = anyOfs;
        
        return target;
	}

	private TargetType getExecutionTarget(IoTEntity entity, String subject, String type) {
		
		TargetType target = new TargetType();
		List<AnyOfType> anyOfs = new ArrayList<AnyOfType>();
		List<AllOfType> allOfs = new ArrayList<AllOfType>();
		AllOfType allOf = new AllOfType();
		AnyOfType anyOf = new AnyOfType();
		List<MatchType> matches = new ArrayList<MatchType>();
        
        matches.add(getSubjectMatch(subject));
        
        if(entity != null)
        	matches.add(getResourceMatch(entity));
        else if(type != null)
        	matches.add(getTypeMatch(type));

        allOf.match = matches;
        allOfs.add(allOf);
        anyOf.allOf = allOfs;
        anyOfs.add(anyOf);
        target.anyOf = anyOfs;
        
        return target;
	}
	
	public static MatchType getSubjectMatch(String subject) {
        MatchType match = new MatchType();
        
        //ATTRIBUTE VALUE
      	AttributeValueType av = new AttributeValueType();
      	List<Object> ids = new ArrayList<Object>();
      	ids.add(subject);
      	av.content = new ArrayList<Object>(ids);
      	av.setDataType("http://www.w3.org/2001/XMLSchema#string");
        match.setAttributeValue(av);
        
        //MATCH ID
        match.setMatchId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
        
        //ATTRIBUTE DESIGNATOR
        AttributeDesignatorType ad = new AttributeDesignatorType();
      	ad.setAttributeId("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
      	ad.setCategory("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
      	ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
      	ad.setMustBePresent(true);
        match.setAttributeDesignator(ad);
        
        return match;
	}
	
	public static MatchType getResourceMatch(IoTEntity entity) {
        MatchType match = new MatchType();
                
        //ATTRIBUTE VALUE
        AttributeValueType av = new AttributeValueType();
        List<Object> ids = new ArrayList<Object>();
        ids.add(entity.getId());
        av.content = new ArrayList<Object>(ids);
        av.setDataType("http://www.w3.org/2001/XMLSchema#string");
        match.setAttributeValue(av);
        
        //MATCH ID
        match.setMatchId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
        
        //ATTRIBUTE DESIGNATOR
        AttributeDesignatorType ad = new AttributeDesignatorType();
        ad.setAttributeId("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
        ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
        ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
        ad.setMustBePresent(true);
        match.setAttributeDesignator(ad);
        
        return match;
	}
	
	public static MatchType getTypeMatch(String type) {
        MatchType match = new MatchType();
                
        //ATTRIBUTE VALUE
        AttributeValueType av = new AttributeValueType();
        List<Object> ids = new ArrayList<Object>();
        ids.add(type);
        av.content = new ArrayList<Object>(ids);
        av.setDataType("http://www.w3.org/2001/XMLSchema#string");
        match.setAttributeValue(av);
        
        //MATCH ID
        match.setMatchId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
        
        //ATTRIBUTE DESIGNATOR
        AttributeDesignatorType ad = new AttributeDesignatorType();
        ad.setAttributeId("eu.sifis-home:1.0:resource:device-type");
        ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
        ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
        ad.setMustBePresent(true);
        match.setAttributeDesignator(ad);
        
        return match;
	}
	
	public static MatchType getActionMatch(String action) {
        MatchType match = new MatchType();
                
        //ATTRIBUTE VALUE
        AttributeValueType av = new AttributeValueType();
        List<Object> ids = new ArrayList<Object>();
        ids.add(action);
        av.content = new ArrayList<Object>(ids);
        av.setDataType("http://www.w3.org/2001/XMLSchema#string");
        match.setAttributeValue(av);
        
        //MATCH ID
        match.setMatchId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
        
        //ATTRIBUTE DESIGNATOR
        AttributeDesignatorType ad = new AttributeDesignatorType();
        ad.setAttributeId("urn:oasis:names:tc:xacml:1.0:action:action-id");
        ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:action");
        ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
        ad.setMustBePresent(true);
        match.setAttributeDesignator(ad);
        
        return match;
	}
	
	public static RuleType getDefaultDenyRule() {
		RuleType rule = new RuleType();
		rule.setEffect(EffectType.DENY);
		TargetType target = new TargetType();
        rule.setTarget(target);
		rule.setRuleId("urn:oasis:names:tc:xacml:3.0:defdeny");
		rule.setDescription("Default Deny");

		return rule;
	}
	
	public static RuleType getDefaultPermitRule() {
		RuleType rule = new RuleType();
		rule.setEffect(EffectType.PERMIT);
		TargetType target = new TargetType();
        rule.setTarget(target);
		rule.setRuleId("default-permit");
		rule.setDescription("Default Permit");

		return rule;
	}
	
	public static RuleType getInstallationRule(EffectType effect, String subject, Trigger trigger, Command command, String type, Boolean allActions){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        RuleType rule = new RuleType();
		rule.setRuleId(String.valueOf(timestamp.getTime()));
		rule.setEffect(effect);
		
		TargetType target = new TargetType();
        
        rule.setTarget(target);
        
        if(type != null && allActions)
            rule.setPreCondition(getTypeCondition(type, "pre"));
        else if(!allActions)
            rule.setPreCondition(getActionCondition(command, "pre"));        
        	
        
        if (trigger.getId().equals("sifis_always_trigger"))
        	rule.setOngoingCondition(getEnvironmentCondition("ongoing"));
        else
        	rule.setOngoingCondition(getTriggerCondition(trigger, "ongoing"));

        rule.setPostCondition(getSubjectCondition(subject, "post"));
        return rule;
	} 
	
	public static RuleType getExecutionRule(EffectType effect, String subject, Trigger trigger, Command command, Boolean allActions){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		RuleType rule = new RuleType();
		rule.setRuleId(String.valueOf(timestamp.getTime()));
		rule.setEffect(effect);
		
		TargetType target = new TargetType();
        
        rule.setTarget(target);
        
        if(!allActions)
        	rule.setPreCondition(getActionCondition(command, "pre"));
        
        if(trigger != null)
        	rule.setOngoingCondition(getTriggerCondition(trigger, "ongoing"));
        else
        	rule.setOngoingCondition(getEnvironmentCondition("ongoing"));
   
        rule.setPostCondition(getSubjectCondition(subject, "post"));

		return rule;
		
	}
	
	public static ConditionType getEnvironmentCondition(String decisionTime) {
		ConditionType condition = new ConditionType();
		condition.setDecisionTime(decisionTime);
		
		//ATTRIBUTE DESIGNATOR
		AttributeDesignatorType ad = new AttributeDesignatorType();
		ad.setAttributeId("urn:oasis:names:tc:xacml:3.0:environment:attribute-1");
		ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:environment");
		ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
		ad.setMustBePresent(true);
		
		//VALUE
		AttributeValueType value = new AttributeValueType();
		List<Object> ids = new ArrayList<Object>();
		ids.add("attribute-1-value");
		value.content = new ArrayList<Object>(ids);
		value.setDataType("http://www.w3.org/2001/XMLSchema#string");
		
		ObjectFactory factory = new ObjectFactory();
	    JAXBElement<?> expItem = factory.createAttributeDesignator(ad);
	    List<JAXBElement<?>> expressions = new ArrayList<JAXBElement<?>>();  
	    expressions.add(expItem);
	    ApplyType applyDesignator = new ApplyType();
	    applyDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-one-and-only");
	    applyDesignator.expression = expressions;
	        
	    JAXBElement<?> item1 = factory.createApply(applyDesignator);
	    JAXBElement<?> item2 = factory.createAttributeValue(value);

	    List<JAXBElement<?>> expressionsExt = new ArrayList<JAXBElement<?>>();  
	    expressionsExt.add(item1);
	    expressionsExt.add(item2);
	        
	    //EXTERNAL APPLY
	    ApplyType externalDesignator = new ApplyType();
	    externalDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
	    externalDesignator.expression = expressionsExt;
	    
	    List<JAXBElement<?>> expressionsRoot = new ArrayList<JAXBElement<?>>();  
	    expressionsRoot.addAll(externalDesignator.getExpression());

	    //ROOT APPLY
	    ApplyType rootDesignator = new ApplyType();
	    rootDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:and");
	    rootDesignator.expression = expressionsRoot;
	    
	  
	    JAXBElement<?> conditionInner = factory.createApply(rootDesignator);
	    condition.expression = conditionInner;
	    
	    return condition;
				
	}
	
	public static ConditionType getResourceCondition(IoTEntity entity, String decisionTime) {
		ConditionType condition = new ConditionType();
		condition.setDecisionTime(decisionTime);
		
		//ATTRIBUTE DESIGNATOR
		AttributeDesignatorType ad = new AttributeDesignatorType();
		ad.setAttributeId("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
		ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
		ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
		ad.setMustBePresent(true);
		
		//VALUE
		AttributeValueType value = new AttributeValueType();
		List<Object> ids = new ArrayList<Object>();
		ids.add(entity.getId());
		value.content = new ArrayList<Object>(ids);
		value.setDataType("http://www.w3.org/2001/XMLSchema#string");
		
		ObjectFactory factory = new ObjectFactory();
	    JAXBElement<?> expItem = factory.createAttributeDesignator(ad);
	    List<JAXBElement<?>> expressions = new ArrayList<JAXBElement<?>>();  
	    expressions.add(expItem);
	    ApplyType applyDesignator = new ApplyType();
	    applyDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-one-and-only");
	    applyDesignator.expression = expressions;
	        
	    JAXBElement<?> item1 = factory.createApply(applyDesignator);
	    JAXBElement<?> item2 = factory.createAttributeValue(value);

	    List<JAXBElement<?>> expressionsExt = new ArrayList<JAXBElement<?>>();  
	    expressionsExt.add(item1);
	    expressionsExt.add(item2);
	        
	    //EXTERNAL APPLY
	    ApplyType externalDesignator = new ApplyType();
	    externalDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
	    externalDesignator.expression = expressionsExt;
	    
	    List<JAXBElement<?>> expressionsRoot = new ArrayList<JAXBElement<?>>();  
	    expressionsRoot.addAll(externalDesignator.getExpression());

	    //ROOT APPLY
	    ApplyType rootDesignator = new ApplyType();
	    rootDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:and");
	    rootDesignator.expression = expressionsRoot;
	    
	  
	    JAXBElement<?> conditionInner = factory.createApply(rootDesignator);
	    condition.expression = conditionInner;
	    
	    return condition;
				
	}
	
	
	public static ConditionType getTypeCondition(String type, String decisionTime) {
		ConditionType condition = new ConditionType();
		condition.setDecisionTime(decisionTime);
		
		//ATTRIBUTE DESIGNATOR
		AttributeDesignatorType ad = new AttributeDesignatorType();
		ad.setAttributeId("eu.sifis-home:1.0:resource:device:device-type");
		ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
		ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
		ad.setMustBePresent(true);
		
		//VALUE
		AttributeValueType value = new AttributeValueType();
		List<Object> ids = new ArrayList<Object>();
		ids.add(type);
		value.content = new ArrayList<Object>(ids);
		value.setDataType("http://www.w3.org/2001/XMLSchema#string");
		
		ObjectFactory factory = new ObjectFactory();
	    JAXBElement<?> expItem = factory.createAttributeDesignator(ad);
	    List<JAXBElement<?>> expressions = new ArrayList<JAXBElement<?>>();  
	    expressions.add(expItem);
	    ApplyType applyDesignator = new ApplyType();
	    applyDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-one-and-only");
	    applyDesignator.expression = expressions;
	        
	    JAXBElement<?> item1 = factory.createApply(applyDesignator);
	    JAXBElement<?> item2 = factory.createAttributeValue(value);

	    List<JAXBElement<?>> expressionsExt = new ArrayList<JAXBElement<?>>();  
	    expressionsExt.add(item1);
	    expressionsExt.add(item2);
	        
	    //EXTERNAL APPLY
	    ApplyType externalDesignator = new ApplyType();
	    externalDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
	    externalDesignator.expression = expressionsExt;
	    
	    List<JAXBElement<?>> expressionsRoot = new ArrayList<JAXBElement<?>>();  
	    expressionsRoot.addAll(externalDesignator.getExpression());

	    //ROOT APPLY
	    ApplyType rootDesignator = new ApplyType();
	    rootDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:and");
	    rootDesignator.expression = expressionsRoot;
	    
	  
	    JAXBElement<?> conditionInner = factory.createApply(rootDesignator);
	    condition.expression = conditionInner;
	    
	    return condition;
				
	}
	
	public static ConditionType getSubjectCondition(String subject, String decisionTime) {
		ConditionType condition = new ConditionType();
		condition.setDecisionTime(decisionTime);
		
		//ATTRIBUTE DESIGNATOR
		AttributeDesignatorType ad = new AttributeDesignatorType();
		ad.setAttributeId("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		ad.setCategory("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
		ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
		ad.setMustBePresent(true);
		
		//VALUE
		AttributeValueType value = new AttributeValueType();
		List<Object> ids = new ArrayList<Object>();
		ids.add(subject);
		value.content = new ArrayList<Object>(ids);
		value.setDataType("http://www.w3.org/2001/XMLSchema#string");
		
		ObjectFactory factory = new ObjectFactory();
	    JAXBElement<?> expItem = factory.createAttributeDesignator(ad);
	    List<JAXBElement<?>> expressions = new ArrayList<JAXBElement<?>>();  
	    expressions.add(expItem);
	    ApplyType applyDesignator = new ApplyType();
	    applyDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-one-and-only");
	    applyDesignator.expression = expressions;
	        
	    JAXBElement<?> item1 = factory.createApply(applyDesignator);
	    JAXBElement<?> item2 = factory.createAttributeValue(value);

	    List<JAXBElement<?>> expressionsExt = new ArrayList<JAXBElement<?>>();  
	    expressionsExt.add(item1);
	    expressionsExt.add(item2);
	        
	    //EXTERNAL APPLY
	    ApplyType externalDesignator = new ApplyType();
	    externalDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
	    externalDesignator.expression = expressionsExt;
	    
	    List<JAXBElement<?>> expressionsRoot = new ArrayList<JAXBElement<?>>();  
	    expressionsRoot.addAll(externalDesignator.getExpression());

	    //ROOT APPLY
	    ApplyType rootDesignator = new ApplyType();
	    rootDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:and");
	    rootDesignator.expression = expressionsRoot;
	    
	  
	    JAXBElement<?> conditionInner = factory.createApply(rootDesignator);
	    condition.expression = conditionInner;
	    
	    return condition;
				
	}
	
	public static ConditionType getActionCondition(Command command, String decisionTime) {
		ConditionType condition = new ConditionType();
		condition.setDecisionTime(decisionTime);

		//ATTRIBUTE DESIGNATOR
		AttributeDesignatorType ad = new AttributeDesignatorType();
		ad.setAttributeId("urn:oasis:names:tc:xacml:1.0:action:action-id");
		ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:action");
		ad.setDataType("http://www.w3.org/2001/XMLSchema#string");
		ad.setMustBePresent(true);
		
		//VALUE
		AttributeValueType value = new AttributeValueType();
		List<Object> ids = new ArrayList<Object>();
		ids.add(command.getType());
		value.content = new ArrayList<Object>(ids);
		value.setDataType("http://www.w3.org/2001/XMLSchema#string");     
		
		ObjectFactory factory = new ObjectFactory();
	    JAXBElement<?> expItem = factory.createAttributeDesignator(ad);
	    List<JAXBElement<?>> expressions = new ArrayList<JAXBElement<?>>();  
	    expressions.add(expItem);
	    ApplyType applyDesignator = new ApplyType();
	    applyDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-one-and-only");
	    applyDesignator.expression = expressions;
	        
	    JAXBElement<?> item1 = factory.createApply(applyDesignator);
	    JAXBElement<?> item2 = factory.createAttributeValue(value);

	    List<JAXBElement<?>> expressionsExt = new ArrayList<JAXBElement<?>>();  
	    expressionsExt.add(item1);
	    expressionsExt.add(item2);
	        
	    //EXTERNAL APPLY
	    ApplyType externalDesignator = new ApplyType();
	    externalDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:string-equal");
	    externalDesignator.expression = expressionsExt;
	    
	    List<JAXBElement<?>> expressionsRoot = new ArrayList<JAXBElement<?>>();  
	    expressionsRoot.addAll(externalDesignator.getExpression());

	    //ROOT APPLY
	    ApplyType rootDesignator = new ApplyType();
	    rootDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:and");
	    rootDesignator.expression = expressionsRoot;
	    
	  
	    JAXBElement<?> conditionInner = factory.createApply(rootDesignator);
	    condition.expression = conditionInner;
		
	    return condition;
	}

	public static ConditionType getTriggerCondition(Trigger trigger, String decisionTime) {
		
		ConditionType condition = new ConditionType();
		condition.setDecisionTime(decisionTime);

		//ATTRIBUTE DESIGNATOR
		AttributeDesignatorType ad = new AttributeDesignatorType();
        List<JAXBElement<?>> expressionsExt = new ArrayList<JAXBElement<?>>();  
        ObjectFactory factory = new ObjectFactory();
        JAXBElement<?> expItem;
        List<JAXBElement<?>> expressions;
        ApplyType applyDesignator = new ApplyType();
        ApplyType externalDesignator = new ApplyType();

        JAXBElement<?> item1;
        JAXBElement<?> item2;
        JAXBElement<?> item3;
		switch(trigger.getService().getId()) {
			case "sifis_time":
		        ad.setAttributeId("urn:oasis:names:tc:xacml:1.0:environment:current-time");
		        ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:environment");
		        ad.setDataType("http://www.w3.org/2001/XMLSchema#time");
		        ad.setMustBePresent(true);
		        
		        //TIME 1
				AttributeValueType time1 = new AttributeValueType();
		        List<Object> ids = new ArrayList<Object>();
		        ids.add(extractFrom(trigger));
		        time1.content = new ArrayList<Object>(ids);
		        time1.setDataType("http://www.w3.org/2001/XMLSchema#time");
				
				//TIME 2
		        AttributeValueType time2 = new AttributeValueType();
		        ids = new ArrayList<Object>();
		        ids.add(extractTo(trigger));
		        time2.content = new ArrayList<Object>(ids);
		        time2.setDataType("http://www.w3.org/2001/XMLSchema#time");
		        
		        expItem = factory.createAttributeDesignator(ad);
		        expressions = new ArrayList<JAXBElement<?>>();  
		        expressions.add(expItem);
		        applyDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:time-one-and-only");
		        applyDesignator.expression = expressions;
		        
		        
		        item1 = factory.createApply(applyDesignator);
		        item2 = factory.createAttributeValue(time1);
		        item3 = factory.createAttributeValue(time2);

		        expressionsExt.add(item1);
		        expressionsExt.add(item2);
		        expressionsExt.add(item3);
		        
		        //EXTERNAL APPLY
		        externalDesignator.setFunctionId("urn:oasis:names:tc:xacml:2.0:function:time-in-range");
		        externalDesignator.expression = expressionsExt;
		        
		        break;
		        
			case "sifis_windows":
				ad.setAttributeId("eu:sifis-home:1.0:environment:all-windows-in-bedroom-closed");
		        ad.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:environment");
		        ad.setDataType("http://www.w3.org/2001/XMLSchema#boolean");
		        ad.setMustBePresent(true);
		        
		        //VALUE
		        AttributeValueType av = new AttributeValueType();
		        ids = new ArrayList<Object>();
		        if(trigger.getId().equals("sifis_windows_close_trigger"))
		        	ids.add("true");
		        else 
		        	ids.add("false");
		        av.content = new ArrayList<Object>(ids);
		        av.setDataType("http://www.w3.org/2001/XMLSchema#boolean");
		        
		        expItem = factory.createAttributeDesignator(ad);
		        expressions = new ArrayList<JAXBElement<?>>();  
		        expressions.add(expItem);

		        applyDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:boolean-one-and-only");
		        applyDesignator.expression = expressions;
		        
		        
		        item1 = factory.createApply(applyDesignator);
		        item2 = factory.createAttributeValue(av);

		        expressionsExt.add(item1);
		        expressionsExt.add(item2);		        
		        
		        //EXTERNAL APPLY
		        externalDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:boolean-equal");
		        externalDesignator.expression = expressionsExt;
		        
		        break;
		}
		
       
        JAXBElement<?> conditionInner = factory.createApply(externalDesignator);
        
        condition.expression = conditionInner;
        
        return condition;
	}



	private static String extractFrom(Trigger trigger) {
		for(Detail detail : trigger.getDetails()){
			if(detail.getValue()!= null && detail.getName().trim().contains("from")){
				return detail.getValue() + ":00:00+01:00";
			}
		}
		return "N.A.";
	}



	private static String extractTo(Trigger trigger) {
		for(Detail detail : trigger.getDetails()){
			if(detail.getValue()!= null && detail.getName().trim().contains("to")){
				return detail.getValue() + ":00:00+01:00";
			}
		}
		return "N.A.";
	}
	
	
}
