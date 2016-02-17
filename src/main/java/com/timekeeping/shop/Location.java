package com.timekeeping.shop;

import javax.persistence.Embeddable;

@Embeddable
public class Location {
	private String city;
	private String street;
	
	public Location(String city, String street) {
		this.city = city;
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location: ").append(city).append(" :: ").append(street);
		return builder.toString();
	}
	
}
