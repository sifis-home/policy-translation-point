package it.polito.elite.sifis.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@CrossOrigin("*")
public class Index {
	
	@GetMapping(value = {"/", "/home"})
	public String getIndex() {
	    return "redirect:/index.html";
	}
	
}
