package com.timekeeping.user;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.timekeeping.shop.Shop;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String login;
	private String password;
	@ManyToMany
	@JoinTable(name = "USER_SHOP", 
		joinColumns = @JoinColumn(name = "USER_ID"),
		inverseJoinColumns = @JoinColumn(name = "SHOP_ID"))
	private List<Shop> shops = new LinkedList<>();
	private String roles;

	protected User() {
		
	}

	public User(String login, String password, String roles) {
		this.login = login;
		this.password = password;
		this.roles = roles;
	}
	
	public void addRole(String role) {
		if(!this.roles.contains(role)) {
			this.roles += this.roles.isEmpty() ? role : "," + role;
		}
	}
	
	public void addShop(Shop shop) {
		this.shops.add(shop);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Shop> getShops() {
		return shops;
	}

	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [id: ").append(id).append(", login: ").append(login).append(", roles: ").append(roles)
				.append("]");
		return builder.toString();
	}

}
