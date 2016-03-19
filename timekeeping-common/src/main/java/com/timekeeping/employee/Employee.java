package com.timekeeping.employee;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonView;
import com.timekeeping.schedule.ScheduleJsonView;
import com.timekeeping.shop.Shop;

/**
 * JPA entity representing an employee.
 * 
 * @author Mikhail Romanenko
 *
 */

@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(ScheduleJsonView.ScheduleTableView.class)
	private Long id;
	@Column(name = "FIRST_NAME")
	private String firstName;
	@Column(name = "LAST_NAME")
	private String lastName;
	@ManyToOne
	private Shop shop;
	@ManyToOne
	private Position position;
	private boolean active = true;
	@Enumerated(EnumType.STRING)
	private EmploymentType employment;
	
	protected Employee() {
		
	}
	
	public Employee(Employee emp) {
		this(emp.firstName, emp.lastName, emp.shop, emp.position, emp.employment);
	}
	
	public Employee(String firstName, String lastName, Shop shop, Position position, EmploymentType employment) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.shop = shop;
		this.position = position;
		this.employment = employment;
	}

	public Long getId() {
		return this.id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public EmploymentType getEmployment() {
		return employment;
	}

	public void setEmployment(EmploymentType employment) {
		this.employment = employment;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Employee [id: ").append(id).append(", firstName: ").append(firstName).append(", lastName: ").append(lastName)
				.append(", positionID: ").append(position != null ? position.getId() : "null")
				.append(", shopID: ").append(shop != null ? shop.getId() : "null").append(", active: ").append(active).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(active, employment, firstName, lastName, id, position, shop);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (active != other.active)
			return false;
		if (employment != other.employment)
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (shop == null) {
			if (other.shop != null)
				return false;
		} else if (!shop.equals(other.shop))
			return false;
		return true;
	}

}
