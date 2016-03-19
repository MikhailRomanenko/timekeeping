package com.timekeeping.shop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import com.timekeeping.employee.Employee;
import com.timekeeping.user.User;

public class ShopBuilder {
	private Long id;
	private String name;
	private List<Employee> employees = new ArrayList<>();
	private Location location;
	private List<User> users = new ArrayList<>();
	
	private ShopBuilder() {
		
	}
	
	public static ShopBuilder shop() {
		return new ShopBuilder();
	}
	
	public Shop build() {
		Shop shop = new Shop(name, location);
		ReflectionTestUtils.setField(shop, "id", id);
		ReflectionTestUtils.setField(shop, "employees", employees);
		ReflectionTestUtils.setField(shop, "users", users);
		return shop;
	}
	
	public ShopBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	public ShopBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public ShopBuilder employees(List<Employee> employees) {
		this.employees = employees;
		return this;
	}
	
	public ShopBuilder addEmployee(Employee employee) {
		this.employees.add(employee);
		return this;
	}
	
	public ShopBuilder location(Location location) {
		this.location = location;
		return this;
	}
	
	public ShopBuilder users(List<User> users) {
		this.users = users;
		return this;
	}
	
	public ShopBuilder addUser(User user) {
		this.users.add(user);
		return this;
	}
}
