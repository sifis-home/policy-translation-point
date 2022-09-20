package it.polito.elite.sifis.entities.owl;

import java.util.Set;

public class Technology {
	private String URL;
	private String id;
	private Set<Service> services;
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<Service> getServices() {
		return services;
	}
	public void setServices(Set<Service> services) {
		this.services = services;
	}
	
	
}
