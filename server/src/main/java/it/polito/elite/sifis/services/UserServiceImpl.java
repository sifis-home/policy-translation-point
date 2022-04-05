package it.polito.elite.sifis.services;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import it.polito.elite.sifis.entities.db.User;
import it.polito.elite.sifis.entities.owl.Role;
import it.polito.elite.sifis.repositories.RoleRepository;
import it.polito.elite.sifis.repositories.UserRepository;


@Service("userService")
public class UserServiceImpl implements UserService{
	
	@Autowired
	OWLService owlService;
	
	@Autowired
	DBService dbService;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
    private RoleRepository roleRepository;
    /*@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;*/
	
	@Override	
	public User findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public void saveUser(User user) throws InterruptedException, OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		//user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setPassword(user.getPassword());
        user.setActive(1);
        Role userRole = roleRepository.findByRole("ADMIN");
        
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
		
	}
}