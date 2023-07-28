package it.polito.elite.sifis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.assertj.core.api.Assertions.assertThat;

import it.polito.elite.sifis.controllers.LoginController;
import it.polito.elite.sifis.controllers.SifisController;
import it.polito.elite.sifis.entities.db.User;
import it.polito.elite.sifis.entities.owl.IoTEntity;
import it.polito.elite.sifis.entities.owl.Rule;
import it.polito.elite.sifis.services.DBService;
import it.polito.elite.sifis.services.UserService;
import it.polito.elite.sifis.utils.PropertyFileReader;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = SifisApplication.class)
@AutoConfigureMockMvc
public class SifisControllerTest {

	@Autowired
    private MockMvc mvc;
	
	@Autowired
	DBService dbService;
	
	@Autowired
	UserService userService;
	
	

	@Test 
	public void testLogin() throws Exception {
		mvc.perform(get("/user")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	@Test 
	public void testRegistration() throws Exception {
		
		User u = new User();
		u.setActive(1);
		u.setLastName("Test");
		u.setName("Test");
		u.setPassword("Test");
		u.setUsername("Test");
		
		ObjectMapper mapper = new ObjectMapper();  

		mvc.perform(post("/registration")
			      .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(u)))
			      .andExpect(status().isOk());
		
		User added = userService.findUserByUsername("Test");
		assertThat(added).isNotNull();
		userService.deleteUser(added);
		
		User addedAfterDeletion = userService.findUserByUsername("Test");
		assertThat(addedAfterDeletion).isNull();
		
	}
	
	@Test 
	public void testTriggerServices() throws Exception {
		mvc.perform(get("/sifis/triggerservice")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}

	
	@Test 
	public void testTriggers() throws Exception {
		String service = "sifis_time";
		mvc.perform(get("/sifis/triggerservice/" + service + "/triggers")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	@Test 
	public void testTriggerDetails() throws Exception {
		String service = "sifis_time";
		String trigger = "sifis_every_afternoon_trigger";
		mvc.perform(get("/sifis/triggerservice/" + service + "/triggers/" + trigger + "/details")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	
	
	@Test 
	public void testActionServices() throws Exception {
		mvc.perform(get("/sifis/actionservice")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	@Test 
	public void testActions() throws Exception {
		String service = "sifis_video";
		mvc.perform(get("/sifis/triggerservice/" + service + "/triggers")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	@Test 
	public void testActionDetails() throws Exception {
		String service = "sifis_video";
		String action = "sifis_every_afternoon_trigger";
		mvc.perform(get("/sifis/actionservice/" + service + "/actions/" + action + "/details")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	@Test 
	public void testListRules() throws Exception {
		
		mvc.perform(get("/sifis/rule")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	@Test 
	public void testSaveRule() throws Exception {
		int rulesBefore = dbService.getRulesByType("sifis", "ptp-demo").size();
		String rule = "{\"hover\":false,\"subject\":\"marketplace\",\"trigger\":{\"id\":\"sifis_every_afternoon_trigger\",\"description\":\"This trigger fires every afternoon\",\"name\":\"Every Afternoon\",\"service\":{\"id\":\"sifis_time\",\"name\":\"Temporal\",\"color\":\"rgb(84, 89, 95)\",\"webId\":1,\"image\":\"time.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_time\"},\"details\":[{\"id\":\"sifis_hour_from_detail\",\"name\":\"1 - from [hh]\",\"type\":\"value\",\"value\":\"13\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_from_detail\"},{\"id\":\"sifis_hour_to_detail\",\"name\":\"2 - to [hh]\",\"type\":\"value\",\"value\":\"17\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_to_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_every_afternoon_trigger\"},\"action\":{\"id\":\"sifis_stop_record_video_action\",\"name\":\"Stop recording video\",\"description\":\"This action stops the video recording\",\"service\":{\"id\":\"sifis_video\",\"name\":\"Video\",\"color\":\"rgb(255, 58, 58)\",\"webId\":2,\"image\":\"video.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_video\"},\"details\":[{\"id\":\"sifis_location_detail\",\"name\":\"Location\",\"type\":\"location\",\"value\":{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},\"trigger\":null,\"action\":null,\"alternatives\":[{\"id\":null,\"name\":\"Entire Home\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-home\"},{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},{\"id\":null,\"name\":\"Bedroom\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-bedroom\"},{\"id\":null,\"name\":\"Kitchen\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-kitchen\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_location_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_stop_record_video_action\"},\"effect\":\"Deny\",\"dbId\":null,\"timestamp\":1690467841444}";
		
		
		MvcResult added = mvc.perform(post("/sifis/rule")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON).content(rule))
			      .andExpect(status().isOk()).andReturn();
		
		dbService.deleteRule(Long.parseLong(added.getResponse().getContentAsString()));
		
		int rulesAfter = dbService.getRulesByType("sifis", "ptp-demo").size();
		assertThat (rulesBefore == rulesAfter);
	}
	
	@Test 
	public void testCheckRule() throws Exception {

		String rule = "{\"hover\":false,\"subject\":\"marketplace\",\"trigger\":{\"id\":\"sifis_every_afternoon_trigger\",\"description\":\"This trigger fires every afternoon\",\"name\":\"Every Afternoon\",\"service\":{\"id\":\"sifis_time\",\"name\":\"Temporal\",\"color\":\"rgb(84, 89, 95)\",\"webId\":1,\"image\":\"time.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_time\"},\"details\":[{\"id\":\"sifis_hour_from_detail\",\"name\":\"1 - from [hh]\",\"type\":\"value\",\"value\":\"13\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_from_detail\"},{\"id\":\"sifis_hour_to_detail\",\"name\":\"2 - to [hh]\",\"type\":\"value\",\"value\":\"17\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_to_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_every_afternoon_trigger\"},\"action\":{\"id\":\"sifis_stop_record_video_action\",\"name\":\"Stop recording video\",\"description\":\"This action stops the video recording\",\"service\":{\"id\":\"sifis_video\",\"name\":\"Video\",\"color\":\"rgb(255, 58, 58)\",\"webId\":2,\"image\":\"video.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_video\"},\"details\":[{\"id\":\"sifis_location_detail\",\"name\":\"Location\",\"type\":\"location\",\"value\":{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},\"trigger\":null,\"action\":null,\"alternatives\":[{\"id\":null,\"name\":\"Entire Home\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-home\"},{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},{\"id\":null,\"name\":\"Bedroom\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-bedroom\"},{\"id\":null,\"name\":\"Kitchen\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-kitchen\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_location_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_stop_record_video_action\"},\"effect\":\"Deny\",\"dbId\":null,\"timestamp\":1690467841444}";
		mvc.perform(post("/sifis/check")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON).content(rule))
			      .andExpect(status().isOk());
	}
	
	@Test 
	public void testInconsistency() throws Exception {
		int rulesBefore = dbService.getRulesByType("sifis", "ptp-demo").size();

		
		String rule = "{\"subject\":\"marketplace\",\"trigger\":{\"id\":\"sifis_every_afternoon_trigger\",\"description\":\"This trigger fires every afternoon\",\"name\":\"Every Afternoon\",\"service\":{\"id\":\"sifis_time\",\"name\":\"Temporal\",\"color\":\"rgb(84, 89, 95)\",\"webId\":1,\"image\":\"time.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_time\"},\"details\":[{\"id\":\"sifis_hour_from_detail\",\"name\":\"1 - from [hh]\",\"type\":\"value\",\"value\":\"13\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_from_detail\"},{\"id\":\"sifis_hour_to_detail\",\"name\":\"2 - to [hh]\",\"type\":\"value\",\"value\":\"17\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_to_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_every_afternoon_trigger\"},\"action\":{\"id\":\"sifis_stop_record_video_action\",\"name\":\"Stop recording video\",\"description\":\"This action stops the video recording\",\"service\":{\"id\":\"sifis_video\",\"name\":\"Video\",\"color\":\"rgb(255, 58, 58)\",\"webId\":2,\"image\":\"video.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_video\"},\"details\":[{\"id\":\"sifis_location_detail\",\"name\":\"Location\",\"type\":\"location\",\"value\":{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},\"trigger\":null,\"action\":null,\"alternatives\":[{\"id\":null,\"name\":\"Entire Home\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-home\"},{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},{\"id\":null,\"name\":\"Bedroom\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-bedroom\"},{\"id\":null,\"name\":\"Kitchen\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-kitchen\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_location_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_stop_record_video_action\"},\"effect\":\"Deny\",\"dbId\":null,\"timestamp\":1690467841444}";
		MvcResult added = mvc.perform(post("/sifis/rule")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			    .contentType(MediaType.APPLICATION_JSON).content(rule)).andReturn();
		
		String rule2 = "{\"hover\":false,\"subject\":\"marketplace\",\"trigger\":{\"id\":\"sifis_every_afternoon_trigger\",\"description\":\"This trigger fires every afternoon\",\"name\":\"Every Afternoon\",\"service\":{\"id\":\"sifis_time\",\"name\":\"Temporal\",\"color\":\"rgb(84, 89, 95)\",\"webId\":1,\"image\":\"time.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_time\"},\"details\":[{\"id\":\"sifis_hour_from_detail\",\"name\":\"1 - from [hh]\",\"type\":\"value\",\"value\":\"13\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_from_detail\"},{\"id\":\"sifis_hour_to_detail\",\"name\":\"2 - to [hh]\",\"type\":\"value\",\"value\":\"17\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_to_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_every_afternoon_trigger\"},\"action\":{\"id\":\"sifis_record_video_action\",\"name\":\"Record video\",\"description\":\"This action starts the video recording\",\"service\":{\"id\":\"sifis_video\",\"name\":\"Video\",\"color\":\"rgb(255, 58, 58)\",\"webId\":2,\"image\":\"video.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_video\"},\"details\":[{\"id\":\"sifis_location_detail\",\"name\":\"Location\",\"type\":\"location\",\"value\":{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},\"trigger\":null,\"action\":null,\"alternatives\":[{\"id\":null,\"name\":\"Entire Home\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-home\"},{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},{\"id\":null,\"name\":\"Bedroom\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-bedroom\"},{\"id\":null,\"name\":\"Kitchen\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-kitchen\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_location_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_record_video_action\"},\"effect\":\"Deny\",\"dbId\":null,\"timestamp\":1690556346325}";
		MvcResult added2 = mvc.perform(post("/sifis/rule")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			    .contentType(MediaType.APPLICATION_JSON).content(rule2)).andReturn();
		
		ObjectMapper mapper = new ObjectMapper();  


		List<Rule> rules = dbService.getRulesByType("sifis","ptp-demo");

		for(Rule r : rules) {
			if(r.getDbId() == Long.parseLong(added2.getResponse().getContentAsString())) {
				System.out.println("####### CHECK INCONSISTENCY #######");
				mvc.perform(post("/sifis/check")
						.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
					      .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(r)))
					      .andExpect(status().isOk());
			}
		}

		
		dbService.deleteRule(Long.parseLong(added.getResponse().getContentAsString()));
		dbService.deleteRule(Long.parseLong(added2.getResponse().getContentAsString()));

		int rulesAfter = dbService.getRulesByType("sifis", "ptp-demo").size();
		assertThat (rulesBefore == rulesAfter);
	}
	
	@Test 
	public void translatePolicy() throws Exception {
		int rulesBefore = dbService.getRulesByType("sifis", "ptp-demo").size();

		String rule = "{\"hover\":false,\"subject\":\"marketplace\",\"trigger\":{\"id\":\"sifis_every_afternoon_trigger\",\"description\":\"This trigger fires every afternoon\",\"name\":\"Every Afternoon\",\"service\":{\"id\":\"sifis_time\",\"name\":\"Temporal\",\"color\":\"rgb(84, 89, 95)\",\"webId\":1,\"image\":\"time.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_time\"},\"details\":[{\"id\":\"sifis_hour_from_detail\",\"name\":\"1 - from [hh]\",\"type\":\"value\",\"value\":\"13\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_from_detail\"},{\"id\":\"sifis_hour_to_detail\",\"name\":\"2 - to [hh]\",\"type\":\"value\",\"value\":\"17\",\"trigger\":null,\"action\":null,\"alternatives\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_to_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_every_afternoon_trigger\"},\"action\":{\"id\":\"sifis_stop_record_video_action\",\"name\":\"Stop recording video\",\"description\":\"This action stops the video recording\",\"service\":{\"id\":\"sifis_video\",\"name\":\"Video\",\"color\":\"rgb(255, 58, 58)\",\"webId\":2,\"image\":\"video.png\",\"triggers\":null,\"actions\":null,\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_video\"},\"details\":[{\"id\":\"sifis_location_detail\",\"name\":\"Location\",\"type\":\"location\",\"value\":{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},\"trigger\":null,\"action\":null,\"alternatives\":[{\"id\":null,\"name\":\"Entire Home\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-home\"},{\"id\":null,\"name\":\"Living Room\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-livingroom\"},{\"id\":null,\"name\":\"Bedroom\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-bedroom\"},{\"id\":null,\"name\":\"Kitchen\",\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo#location-kitchen\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_location_detail\"}],\"url\":\"http://elite.polito.it/ontologies/sifis-home.owl#sifis_stop_record_video_action\"},\"effect\":\"Deny\",\"dbId\":null,\"timestamp\":1690467841444}";
		MvcResult added = mvc.perform(post("/sifis/rule")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			    .contentType(MediaType.APPLICATION_JSON).content(rule)).andReturn();
		
		List<Rule> rules = dbService.getRulesByType("sifis","ptp-demo");
		Rule policy = rules.get(0);
		ObjectMapper mapper = new ObjectMapper();  
		
		mvc.perform(post("/sifis/translate")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(policy)))
			      .andExpect(status().isOk());
		
		dbService.deleteRule(Long.parseLong(added.getResponse().getContentAsString()));
		
		int rulesAfter = dbService.getRulesByType("sifis", "ptp-demo").size();
		assertThat (rulesBefore == rulesAfter);

	}
	
	@Test 
	public void downloadPolicy() throws Exception {
		mvc.perform(get("/sifis/download")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes())))
				.andExpect(status().isOk());
	}
	
	
	@Test
	public void contextLoads() throws Exception {
		assertThat(true);
	}
}
