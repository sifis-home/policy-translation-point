package it.polito.elite.sifis.entities.db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Table(name = "dbaction")
public class Dbaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String url;
	@JoinColumn(name = "entity_id")
	@ManyToOne(cascade = CascadeType.MERGE)
	Dbentity entity;
	@JoinColumn(name = "rule_id")
	@ManyToOne(cascade = CascadeType.ALL)
	Dbrule rule;
	@OneToMany(mappedBy = "action", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	List<Dbdetail> details;
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
	public Dbentity getEntity() {
		return entity;
	}
	public void setEntity(Dbentity entity) {
		this.entity = entity;
	}
	public Dbrule getRule() {
		return rule;
	}
	public void setRule(Dbrule rule) {
		this.rule = rule;
	}
	public List<Dbdetail> getDetails() {
		return details;
	}
	public void setDetails(List<Dbdetail> details) {
		this.details = details;
	}
	
}
