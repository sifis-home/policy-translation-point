package it.polito.elite.sifis.services;

import java.io.File;
import java.io.IOException;

import it.polito.elite.sifis.entities.xacml.PolicyType;

public interface DHTService {
	
	String publishPolicy(File policyFile, PolicyType policy) throws IOException;
}
