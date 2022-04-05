package it.polito.elite.sifis.entities.db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "dbrule")
public class Dbrule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@JoinColumn(name = "user_id")
	@ManyToOne(cascade = CascadeType.MERGE)
	User user;
	@OneToMany(mappedBy = "rule", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Dbtrigger> triggers;
	@OneToMany(mappedBy = "rule", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Dbaction> actions;
	private Long timestamp;
	private String type;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Dbtrigger> getTriggers() {
		return triggers;
	}
	public void setTriggers(List<Dbtrigger> triggers) {
		this.triggers = triggers;
	}
	public List<Dbaction> getActions() {
		return actions;
	}
	public void setActions(List<Dbaction> actions) {
		this.actions = actions;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
