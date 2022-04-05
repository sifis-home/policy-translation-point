package it.polito.elite.sifis.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import it.polito.elite.sifis.entities.db.SecurityUser;
import it.polito.elite.sifis.entities.db.User;



@Component
public class CustomUserDetailsService  implements UserDetailsService {
	
	@Autowired
	private UserService userService;
	
	
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		User user = userService.findUserByUsername(userName);
		if(user == null){
			throw new UsernameNotFoundException("UserName "+userName+" not found");
		}

		return new SecurityUser(user);
	}
	
	 
}
