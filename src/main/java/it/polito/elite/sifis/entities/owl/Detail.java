package it.polito.elite.sifis.entities.owl;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.polito.elite.sifis.entities.db.Dbaction;
import it.polito.elite.sifis.entities.db.Dbtrigger;

public class Detail {
	
	private String URL;
	private String id;
	private String name;
	private String type;
	private Object value;
	@JoinColumn(name = "triggerId")
	@ManyToOne(cascade = CascadeType.ALL)
	private Dbtrigger trigger;
	@JoinColumn(name = "actionId")
	@ManyToOne(cascade = CascadeType.ALL)
	private Dbaction action;
	private Set<Object> alternatives;
	
	
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Set<Object> getAlternatives() {
		return alternatives;
	}
	public void setAlternatives(Set<Object> alternatives) {
		this.alternatives = alternatives;
	}
	public Dbaction getAction() {
		return action;
	}
	public void setAction(Dbaction action) {
		this.action = action;
	}
	public Dbtrigger getTrigger() {
		return trigger;
	}
	public void setTrigger(Dbtrigger trigger) {
		this.trigger = trigger;
	}
	@JsonIgnore
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((URL == null) ? 0 : URL.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Detail other = (Detail) obj;
		if (URL == null) {
			if (other.URL != null)
				return false;
		} else if (!URL.equals(other.URL))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	@JsonIgnore
	@Override
	public String toString() {
		return "Detail [name=" + name + ", value=" + value + "]";
	}
		
}
