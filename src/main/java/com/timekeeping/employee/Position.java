package com.timekeeping.employee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * JPA entity representing a position that an employee could take.
 * 
 * @author Mikhail Romanenko
 *
 */

@Entity
public class Position {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private Integer id;
	private String name;
	private String department;
	
	protected Position() {
		
	}

	public Position(String name, String department) {
		this.name = name;
		this.department = department;
	}

	public Integer getId() {
		return id;
	}

	protected void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Position [name: ").append(name).append(", department: ").append(department).append("]");
		return builder.toString();
	}
	
	

}
