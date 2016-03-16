package com.timekeeping.shop;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonView;
import com.timekeeping.employee.Employee;
import com.timekeeping.schedule.ScheduleJsonView;
import com.timekeeping.user.User;

/**
 * JPA entity representing shop
 * 
 * @author Mikhail Romanenko
 *
 */

@Entity
public class Shop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(ScheduleJsonView.ScheduleTableView.class)
	private Long id;
	private String name;
	@OneToMany(mappedBy = "shop")
	private List<Employee> employees = new ArrayList<>();
	@Embedded
	private Location location;
	@ManyToMany(mappedBy = "shops")
	private List<User> users = new ArrayList<>();
	
	protected Shop() {
		
	}
	
	public Shop(String name, Location location) {
		this.name = name;
		this.location = location;
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Shop [name: ").append(name).append(", location: ").append(location).append("]");
		return builder.toString();
	}
	
}
