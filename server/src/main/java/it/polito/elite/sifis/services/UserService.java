package it.polito.elite.sifis.services;

import java.io.FileNotFoundException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import it.polito.elite.sifis.entities.db.User;


public interface UserService {
	public User findUserByUsername(String username);
	public void saveUser(User user) throws InterruptedException, OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException;
}
