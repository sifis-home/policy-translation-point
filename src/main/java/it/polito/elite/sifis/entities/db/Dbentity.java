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
@Table(name = "dbentity")
public class Dbentity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String url;
	
	@JoinColumn(name = "user_id")
	@ManyToOne(cascade = CascadeType.MERGE)
	User user;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
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
	
	
}
