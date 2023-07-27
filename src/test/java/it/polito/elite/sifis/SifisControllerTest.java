package it.polito.elite.sifis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.assertj.core.api.Assertions.assertThat;

import it.polito.elite.sifis.controllers.LoginController;
import it.polito.elite.sifis.controllers.SifisController;
import it.polito.elite.sifis.services.DBService;
import it.polito.elite.sifis.services.UserService;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = SifisApplication.class)
@AutoConfigureMockMvc
public class SifisControllerTest {
	
	@Autowired
    private MockMvc mvc;
	
	@Autowired
	private SifisController controller;
	
	@Autowired
	private LoginController loginController;

	@Test 
	public void testLogin() throws Exception {
		mvc.perform(get("/user")
				.header("authorization", "Basic " + Base64.getEncoder().encodeToString("ptp-demo:ptp-demo".getBytes()))
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	@Test 
	public void testPolicy() {
		
	}
	
	@Test
	public void contextLoads() throws Exception {
		
		
		
		assertThat(true);
	}
}
