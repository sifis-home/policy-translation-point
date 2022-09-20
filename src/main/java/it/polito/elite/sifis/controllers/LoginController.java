package it.polito.elite.sifis.controllers;

import java.io.FileNotFoundException;
import java.security.Principal;
import javax.validation.Valid;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.polito.elite.sifis.entities.db.User;
import it.polito.elite.sifis.services.DBService;
import it.polito.elite.sifis.services.OWLService;
import it.polito.elite.sifis.services.UserService;


@RestController
@CrossOrigin("*")
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OWLService owlService;
	
	@Autowired
	private DBService dbService;
	
	
	
	@GetMapping(value = "/user")
	public Principal login(Principal user) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException, InterruptedException {
		/*if(user.getName().equals("sifis-home")) {
			owlService.deleteAllEntities(user.getName());
			dbService.deleteAllRules(user.getName());
			dbService.deleteAllEntities(user.getName());
			owlService.registerDemoPlot();
		}*/
		return user;
	}
	
	@PostMapping(value = "/registration")
	@ResponseStatus(value = HttpStatus.OK)
	public User createNewUser(@Valid @RequestBody User user, BindingResult bindingResult) throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException, InterruptedException {
		IRI userIRI = IRI.create(this.owlService.getBaseIRI().toString() + "/" + user.getUsername());
		user.setOntologyIRI(userIRI.toString());
		owlService.addOntology(user.getUsername(), userIRI);
		userService.saveUser(user);		
		return user;
	}
	

	//@CrossOrigin(origins = "http://localhost:4200") 
	@RequestMapping(value = "/logout", method = RequestMethod.OPTIONS)
	@ResponseStatus(value = HttpStatus.OK)
	public void logoutoptions() {
		System.out.println("OPTIONS");
	}
}