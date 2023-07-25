package it.polito.elite.sifis.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import it.polito.elite.sifis.entities.xacml.PolicyType;
import it.polito.elite.sifis.utils.PropertyFileReader;

public class DHTServiceImpl implements DHTService {

	private static String configFile = "config.properties";
    private final RestTemplate restTemplate;

	Properties prop;

	public DHTServiceImpl() {
		prop = PropertyFileReader.loadProperties(configFile);
        this.restTemplate = new RestTemplate();
	}

	@Override
	public String publishPolicy(File policyFile, PolicyType policy) throws IOException {
		
		InputStream inputStream = new FileInputStream(policyFile);
	    String policyStr = it.polito.elite.sifis.utils.FileUtils.readFromInputStream(inputStream);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	    JSONObject message = new JSONObject();
	    message.put("purpose", "ADD_POLICY");
	    message.put("message_id", UUID.randomUUID());
	    message.put("policy",  Base64.getEncoder().encodeToString(policyStr.getBytes()));
	    message.put("policy_id", policy.getPolicyId());
	    
	    JSONObject messageExt = new JSONObject();
	    messageExt.put("message", message);

	    JSONObject command = new JSONObject();
	    command.put("command_type", "pap-command");
	    command.put("id", "pap-"+UUID.randomUUID());
	    command.put("topic_name", "topic-name");
	    command.put("topic_uuid", "topic-uuid-the-ucs-is-subscribed-to");
	    command.put("value", messageExt);
	    
	    JSONObject value = new JSONObject();
	    value.put("timestamp", timestamp.getTime());
	    value.put("command", command);
	    
	    JSONObject request = new JSONObject();
	    request.put("value", value);
	    
	    JSONObject toSend = new JSONObject();
	    toSend.put("RequestPubMessage", request);
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<String> req = 
	    	      new HttpEntity<String>(toSend.toString(), headers);
	    
	    try {
		    String res = restTemplate.postForObject(prop.getProperty("dhtEndpoint") + "pub", req, String.class);
		    return res;
	    } catch(Throwable t) {
	    	return null;
	    }
	}

}
