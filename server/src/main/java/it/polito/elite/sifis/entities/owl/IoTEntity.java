package it.polito.elite.sifis.entities.owl;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class IoTEntity {
	
	private String URL;
	private String id;
	private String type;
	private String name;
	private Set<String> services;
	
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
	public Set<String> getServices() {
		return services;
	}
	public void setServices(Set<String> services) {
		this.services = services;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@JsonIgnore
	@Override
	public String toString() {
		return "IoTEntity [URL=" + URL + "]";
	}
	@JsonIgnore
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((URL == null) ? 0 : URL.hashCode());
		return result;
	}
	@JsonIgnore
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IoTEntity other = (IoTEntity) obj;
		if (URL == null) {
			if (other.URL != null)
				return false;
		} else if (!URL.equals(other.URL))
			return false;
		return true;
	}
	
	
	
	
}
