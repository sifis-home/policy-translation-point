package it.polito.elite.sifis.entities.db;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dbdetail")
public class Dbdetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String url;
	String value;
	String valueURL;
	String type;
	@JoinColumn(name = "action_id")
	@ManyToOne(cascade = CascadeType.ALL)
	Dbaction action;
	@JoinColumn(name = "trigger_id")
	@ManyToOne(cascade = CascadeType.ALL)
	Dbtrigger trigger;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getValueURL() {
		return valueURL;
	}
	public void setValueURL(String valueURL) {
		this.valueURL = valueURL;
	}
	
	
}
