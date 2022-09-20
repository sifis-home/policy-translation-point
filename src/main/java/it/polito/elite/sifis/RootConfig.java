package it.polito.elite.sifis;

import java.io.IOException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import it.polito.elite.sifis.services.CustomUserDetailsService;
import it.polito.elite.sifis.services.DBService;
import it.polito.elite.sifis.services.DBServiceImpl;
import it.polito.elite.sifis.services.OWLService;
import it.polito.elite.sifis.services.OWLServiceImpl;
import it.polito.elite.sifis.services.PetriNetService;
import it.polito.elite.sifis.services.PetriNetServiceImpl;
import it.polito.elite.sifis.services.UserService;
import it.polito.elite.sifis.services.UserServiceImpl;
import it.polito.elite.sifis.services.XACMLService;
import it.polito.elite.sifis.services.XACMLServiceImpl;


@Configuration
public class RootConfig {
	

	@Bean
	public OWLService MyOWLService() throws OWLOntologyCreationException, IOException{
		return new OWLServiceImpl();
	}
	
	@Bean
	public UserService MyUserService() {
		return new UserServiceImpl();
	}
	
	@Bean
	public DBService MyDBService() {
		return new DBServiceImpl();
	}
	
	@Bean
	public XACMLService MyXACMLService() {
		return new XACMLServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
		return bCryptPasswordEncoder;
	}
	
	@Bean
	public UserDetailsService userDetailService() {
		return new CustomUserDetailsService();
	}
	
	@Bean
	public PetriNetService petriNetService() {
		return new PetriNetServiceImpl();
	}
	
	
}
