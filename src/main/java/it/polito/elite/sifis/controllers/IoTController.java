package it.polito.elite.sifis.controllers;

import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.polito.elite.sifis.entities.db.Dbentity;
import it.polito.elite.sifis.entities.db.User;
import it.polito.elite.sifis.entities.owl.IoTEntity;
import it.polito.elite.sifis.entities.owl.Service;
import it.polito.elite.sifis.services.DBService;
import it.polito.elite.sifis.services.OWLService;
import it.polito.elite.sifis.services.UserService;

@RestController
@CrossOrigin("*")
public class IoTController {
	@Autowired
	OWLService owlService;
	
	@Autowired
	DBService dbService;
	
	@Autowired
	UserService userService;
	/*@GetMapping(value = "/IoTEntity")
	public List<IoTEntity> getIoTEntities(@RequestParam("service") String service, Principal user) throws OWLOntologyCreationException{
		Collection<IoTEntity> IoTEntities = owlService.getIoTEntitiesByService(service, user.getName());
		List<IoTEntity> entityList = new LinkedList<IoTEntity>(IoTEntities);
		return entityList;
	}*/
	
	@PostMapping(value = "/IoTEntity/list")
	public List<IoTEntity> getIoTEntities(@RequestBody List<Service> services, Principal user) throws OWLOntologyCreationException{
		Collection<IoTEntity> IoTEntities = owlService.getIoTEntitiesByServices(services, user.getName());
		List<IoTEntity> entityList = new LinkedList<IoTEntity>(IoTEntities);
		return entityList;
	}
	
	
	@PostMapping(value = "/IoTEntity")
	@ResponseStatus(value = HttpStatus.CREATED)
	public IoTEntity createEntity(@RequestBody IoTEntity entity, Principal user) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException{
		Dbentity dbentity = new Dbentity();
		User u = new User();
		u = userService.findUserByUsername(user.getName());
		dbentity.setUser(u);
		dbentity = dbService.saveEntity(dbentity);
		if(entity.getId() == null){
			entity.setId("IoTEntity" + dbentity.getId().toString());
		}
		if(entity.getType() == null){
			entity.setType("IoTEntity");
		}
		IoTEntity ontoEntity = this.owlService.createEntity(entity, user.getName());
		dbentity.setUrl(ontoEntity.getURL());
		dbService.saveEntity(dbentity);
		return ontoEntity;
	}
	
}
