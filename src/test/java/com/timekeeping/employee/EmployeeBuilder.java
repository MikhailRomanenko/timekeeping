package com.timekeeping.employee;

import org.springframework.test.util.ReflectionTestUtils;

import com.timekeeping.shop.Shop;

public final class EmployeeBuilder {

	private Long id = 1L;
	private String firstName = "";
	private String lastName = "";
	private Shop shop;
	private Position position;
	private boolean active = true;
	private EmploymentType employment = EmploymentType.FULL_TIME;
	
	private EmployeeBuilder(){
	}
	
	public static EmployeeBuilder employee() {
		return new EmployeeBuilder();
	}
	
	public Employee build() {
		Employee emp = new Employee(firstName, lastName, shop, position, employment);
		ReflectionTestUtils.setField(emp, "id", id);
		emp.setActive(this.active);
		return emp;
	}
	
	public EmployeeBuilder id(Long id) {
		this.id = id;
		return this;
	}
	
	public EmployeeBuilder firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	
	public EmployeeBuilder lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public EmployeeBuilder id(Shop shop) {
		this.shop = shop;
		return this;
	}
	
	public EmployeeBuilder id(Position position) {
		this.position = position;
		return this;
	}
	
	public EmployeeBuilder active(boolean active) {
		this.active = active;
		return this;
	}
	
	public EmployeeBuilder employment(EmploymentType employment) {
		this.employment = employment;
		return this;
	}
	
}
