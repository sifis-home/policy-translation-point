package it.polito.elite.sifis.entities.owl;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Service implements Comparable<Service>{
	private String URL;
	private String id;
	private String name;
	private String color;
	private Long webId;
	private String image;
	private Set<Trigger> triggers;
	private Set<Action> actions;
	
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
	public Set<Trigger> getTriggers() {
		return triggers;
	}
	public void setTriggers(Set<Trigger> triggers) {
		this.triggers = triggers;
	}
	public Set<Action> getActions() {
		return actions;
	}
	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Long getWebId() {
		return webId;
	}
	public void setWebId(Long webId) {
		this.webId = webId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	@JsonIgnore
	@Override
	public int compareTo(Service o) {
		if(this.webId > o.webId)
			return 1;
		else{
			if(this.webId < o.webId)
				return -1;
			else
				return 0;
		}
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
		Service other = (Service) obj;
		if (URL == null) {
			if (other.URL != null)
				return false;
		} else if (!URL.equals(other.URL))
			return false;
		return true;
	}
	
	
	
}
