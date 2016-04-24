package com.dur.dao;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;


@Entity
public class TestDao {
	
	String name;
	
	String value;
	
	Collection<String> col;
	
	public void printHello(){
		System.out.println("Hello");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@ManyToMany(
			targetEntity=com.dur.model.ClientSession.class
	)
	@JoinTable
	public final Collection<String> getCol() {
		return col;
	}

	public final void setCol(Collection<String> col) {
		this.col = col;
	}
	
	
}
