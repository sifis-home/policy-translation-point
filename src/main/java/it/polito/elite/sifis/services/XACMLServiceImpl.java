package it.polito.elite.sifis.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;

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
		

		
		for(Command command : commands) {

			if(command.getType() != null) {
				switch (command.getType()) {
					case "permit":
						policies.add(getPolicy(EffectType.PERMIT, rule.getTrigger(), command));
						break;
					case "deny":
						policies.add(getPolicy(EffectType.DENY, rule.getTrigger(), command));
						break;
					default:
				}	
			}
		}
		return policies;
	}
	
	
	
	private PolicyType getPolicy(EffectType effect, Trigger trigger, Command command) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		PolicyType myPolicy = new PolicyType();
	    myPolicy.setDescription("XACML policy for SIFIS-Home - " + timestamp.getTime());
        myPolicy.setPolicyId(String.valueOf(timestamp.getTime()) + "-" + command.getEntitiy().getId());
        myPolicy.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable");
        myPolicy.setVersion("1");
        
        myPolicy.setTarget(getTargetResource(command.getEntitiy()));
        
        List<Object> rules = new ArrayList<Object>();
        rules.add(getRule(effect, trigger, command));
        myPolicy.combinerParametersOrRuleCombinerParametersOrVariableDefinition = rules;
		
		return myPolicy;
	}
	
	private TargetType getTargetResource(IoTEntity entity) {
		
		TargetType target = new TargetType();
		List<AnyOfType> anyOfs = new ArrayList<AnyOfType>();
		List<AllOfType> allOfs = new ArrayList<AllOfType>();
		AllOfType allOf = new AllOfType();
		AnyOfType anyOf = new AnyOfType();
		List<MatchType> matches = new ArrayList<MatchType>();
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
        
        matches.add(match);
        allOf.match = matches;
        allOfs.add(allOf);
        anyOf.allOf = allOfs;
        anyOfs.add(anyOf);
        target.anyOf = anyOfs;
        
        return target;
	}
	
	
	public static RuleType getRule(EffectType effect, Trigger trigger, Command command){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		RuleType rule = new RuleType();
		rule.setRuleId(String.valueOf(timestamp.getTime()));
		rule.setEffect(effect);
		
		TargetType target = new TargetType();
		List<AnyOfType> anyOfs = new ArrayList<AnyOfType>();
		List<AllOfType> allOfs = new ArrayList<AllOfType>();
		AllOfType allOf = new AllOfType();
		AnyOfType anyOf = new AnyOfType();
		List<MatchType> matches = new ArrayList<MatchType>();
		
		
		//match action
        MatchType match = new MatchType();
        
        //ATTRIBUTE VALUE
        AttributeValueType av = new AttributeValueType();
        List<Object> ids = new ArrayList<Object>();
        ids.add(command.getId());
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
        
        matches.add(match);
      
        allOf.match = matches;
        allOfs.add(allOf);
        anyOf.allOf = allOfs;
        anyOfs.add(anyOf);
        target.anyOf = anyOfs;
        
        rule.setTarget(target);
        
        rule.setCondition(getCondition(trigger));
      
		return rule;
		
	}
	
	public static ConditionType getCondition(Trigger trigger) {
		
		ConditionType condition = new ConditionType();
		
		//ATTRIBUTE DESIGNATOR
        
		AttributeDesignatorType ad = new AttributeDesignatorType();
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
        
        ObjectFactory factory = new ObjectFactory();
        JAXBElement<?> expItem = factory.createAttributeDesignator(ad);
        List<JAXBElement<?>> expressions = new ArrayList<JAXBElement<?>>();  
        expressions.add(expItem);
        ApplyType applyDesignator = new ApplyType();
        applyDesignator.setFunctionId("urn:oasis:names:tc:xacml:1.0:function:time-one-and-only");
        applyDesignator.expression = expressions;
        
        
        JAXBElement<?> item1 = factory.createApply(applyDesignator);
        JAXBElement<?> item2 = factory.createAttributeValue(time1);
        JAXBElement<?> item3 = factory.createAttributeValue(time2);

        List<JAXBElement<?>> expressionsExt = new ArrayList<JAXBElement<?>>();  
        expressionsExt.add(item1);
        expressionsExt.add(item2);
        expressionsExt.add(item3);
        
        //EXTERNAL APPLY
        ApplyType externalDesignator = new ApplyType();
        externalDesignator.setFunctionId("urn:oasis:names:tc:xacml:2.0:function:time-in-range");
        externalDesignator.expression = expressionsExt;
        
  
        JAXBElement<?> conditionInner = factory.createApply(externalDesignator);
        condition.expression = conditionInner;
        
        return condition;
	}



	private static String extractTo(Trigger trigger) {
		for(Detail detail : trigger.getDetails()){
			if(detail.getValue()!= null && detail.getName().trim().contains("from")){
				return detail.getValue() + ":00:00+01:00";
			}
		}
		return "N.A.";
	}



	private static String extractFrom(Trigger trigger) {
		for(Detail detail : trigger.getDetails()){
			if(detail.getValue()!= null && detail.getName().trim().contains("to")){
				return detail.getValue() + ":00:00+01:00";
			}
		}
		return "N.A.";
	}
	
	
}
